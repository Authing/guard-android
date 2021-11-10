package cn.authing.guard;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.authing.guard.data.Config;
import cn.authing.guard.network.Guardian;

public class Authing {

    private final static String TAG = "Authing";

    private static Context sAppContext;
    private static String sAppId;
    private static Config publicConfig = new Config();

    public static void init(final Context context, String appId) {
        sAppContext = context.getApplicationContext();
        sAppId = appId;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppId() {
        return sAppId;
    }

    public static Config getPublicConfig() {
        return publicConfig;
    }

    public static void requestPublicConfig(Callback<Config> callback) {
        new Thread() {
            public void run() {
                _requestPublicConfig(callback);
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

    private static void _requestPublicConfig(Callback<Config> callback) {
        String url = "https://console.authing.cn/api/v2/applications/" + sAppId + "/public-config";
        Guardian.get(url, (response)->{
            try {
                if (response.getCode() == 200) {
                    JSONObject data = response.getData();
                    publicConfig = Config.parse(data);
                    if (callback != null) {
                        callback.call(true, publicConfig);
                    }
                } else {
                    Log.d(TAG, "Get public config failed for appId: " + sAppId + " Msg:" + response.getMessage());
                    if (callback != null) {
                        callback.call(false, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.call(false, null);
                }
            }
        });
    }

    private static void _logout(Callback<Object> callback) {
        if (callback != null) {
            callback.call(true, null);
        }
    }
}
