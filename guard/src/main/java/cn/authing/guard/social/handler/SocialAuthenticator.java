package cn.authing.guard.social.handler;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.handler.BaseHandler;
import cn.authing.guard.social.view.SocialLoginButton;

public abstract class SocialAuthenticator extends BaseHandler {

    public abstract void login(Context context, @NotNull AuthCallback<UserInfo> callback);

    protected abstract void standardLogin(String authCode, @NotNull AuthCallback<UserInfo> callback);

    protected abstract void oidcLogin(String authCode, @NotNull AuthCallback<UserInfo> callback);

    protected void login(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        callback.call(SocialLoginButton.AUTH_SUCCESS, "Auth success", null);
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            standardLogin(authCode, callback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            oidcLogin(authCode, callback);
        }
    }

    public void onDetachedFromWindow() {

    }
}
