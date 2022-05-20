package cn.authing.push;

import android.content.Context;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.push.huawei.HuaweiPush;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Push {

    private static final String TAG = "Push";

//    public static final String BASE_URL = "http://192.168.100.100:9005";
    public static final String BASE_URL = "https://developer-beta.authing.cn";

    public void registerDevice(Context context) {
        HuaweiPush.registerDevice(context);
    }

    public static void unregister(Context context, Callback<String> callback) {
        new Thread() {
            @Override
            public void run() {
                HuaweiPush.getHuaweiToken(context, ((ok, token) -> unregister(token, callback)));
            }
        }.start();
    }

    private static void unregister(String token, Callback<String> callback) {
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            ALog.w(TAG, "push not registered. user not logged in");
            callback.call(false, null);
            return;
        }

        Authing.getPublicConfig(config -> {
            if (config == null) {
                ALog.w(TAG, "push registered failed. uninitialized");
                callback.call(false, null);
                return;
            }

            ALog.i(TAG, "unregister push token:" + token);
            Request.Builder builder = new Request.Builder();
            builder.url(Push.BASE_URL + "/ams/push/unregister");
            builder.addHeader("x-authing-app-id", Authing.getAppId());
            builder.addHeader("x-authing-userpool-id", config.getUserPoolId());
            builder.addHeader("authorization", "Bearer " + userInfo.getIdToken());
            String body = "{\"token\":\"" + token + "\"}";
            builder.post(RequestBody.create(body, Const.JSON));

            Request request = builder.build();
            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            okhttp3.Response response;
            try {
                response = call.execute();
                if (response.code() == 201 || response.code() == 200) {
                    ALog.i(TAG, "unregister token success");
                    callback.call(true, null);
                } else {
                    String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                    ALog.e(TAG, "unregister token failed:" + s);
                    callback.call(false, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ALog.e(TAG, "unregister token exception:", e);
                callback.call(false, null);
            }
        });
    }

    public static void authConfirm(String sessionId, Callback<String> callback) {
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            ALog.w(TAG, "user not logged in");
            return;
        }

        Authing.getPublicConfig(config -> {
            if (config == null) {
                ALog.w(TAG, "push failed. uninitialized");
                return;
            }
            new Thread() {
                @Override
                public void run() {
                    _authConfirm(userInfo, config, sessionId, callback);
                }
            }.start();
        });
    }

    private static void _authConfirm(UserInfo userInfo, Config config, String sessionId, Callback<String> callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + "/ams/push/auth-confirm?sessionId=" + sessionId);
        builder.addHeader("authorization", "Bearer " + userInfo.getIdToken());
        builder.addHeader("x-authing-app-id", Authing.getAppId());
        builder.addHeader("x-authing-userpool-id", config.getUserPoolId());

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() == 201 || response.code() == 200) {
                ALog.i(TAG, "auth confirm success");
                callback.call(true, null);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                ALog.e(TAG, "auth confirm failed:" + s);
                callback.call(false, s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ALog.e(TAG, "auth confirm failed:", e);
            callback.call(false, e.toString());
        }
    }
}
