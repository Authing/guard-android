package cn.authing.guard.social;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.authing.guard.Callback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.wxapi.WXEntryActivity;

public class Wechat {

    public static IWXAPI api;
    public static String appId;

    public static void login(Context context, Callback<UserInfo> callback) {
        api = WXAPIFactory.createWXAPI(context, appId, true);
        api.registerApp(appId);

        WXEntryActivity.setCallback(callback);

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }
}
