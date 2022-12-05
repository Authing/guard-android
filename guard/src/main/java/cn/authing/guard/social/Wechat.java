package cn.authing.guard.social;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.social.wechat.WXCallbackActivity;
import cn.authing.guard.util.Const;

public class Wechat extends SocialAuthenticator {

    public static IWXAPI api;
    public static String appId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        login(context, null, callback);
    }

    public void login(Context context, String contextParam, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            String id = appId;
            if (id == null && config != null){
                id = config.getSocialAppId(Const.EC_TYPE_WECHAT);
            }
            api = WXAPIFactory.createWXAPI(context, id, true);
            api.registerApp(id);

            WXCallbackActivity.setCallback(callback);
            WXCallbackActivity.setContext(contextParam);

            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            api.sendReq(req);
        });
    }

    public void getAuthCode(Context context, @NotNull AuthCallback<String> callback){
        Authing.getPublicConfig(config -> {
            String id = appId;
            if (id == null && config != null){
                id = config.getSocialAppId(Const.EC_TYPE_WECHAT);
            }
            api = WXAPIFactory.createWXAPI(context, id, true);
            api.registerApp(id);

            WXCallbackActivity.setAuthCodeCallback(callback);
            WXCallbackActivity.setOnlyGetAuthCode(true);

            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            api.sendReq(req);
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }
}
