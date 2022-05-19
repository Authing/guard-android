package cn.authing.push.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.Authing;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;
import cn.authing.push.Push;
import cn.authing.push.R;
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
                if (response.code() == 201 || response.code() == 200) {
                    ALog.i(TAG, "push auth request success");
                    authRequestDone();
                } else {
                    String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                    ALog.e(TAG, "push auth request failed:" + s);
                    post(()-> Toast.makeText(getContext(), "认证请求发送失败。请确认已在受信设备上登录", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                ALog.e(TAG, "push auth request failed:", e);
            }
        });
    }

    private void authRequestDone() {
        post(()-> Toast.makeText(getContext(), "认证请求已发送。请到受信设备上确认", Toast.LENGTH_LONG).show());
    }
}
