package cn.authing.guard;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.data.Config;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
            if (response.code() == 200) {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(s);
                int code = json.getInt("code");
                if (code != 200) {
                    Log.d(TAG, "Get public config failed for appId: " + sAppId + " Msg:" + s);
                    if (callback != null) {
                        callback.call(false, null);
                    }
                    return;
                }

                JSONObject data = json.getJSONObject("data");
                publicConfig = Config.parse(data);

                if (callback != null) {
                    callback.call(true, publicConfig);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.call(false, null);
            }
        }
    }

    private static void _logout(Callback<Object> callback) {
        if (callback != null) {
            callback.call(true, null);
        }
    }
}
