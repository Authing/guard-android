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
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Guardian {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "Guardian";

    public static String MFA_TOKEN;

    public interface GuardianCallback {
        void call(@NotNull Response response);
    }

    public static void get(String url, @NotNull GuardianCallback callback) {
        request(url, "get", null, callback);
    }

    public static void post(String url, JSONObject body, @NotNull GuardianCallback callback) {
        request(url, "post", body, callback);
    }

    private static void request(String url, String method, JSONObject body, @NotNull GuardianCallback callback) {
        Authing.getPublicConfig(config -> new Thread() {
            public void run() {
                 request(config, url, method, body, callback);
            }
        }.start());
    }

    public static void request(Config config, String url, String method, JSONObject body, @NotNull GuardianCallback callback) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (config != null && config.getUserPoolId() != null) {
            builder.addHeader("x-authing-userpool-id", config.getUserPoolId());
        }
        builder.addHeader("x-authing-app-id", Authing.getAppId());
        builder.addHeader("x-authing-request-from", "Guard@Android@" + SDK_VERSION);
        builder.addHeader("x-authing-lang", Util.getLangHeader());
        UserInfo currentUser = Authing.getCurrentUser();
        if (currentUser != null) {
            String token = currentUser.getIdToken();
            if (!Util.isNull(token)) {
                builder.addHeader("Authorization", "Bearer " + token);
            }
        } else if (MFA_TOKEN != null) {
            builder.addHeader("Authorization", "Bearer " + MFA_TOKEN);
        }
        if (method.equals("post")) {
            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            builder.post(requestBody);
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() == 201 || response.code() == 200) {
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
                        Boolean data = json.getBoolean("data");
                        JSONObject booleanResult = new JSONObject();
                        booleanResult.put("result", data);
                        resp.setData(booleanResult);
                    } catch(JSONException ignored){
                    }
                } else {
                    Log.w(TAG, "Response has no data:" + url + " msg:" + json);
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
                Log.w(TAG, response.code() + " Guardian failed for:" + url);
                callback.call(new Response(response.code(), "Network Error", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(new Response(500, "Network Exception", null));
        }
    }
}
