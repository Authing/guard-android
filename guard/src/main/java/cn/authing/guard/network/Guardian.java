package cn.authing.guard.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.Config;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Guardian {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "Guardian";

    public interface GuardianCallback {
        void call(Response response);
    }

    public static void get(String url, GuardianCallback callback) {
        new Thread() {
            public void run() {
                send(url, "get", null, callback);
            }
        }.start();
    }

    public static void post(String url, JSONObject body, GuardianCallback callback) {
        new Thread() {
            public void run() {
                send(url, "post", body, callback);
            }
        }.start();
    }

    private static void send(String url, String method, JSONObject body, GuardianCallback callback) {
        Config config = Authing.getPublicConfig();
        if (config == null) {
            fireCallback(callback, null);
            return;
        }

        Request request;
        if (method.equals("post")) {
            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            request = new Request.Builder().url(url)
                    .addHeader("x-authing-userpool-id", config.getUserPoolId())
                    .post(requestBody).build();
        } else {
            request = new Request.Builder().url(url)
                    .addHeader("x-authing-userpool-id", config.getUserPoolId())
                    .build();
        }
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() == 201 || response.code() == 200) {
                Response resp = new Response();
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(s);
                int code;
                try {
                    code = json.getInt("code");
                    resp.setCode(code);
                    String message = json.getString("message");
                    resp.setMessage(message);
                    if (code == 200) {
                        JSONObject data = json.getJSONObject("data");
                        resp.setData(data);
                    } else {
                        Log.i(TAG, "Post failed for:" + url + " msg:" + json);
                    }
                } catch (JSONException je) {
                    // when success for some api, there is no 'code' field
                    resp.setCode(200);
                    resp.setData(json);
                }

                fireCallback(callback, resp);
            } else {
                Log.w(TAG, response.code() + " Post failed for:" + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fireCallback(callback, null);
        }
    }

    private static void fireCallback(GuardianCallback callback, Response data) {
        if (callback != null) {
            callback.call(data);
        }
    }
}
