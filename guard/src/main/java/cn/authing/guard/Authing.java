package cn.authing.guard;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.authing.guard.data.Config;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.Guardian;

public class Authing {

    private final static String TAG = "Authing";

    private static Context sAppContext;
    private static String sAppId;
    private static boolean isGettingConfig;
    private static Config publicConfig;

    private static final Queue<Config.ConfigCallback> listeners = new ConcurrentLinkedQueue<>();

    public static void init(final Context context, String appId) {
        sAppContext = context.getApplicationContext();
        sAppId = appId;
        requestPublicConfig();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppId() {
        return sAppId;
    }

    public static void getPublicConfig(Config.ConfigCallback callback) {
        // add listener first. otherwise callback might be fired in the other thread
        // and this listener is missed
        if (isGettingConfig) {
            listeners.add(callback);
        }

        if (publicConfig != null) {
            listeners.clear();
            callback.call(publicConfig);
        }
    }

    private static void requestPublicConfig() {
        isGettingConfig = true;
        new Thread() {
            public void run() {
                _requestPublicConfig();
            }
        }.start();
    }

    public static void logout(Callback<Object> callback) {
        new Thread() {
            public void run() {
                _logout(callback);
            }
        }.start();
    }

    private static void _requestPublicConfig() {
        String url = "https://console.authing.cn/api/v2/applications/" + sAppId + "/public-config";
        Guardian.request(null, url, "get", null, (response)->{
            try {
                if (response.getCode() == 200) {
                    JSONObject data = response.getData();
                    publicConfig = Config.parse(data);
                    fireCallback(publicConfig);
                } else {
                    Log.d(TAG, "Get public config failed for appId: " + sAppId + " Msg:" + response.getMessage());
                    fireCallback(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fireCallback(null);
            }
        });
    }

    private static void fireCallback(Config config) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->{
            for (Config.ConfigCallback callback : listeners) {
                callback.call(config);
            }
            listeners.clear();
            isGettingConfig = false;
        });
    }

    private static void _logout(Callback<Object> callback) {
        if (callback != null) {
            callback.call(true, null);
        }
    }

    public static void loginByAccount(String account, String password, AuthClient.AuthCallback callback) {
        AuthClient.loginByAccount(account, password, callback);
    }
}
