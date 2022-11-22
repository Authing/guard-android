package cn.authing.guard.network;

import static cn.authing.guard.util.Const.SDK_VERSION;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Guardian {

    private static final String TAG = "Guardian";

    public static String MFA_TOKEN;

    public interface GuardianCallback {
        void call(@NotNull Response response);
    }

    public static void get(String endpoint, @NotNull GuardianCallback callback) {
        request(endpoint, "GET", null, callback);
    }

    public static void post(String endpoint, JSONObject body, @NotNull GuardianCallback callback) {
        request(endpoint, "POST", body.toString(), callback);
    }

    public static void delete(String endpoint, @NotNull GuardianCallback callback) {
        request(endpoint, "DELETE", null, callback);
    }

    private static void request(String url, String method, String body, @NotNull GuardianCallback callback) {
        Authing.getPublicConfig(config -> {
            if (config == null){
                callback.call(new Response(Const.ERROR_CODE_10002, "Config not found", null));
                return;
            }

            request(config, url, method, body, callback);
        });
    }

    public static void request(Config config, String url, String method, String body, @NotNull GuardianCallback callback) {
        new Thread() {
            public void run() {
                _request(config, url, method, body, callback);
            }
        }.start();
    }

    private static void _request(Config config, String endpoint, String method, String body, @NotNull GuardianCallback callback) {
        String url;
        if (config == null) {
            url = endpoint;
        } else {
            url = Authing.getScheme() + "://" + Util.getHost(config) + endpoint;
        }

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (config != null) {
            if (config.getUserPoolId() != null){
                builder.addHeader("x-authing-userpool-id", config.getUserPoolId());
            }
            if (!Util.isNull(config.getUserAgent())){
                builder.removeHeader("User-Agent");
                builder.addHeader("User-Agent", config.getUserAgent());
            }
        }
        builder.addHeader("x-authing-app-id", Authing.getAppId());
        builder.addHeader("x-authing-request-from", Const.SDK_TAG + SDK_VERSION);
        builder.addHeader("x-authing-lang", Util.getLangHeader());
        UserInfo currentUser = Authing.getCurrentUser();
        if (currentUser != null) {
            String token = currentUser.getIdToken();
            if (!Util.isNull(token)) {
                builder.addHeader("Authorization", "Bearer " + token);
            } else {
                token = currentUser.getAccessToken();
                if (!Util.isNull(token)) {
                    builder.addHeader("Authorization", "Bearer " + token);
                }
            }
        } else if (MFA_TOKEN != null) {
            builder.addHeader("Authorization", "Bearer " + MFA_TOKEN);
        }
        if (null != body) {
            MediaType type = (body.startsWith("{") || body.startsWith("[")) && (body.endsWith("]") || body.endsWith("}")) ? Const.JSON : Const.FORM;
            RequestBody requestBody = RequestBody.create(body, type);
            builder.method(method.toUpperCase(), requestBody);
        } else {
            builder.method(method.toUpperCase(), null);
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() == 201 || response.code() == 200) {
                CookieManager.addCookies(response);

                Response resp = new Response();
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                JSONObject json;
                try {
                    json = new JSONObject(s);
                } catch (JSONException e) {
                    // some api returns array directly
                    json = new JSONObject();
                    json.put("result", new JSONArray(s));
                    resp.setCode(200);
                    resp.setData(json);
                }

                int code;
                try {
                    if (json.has("code")) {
                        code = json.getInt("code");
                        resp.setCode(code);
                    }
                } catch (JSONException je) {
                    // when success for some api, there is no 'code' field
                    resp.setCode(200);
                    resp.setData(json);
                }

                try {
                    String message = json.getString("message");
                    resp.setMessage(message);
                } catch (JSONException ignored) {
                }

                if (json.has("data")) {
                    try {
                        JSONObject data = json.getJSONObject("data");
                        resp.setData(data);
                    } catch(JSONException ignored){
                    }

                    try {
                        JSONArray array = json.getJSONArray("data");
                        JSONObject data = new JSONObject();
                        data.put("data", array);
                        resp.setData(data);
                    } catch(JSONException ignored){
                    }

                    try {
                        Boolean data = json.getBoolean("data");
                        JSONObject booleanResult = new JSONObject();
                        booleanResult.put("result", data);
                        resp.setData(booleanResult);
                    } catch(JSONException ignored){
                    }
                } else {
                    if (!json.has("code")) {
                        resp.setCode(200);
                        resp.setData(json);
                    }
                }

                // TODO
                if (json.has("recoveryCode")) {
                    String rc = json.getString("recoveryCode");
                    resp.getData().put("recoveryCode", rc);
                }

                callback.call(resp);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                Log.w(TAG, "Guardian failed. " + response.code() + " message:" + s);
                callback.call(new Response(response.code(), s, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(new Response(Const.ERROR_CODE_10001, "Network error", null));
        }
    }

    public static void authRequest(String url, String method, RequestBody body, @NotNull GuardianCallback callback) {
        new Thread() {
            public void run() {
                _authRequest(url, method, body, callback);
            }
        }.start();
    }

    public static void _authRequest(String url, String method, RequestBody body, @NotNull GuardianCallback callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.addHeader("x-authing-request-from", Const.SDK_TAG + SDK_VERSION);
        builder.addHeader("x-authing-lang", Util.getLangHeader());
        if (method.equalsIgnoreCase("post")) {
            builder.post(body);
        } else {
            builder.get();
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() == 201 || response.code() == 200) {
                CookieManager.addCookies(response);
                Response resp = new Response();
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                JSONObject json;
                try {
                    json = new JSONObject(s);
                    resp.setCode(200);
                    resp.setData(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.call(resp);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                Log.w(TAG, "authRequest failed. " + response.code() + " message:" + s);
                callback.call(new Response(response.code(), s, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(new Response(Const.ERROR_CODE_10001, "Network error", null));
        }
    }
}
