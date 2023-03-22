package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Weibo extends SocialAuthenticator {

    private static final String TAG = "Weibo";
    private String appKey; // 应用的APP_KEY
    private String redirectUrl; // 应用的回调页
    private String scope = "email,direct_messages_read,direct_messages_write,friendships_groups_read," +
            "friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write";
    private IWBAPI mWBAPI;

    private Weibo() {
    }

    public static Weibo getInstance() {
        return WeiboInstanceHolder.mInstance;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        if (mWBAPI != null) {
            mWBAPI.authorizeCallback(activity, requestCode, resultCode, data);
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (appKey == null && config != null) {
                appKey = config.getSocialAppKey(Const.EC_TYPE_WEIBO);
            }
            if (redirectUrl == null && config != null) {
                redirectUrl = config.getSocialRedirectUrl(Const.EC_TYPE_WEIBO);
            }

            AuthInfo authInfo = new AuthInfo(context, appKey, redirectUrl, scope);
            mWBAPI = WBAPIFactory.createWBAPI(context);
            mWBAPI.registerApp(context, authInfo);
            mWBAPI.authorizeClient((Activity) context, new WbAuthListener() {
                @Override
                public void onComplete(Oauth2AccessToken token) {
                    ALog.i(TAG, "Auth onSuccess");
                    login(token.getAccessToken(), callback);
                }

                @Override
                public void onError(UiError error) {
                    ALog.e(TAG, "Auth Failed, errorMessage is" + error.errorMessage);
                    callback.call(Const.ERROR_CODE_10012, "Login by Weibo failed", null);
                }

                @Override
                public void onCancel() {
                    ALog.e(TAG, "Auth Failed, onCancel");
                    callback.call(Const.ERROR_CODE_10012, "Login by Weibo canceled", null);
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWeibo(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByWeibo(authCode, callback);
    }

    @SuppressLint("StaticFieldLeak")
    private static final class WeiboInstanceHolder {
        static final Weibo mInstance = new Weibo();
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
