package cn.authing.guard.network;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {

    private static final String TAG = "WebSocketClient";
    private static WebSocketClient INSTANCE;
    private boolean isReceivePong;
    private HashMap<String, Receiver> receivers;
    private HashMap<String, WebSocket> webSockets;

    private boolean isHart;
    private static final int RECEIVE_PONG = 10;
    private static final String RECEIVE_HART = "Heartbeat";

    private WebSocketClient() {
        this.receivers = new HashMap<>();
        this.webSockets = new HashMap<>();
    }

    public static WebSocketClient getInstance() {
        if (INSTANCE == null) {
            synchronized (WebSocketClient.class) {
                INSTANCE = new WebSocketClient();
            }
        }
        return INSTANCE;
    }

    public void connect(String wsUrl, Receiver receiver, boolean isHart) {
        this.isHart = isHart;
        if (receivers == null) {
            receivers = new HashMap<>();
        }
        if (!receivers.containsKey(wsUrl)) {
            receivers.put(wsUrl, receiver);
        }
        if (isHart){
            heartHandler.removeCallbacksAndMessages(null);
        }
        cancel(wsUrl);

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//允许失败重试
                .readTimeout(50, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(50, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                .pingInterval(40, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();
        WebSocket webSocket = client.newWebSocket(request, this);
        if (webSockets == null) {
            webSockets = new HashMap<>();
        }
        if (!webSockets.containsKey(wsUrl)) {
            webSockets.put(wsUrl, webSocket);
        } else {
            webSockets.replace(wsUrl, webSocket);
        }
        //内存不足时释放
        client.dispatcher().executorService().shutdown();
    }

    /**
     * 发送心跳包
     */
    Handler heartHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what != RECEIVE_PONG) return false;
            if (isReceivePong) {
                String wsUrl = (String) msg.obj;
                send(wsUrl, RECEIVE_HART);
                //isReceivePong置false，等待服务器返回心跳时置ture，如果服务器没有返回则表示连接断开
                isReceivePong = false;
                heartHandler.sendEmptyMessageDelayed(RECEIVE_PONG, 60000);
            } else {
                //没有收到pong命令，进行重连
                if (receivers != null) {
                    for (String url : receivers.keySet()) {
                        Receiver receiver = receivers.get(url);
                        connect(url, receiver, isHart);
                    }
                }
            }
            return false;
        }
    });

    public void send(String wsUrl, String message) {
        if (webSockets != null) {
            WebSocket webSocket = webSockets.get(wsUrl);
            if (webSocket != null) {
                webSocket.send(message);
            }
        }
    }

    public void cancel(String wsUrl) {
        if (webSockets != null) {
            WebSocket webSocket = webSockets.get(wsUrl);
            if (webSocket != null) {
                webSocket.cancel();
            }
        }
    }

    public void close() {
        heartHandler.removeCallbacksAndMessages(null);
        if (webSockets != null) {
            for (String url : webSockets.keySet()) {
                WebSocket webSocket = webSockets.get(url);
                if (webSocket != null) {
                    webSocket.close(1000, null);
                }
            }
            webSockets.clear();
            webSockets = null;
        }
        if (receivers != null) {
            receivers.clear();
        }
        receivers = null;
    }

    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        String wsUrl = getWsUrl(webSocket);
        if (receivers != null) {
            Receiver receiver = receivers.get(wsUrl);
            if (receiver != null) {
                receiver.onOpen();
            }
        }

        if (isHart) {
            //主动发送心跳包
            isReceivePong = true;
            //开启心跳
            Message message = Message.obtain();
            message.what = RECEIVE_PONG;
            message.obj = wsUrl;
            heartHandler.sendMessage(message);
            //测试发消息
            send(wsUrl, RECEIVE_HART);
        }
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        // 收到服务端发送来的 String 类型消息
        if (text.equals(RECEIVE_HART)) {
            isReceivePong = true;
            return;
        }

        if (receivers != null) {
            String wsUrl = getWsUrl(webSocket);
            Receiver receiver = receivers.get(wsUrl);
            if (receiver != null) {
                receiver.onReceiverMessage(text);
            }
        }
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
    }

    @Override
    public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
        if (receivers != null) {
            String wsUrl = getWsUrl(webSocket);
            Receiver receiver = receivers.get(wsUrl);
            if (receiver != null) {
                receiver.onError(t.getMessage());
            }
        }
    }

    private String getWsUrl(WebSocket webSocket) {
        HttpUrl httpUrl = webSocket.request().url();
        return httpUrl.url().toString().replace("https", "wss");
    }

}


