package cn.authing.guard.social;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.social.wechat.WXCallbackActivity;
import cn.authing.guard.util.Const;

public class WechatMiniProgram extends SocialAuthenticator {

    public static IWXAPI api;
    public static String mobileAppId;
    public static String miniProgramOriginalID;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        login(context, null, callback);
    }

    public void login(Context context, String contextParam, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (mobileAppId == null && config != null) {
                mobileAppId = config.getSocialMobileAppID(Const.EC_TYPE_WECHAT_MINI_PROGRAM);
            }

            if (miniProgramOriginalID == null && config != null) {
                miniProgramOriginalID = config.getSocialOriginalID(Const.EC_TYPE_WECHAT_MINI_PROGRAM);
            }

            WXCallbackActivity.setCallback(callback);
            WXCallbackActivity.setContext(contextParam);

            // 填移动应用(App)的 AppId，非小程序的 AppID
            IWXAPI api = WXAPIFactory.createWXAPI(context, mobileAppId);

            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
            req.userName = miniProgramOriginalID; // 填小程序原始id
            //req.path = path;   ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST;// 可选打开 开发版，体验版和正式版
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
