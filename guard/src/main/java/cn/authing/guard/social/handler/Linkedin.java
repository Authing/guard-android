package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.social.linkedin.LinkedInBuilder;
import cn.authing.guard.social.linkedin.helpers.LinkedInUser;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Linkedin extends SocialAuthenticator {

    private static final String TAG = "Linkedin";
    private String appKey; // 应用的APP_KEY
    private String redirectUrl; // 应用的回调页
    public static final int LINKEDIN_REQUEST = 99;
    private AuthCallback<UserInfo> callback;

    private Linkedin() {
    }

    public static Linkedin getInstance() {
        return LinkedinInstanceHolder.mInstance;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LINKEDIN_REQUEST && data != null) {
            if (resultCode == Activity.RESULT_OK && data.hasExtra("social_login")) {
                ALog.i(TAG, "Auth onSuccess");
                LinkedInUser user = data.getParcelableExtra("social_login");
                if (user != null && user.getCode() != null) {
                    login(activity, user.getCode(), callback);
                }
            } else {
                ALog.e(TAG, "Auth Failed, onError errorCode = " + data.getIntExtra("err_code", 0)
                        + " errorMsg = " + data.getStringExtra("err_message"));
                callback.call(Const.ERROR_CODE_10015, "Login by Linkedin failed", null);
            }
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (appKey == null && config != null) {
                appKey = config.getSocialClientId(Const.EC_TYPE_LINKEDIN);
            }
            if (redirectUrl == null && config != null) {
                redirectUrl = config.getSocialRedirectUrl(Const.EC_TYPE_LINKEDIN);
            }
            LinkedInBuilder.getInstance((Activity) context)
                    .setClientID(appKey)
                    .setRedirectURI(redirectUrl)
                    .authenticate(LINKEDIN_REQUEST);
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByLinkedin(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByLinkedin(authCode, callback);
    }

    private static final class LinkedinInstanceHolder {
        static final Linkedin mInstance = new Linkedin();
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
