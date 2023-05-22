package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;
import cn.authing.guard.webauthn.WebAuthNAuthentication;

public class Finger extends SocialAuthenticator {

    private static final String TAG = "Finger";

    /**
     * 通用跳转授权业务 Demo
     */
    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        if (!(context instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) context;
        WebAuthNAuthentication webAuthNAuthentication = new WebAuthNAuthentication(activity,
                new WebAuthNAuthentication.WebAuthNAuthenticationCallBack() {

                    @Override
                    public void onSuccess(int code, String message, JSONObject data) {
                        success(activity, code, message, data);
                    }

                    @Override
                    public void onFailed(int code, String message) {
                        if (!TextUtils.isEmpty(message) && (message.contains("CancelledException")
                                || message.contains("TimeoutException")) || message.contains("UnknownException")) {
                            return;
                        }

                        if (code == Const.EC_ACCOUNT_LOCKED) {
                            showToast(context, context.getString(R.string.authing_account_locked));
                        } else if (code == Const.EC_NO_DEVICE_PERMISSION_DISABLED) {
                            showToast(context, context.getString(R.string.authing_device_deactivated));
                        } else if (code == Const.EC_NO_DEVICE_PERMISSION_SUSPENDED) {
                            showToast(context, message);
                        } else if (code == Const.ERROR_CODE_10011 || code == Const.EC_422) {
                            FlowHelper.handleBiometricAccountBind(activity);
                        } else {
                            ((Activity)context).runOnUiThread(() -> ToastUtil.showCenter(context, message));
                        }
                    }
                });
        webAuthNAuthentication.startAuthentication();
        activity.setWebAuthNAuthentication(webAuthNAuthentication);
    }

    private void showToast(Context context, String message){
        ((Activity)context).runOnUiThread(() -> ToastUtil.showCenterWarning(context, message));
    }

    private void success(AuthActivity activity, int code, String message, JSONObject data) {
        if (code == 200 && data != null) {
            try {
                if (data.has("tokenSet")) {
                    JSONObject tokenSet = data.getJSONObject("tokenSet");
                    String idToken = "";
                    if (tokenSet.has("id_token")) {
                        idToken = tokenSet.getString("id_token");
                    }
                    String access_token = "";
                    if (tokenSet.has("access_token")) {
                        access_token = tokenSet.getString("access_token");
                    }
                    String refresh_token = "";
                    if (tokenSet.has("refresh_token")) {
                        refresh_token = tokenSet.getString("refresh_token");
                    }
                    UserInfo userInfo = new UserInfo();
                    userInfo.setIdToken(idToken);
                    userInfo.setAccessToken(access_token);
                    userInfo.setRefreshToken(refresh_token);
                    Authing.saveUser(userInfo);
                    if (TextUtils.isEmpty(refresh_token)) {
                        AuthClient.getCurrentUser((AuthCallback<UserInfo>) (i, s, userInfo1) -> {
                            onResult(activity, i, s, userInfo1);
                        });
                    } else {
                        OIDCClient oidcClient = new OIDCClient();
                        oidcClient.getUserInfoByAccessToken(Authing.getCurrentUser(), (AuthCallback<UserInfo>) (code1, message1, data1) -> {
                            onResult(activity, code1, message1, data1);
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            activity.runOnUiThread(() -> ToastUtil.showCenter(activity, message));
        }
    }

    private void onResult(AuthActivity activity, int code, String message, UserInfo data){
        if (code == 200) {
            activity.runOnUiThread(() -> {
                Intent intent = new Intent();
                intent.putExtra("user", data);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
                Util.quitActivity();
            });
        } else {
            Safe.logoutUser(Authing.getCurrentUser());
            Authing.setCurrentUser(null);
            activity.runOnUiThread(() -> ToastUtil.showCenter(activity, message));
        }
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }
}
