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

public class Gitee extends SocialAuthenticator {

    private static final String TAG = "Gitee";
    private String clientId;
    private String redirectUrl;
    private String scope = "user_info emails";
    public static final int GITEE_REQUEST = 2002;
    private AuthCallback<UserInfo> callback;

    private Gitee() {
    }

    public static Gitee getInstance() {
        return GithubInstanceHolder.mInstance;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GITEE_REQUEST && data != null) {
            if (resultCode == Activity.RESULT_OK && data.hasExtra("social_login")) {
                ALog.i(TAG, "Auth onSuccess");
                WebAuthUser user = data.getParcelableExtra("social_login");
                if (user != null && user.getCode() != null) {
                    login(activity, user.getCode(), callback);
                }
            } else {
                ALog.e(TAG, "Auth Failed, onError errorCode = " + data.getIntExtra("err_code", 0)
                        + " errorMsg = " + data.getStringExtra("err_message"));
                if (callback != null) {
                    callback.call(Const.ERROR_CODE_10018, "Login by Gitee failed", null);
                }
            }
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (clientId == null && config != null) {
                clientId = config.getSocialClientId(Const.EC_TYPE_GITEE);
            }
            if (redirectUrl == null && config != null) {
                redirectUrl = config.getSocialRedirectUrl(Const.EC_TYPE_GITEE);
            }
            WebAuthBuilder.getInstance((Activity) context)
                    .setAuthorizationUrl("https://gitee.com/oauth/authorize")
                    .setAccessTokenUrl("https://gitee.com/oauth/token")
                    .setClientID(clientId)
                    .setRedirectURI(redirectUrl)
                    .setScope(scope)
                    .authenticate(GITEE_REQUEST);
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByGitee(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByGitee(authCode, callback);
    }

    private static final class GithubInstanceHolder {
        static final Gitee mInstance = new Gitee();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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
