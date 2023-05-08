package cn.authing.guard.network;


import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {

    private static final String TAG = "WebSocketClient";
    private static WebSocketClient INSTANCE;
    private OkHttpClient client;
    private okhttp3.WebSocket webSocket;
    private Receiver mReceiver;

    private WebSocketClient(Receiver receiver) {
        this.mReceiver = receiver;
    }

    public static WebSocketClient getInstance(Receiver receiver) {
        if (INSTANCE == null) {
            synchronized (WebSocketClient.class) {
                INSTANCE = new WebSocketClient(receiver);
            }
        }
        return INSTANCE;
    }

    public void connect(String wsUrl) {
        Request request = new Request.Builder()
                .url(wsUrl)
                .build();
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//允许失败重试
                .readTimeout(50, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(50, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
                .pingInterval(40, TimeUnit.SECONDS)
                .build();
        webSocket = client.newWebSocket(request, this);
        //内存不足时释放
        client.dispatcher().executorService().shutdown();
    }

    public void reConnect() {
        if (webSocket != null) {
            webSocket = client.newWebSocket(webSocket.request(), this);
        }
    }

    public void send(String text) {
        if (webSocket != null) {
            webSocket.send(text);
        }
    }

    public void cancel() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }

    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        if (mReceiver != null) {
            mReceiver.onOpen();
        }
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        if (mReceiver != null) {
            mReceiver.onReceiverMessage(text);
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
        if (mReceiver != null) {
            mReceiver.onError(t.getMessage());
        }
    }

    public void setReceiver(Receiver callBack) {
        mReceiver = callBack;
    }

    public void removeReceiver() {
        mReceiver = null;
    }
}


