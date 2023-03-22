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
import cn.authing.guard.social.web.WebAuthBuilder;
import cn.authing.guard.social.web.helpers.WebAuthUser;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class GitLab extends SocialAuthenticator {

    private static final String TAG = "GitLab";
    private String appId;
    private String redirectUrl;
    private String scope = "read_user openid profile email";
    private AuthCallback<UserInfo> callback;

    private GitLab() {
    }

    public static GitLab getInstance() {
        return GitLibInstanceHolder.mInstance;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Const.GITLAB_REQUEST && data != null) {
            if (resultCode == Activity.RESULT_OK && data.hasExtra("social_login")) {
                ALog.i(TAG, "Auth onSuccess");
                WebAuthUser user = data.getParcelableExtra("social_login");
                if (user != null && user.getCode() != null) {
                    login(user.getCode(), callback);
                }
            } else {
                ALog.e(TAG, "Auth Failed, onError errorCode = " + data.getIntExtra("err_code", 0)
                        + " errorMsg = " + data.getStringExtra("err_message"));
                if (callback != null) {
                    callback.call(Const.ERROR_CODE_10020, "Login by GitLab failed", null);
                }
            }
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (appId == null && config != null) {
                appId = config.getSocialClientId(Const.EC_TYPE_GITLAB);
            }
            if (redirectUrl == null && config != null) {
                redirectUrl = config.getSocialRedirectUrl(Const.EC_TYPE_GITLAB);
            }
            WebAuthBuilder.getInstance((Activity) context)
                    .setAuthorizationUrl("https://gitlab.com/oauth/authorize")
                    .setAccessTokenUrl("https://gitlab.com/oauth/token")
                    .setClientID(appId)
                    .setRedirectURI(redirectUrl)
                    .setScope(scope)
                    .authenticate(Const.GITLAB_REQUEST);
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByGitLab(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByGitLab(authCode, callback);
    }

    private static final class GitLibInstanceHolder {
        static final GitLab mInstance = new GitLab();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
