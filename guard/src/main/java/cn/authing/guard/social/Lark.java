package cn.authing.guard.social;

import android.app.Activity;
import android.content.Context;

import com.ss.android.larksso.CallBackData;
import com.ss.android.larksso.IGetDataCallback;
import com.ss.android.larksso.LarkSSO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.ALog;

public class Lark extends SocialAuthenticator {

    private static final String TAG = "SocialAuthenticator";
    public static String appId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        ArrayList<String> scopeList = new ArrayList<>();
        scopeList.add("contact:user.id:readonly");
        LarkSSO.Builder builder = new LarkSSO.Builder().setAppId(appId)
                .setServer("Feishu")
                .setScopeList(scopeList)
                .setContext((Activity) context);

        LarkSSO.inst().startSSOVerify(builder, new IGetDataCallback() {
            @Override
            public void onSuccess(CallBackData callBackData) {
                AuthClient.loginByLark(callBackData.code, callback);
            }
            @Override
            public void onError(CallBackData callBackData) {
                ALog.e(TAG, "Auth Failed, errorCode is" + callBackData.code);
                callback.call(Integer.parseInt(callBackData.code), "Login by lark failed", null);
            }
        });
    }
}
