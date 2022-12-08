package cn.authing.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.Authing;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PushAuthRequestButton extends PrimaryButton {

    private static final String TAG = "PushAuthRequestButton";

    public PushAuthRequestButton(Context context) {
        this(context, null);
    }

    public PushAuthRequestButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public PushAuthRequestButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener((v -> login()));
    }

    public void login() {
        if (showLoading) {
            return;
        }

        View v = Util.findViewByClass(this, AccountEditText.class);
        if (v != null) {
            AccountEditText accountEditText = (AccountEditText)v;
            String account = accountEditText.getText().toString();
            if (Validator.isValidPhoneNumber(account)) {
                authRequest("phone", account);
            } else if (Validator.isValidEmail(account)) {
                authRequest("email", account);
            } else {
                authRequest("username", account);
            }
        }
    }

    private void authRequest(String key, String value) {
        startLoadingVisualEffect();
        new Thread() {
            @Override
            public void run() {
                _authRequest(key, value);
            }
        }.start();
    }

    private void _authRequest(String key, String value) {
        Authing.getPublicConfig(config -> {
            if (config == null) {
                ALog.w(TAG, "push auth request failed. uninitialized");
                return;
            }
            ALog.i(TAG, "sending push auth request to server");
            Request.Builder builder = new Request.Builder();
            builder.url(Push.BASE_URL + "/ams/push/auth-request");
            builder.addHeader("x-authing-app-id", Authing.getAppId());
            builder.addHeader("x-authing-userpool-id", config.getUserPoolId());
            String body = "{\"key\":\"" + key + "\", \"value\":\"" + value + "\"}";
            builder.post(RequestBody.create(body, Const.JSON));

            Request request = builder.build();
            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            okhttp3.Response response;
            try {
                response = call.execute();
                stopLoadingVisualEffect();
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                if (response.code() == 201 || response.code() == 200) {
                    ALog.i(TAG, "push auth request success");
                    authRequestDone(config, s);
                } else {
                    ALog.e(TAG, "push auth request failed:" + s);
                    post(()-> Toast.makeText(getContext(), "认证请求发送失败。请确认已在受信设备上登录", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                ALog.e(TAG, "push auth request failed:", e);
            }
        });
    }

    private void authRequestDone(Config config, String s) {
        post(()-> Toast.makeText(getContext(), "认证请求已发送。请到受信设备上确认", Toast.LENGTH_LONG).show());
        try {
            JSONObject json = new JSONObject(s);
            if (json.has("data")) {
                JSONObject data = json.getJSONObject("data");
                if (data.has("sessionId")) {
                    String sessionId = data.getString("sessionId");
                    authPoll(System.currentTimeMillis(), config, sessionId);
                } else {
                    ALog.e(TAG, "push auth request failed with no session:" + s);
                }
            } else {
                ALog.e(TAG, "push auth request failed with no session:" + s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void authPoll(long startedTime, Config config, String sessionId) {
        if (System.currentTimeMillis() - startedTime > 5 * 60 * 1000) {
            ALog.i(TAG, "push auth poll timeout");
            Util.setErrorText(this, "push auth poll timeout");
            return;
        }

        ALog.i(TAG, "push auth poll retry " + startedTime);

        Request.Builder builder = new Request.Builder();
        builder.url(Push.BASE_URL + "/ams/push/auth-poll?sessionId=" + sessionId);
        builder.addHeader("x-authing-app-id", Authing.getAppId());
        builder.addHeader("x-authing-userpool-id", config.getUserPoolId());

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            stopLoadingVisualEffect();
            String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            if (response.code() == 201 || response.code() == 200) {
                ALog.i(TAG, "push auth poll success");
                getUserInfo(s);
            } else {
                ALog.i(TAG, "push auth poll ongoing:" + sessionId);
                Thread.sleep(3000);
                authPoll(startedTime, config, sessionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ALog.e(TAG, "push auth poll failed:", e);
            authPoll(startedTime, config, sessionId);
        }
    }

    private void getUserInfo(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if (json.has("data")) {
                JSONObject data = json.getJSONObject("data");
                if (data.has("idToken")) {
                    String idToken = data.getString("idToken");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setIdToken(idToken);
                    Authing.saveUser(userInfo);
                    AuthClient.getCurrentUser(userInfo, ((code, message, us) -> {
                        if (code == 200) {
                            authSuccess();
                        } else {
                            ALog.e(TAG, "getUserInfo failed:" + message);
                            Util.setErrorText(this, message);
                        }
                    }));
                } else {
                    ALog.e(TAG, "getUserInfo no id token:" + s);
                }
            } else {
                ALog.e(TAG, "getUserInfo no id token:" + s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void authSuccess() {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            Intent intent = new Intent();
            activity.setResult(AuthActivity.OK, intent);
            activity.finish();
        }
    }
}
