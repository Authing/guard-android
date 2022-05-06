package cn.authing.guard.social;

import android.app.Activity;
import android.content.Context;

import com.ss.android.larksso.CallBackData;
import com.ss.android.larksso.IGetDataCallback;
import com.ss.android.larksso.LarkSSO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Lark extends SocialAuthenticator {

    private static final String TAG = "SocialAuthenticator";
    public static String appId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            ArrayList<String> scopeList = new ArrayList<>();
            scopeList.add("contact:user.id:readonly");
            String aid = (appId != null ) ? appId : config.getSocialAppId(Const.EC_TYPE_LARK_INTERNAL);
            aid = (aid != null) ? aid : config.getSocialAppId(Const.EC_TYPE_LARK_PUBLIC);
            LarkSSO.Builder builder = new LarkSSO.Builder().setAppId(aid)
                    .setServer("Feishu")
                    .setScopeList(scopeList)
                    .setContext((Activity) context);

            LarkSSO.inst().startSSOVerify(builder, new IGetDataCallback() {

                @Override
                public void onSuccess(CallBackData callBackData) {
                    AuthContainer.AuthProtocol authProtocol = getAuthProtocol(context);
                    if (authProtocol == AuthContainer.AuthProtocol.EInHouse) {
                        AuthClient.loginByLark(callBackData.code, callback);
                    } else if (authProtocol == AuthContainer.AuthProtocol.EOIDC) {
                        OIDCClient.loginByLark(callBackData.code, callback);
                    }
                }

                @Override
                public void onError(CallBackData callBackData) {
                    ALog.e(TAG, "Auth Failed, errorCode is" + callBackData.code);
                    callback.call(Integer.parseInt(callBackData.code), "Login by lark failed", null);
                }
            });
        });
    }
}
