package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.ss.android.larksso.CallBackData;
import com.ss.android.larksso.IGetDataCallback;
import com.ss.android.larksso.LarkSSO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Lark extends SocialAuthenticator {

    private static final String TAG = "Lark";
    private String appId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            ArrayList<String> scopeList = new ArrayList<>();
            scopeList.add("contact:user.id:readonly");
            String aid = appId;
            if (aid == null && config != null){
                aid = config.getSocialAppId(Const.EC_TYPE_LARK_INTERNAL);
                if (aid == null){
                    aid = config.getSocialAppId(Const.EC_TYPE_LARK_PUBLIC);
                }
            }
            LarkSSO.Builder builder = new LarkSSO.Builder().setAppId(aid)
                    .setServer("Feishu")
                    .setScopeList(scopeList)
                    .setContext((Activity) context);

            LarkSSO.inst().startSSOVerify(builder, new IGetDataCallback() {

                @Override
                public void onSuccess(CallBackData callBackData) {
                    if (null == callBackData){
                        ALog.e(TAG, "Auth Failed, callBackData is null");
                        return;
                    }
                    ALog.i(TAG, "Auth success");
                    login(context, callBackData.code, callback);
                }

                @Override
                public void onError(CallBackData callBackData) {
                    String errorMessage = "Auth failed";
                    if ("-1".equals(callBackData.code)){
                        errorMessage = "状态码校验失败，非当前SDK请求的响应";
                    } else if("-2".equals(callBackData.code)){
                        errorMessage = "没有获得有效的授权码";
                    } else if("-3".equals(callBackData.code)){
                        errorMessage = context.getString(R.string.authing_cancelled_by_user);
                    } else if("-4".equals(callBackData.code)){
                        errorMessage = "跳转飞书失败";
                    } else if("-5".equals(callBackData.code)){
                        errorMessage = "授权失败";
                    } else if("-6".equals(callBackData.code)){
                        errorMessage = "请求参数错误";
                    }
                    ALog.e(TAG, "Auth Failed, errorCode is: " + callBackData.code + ",errorMessage is: " + errorMessage);
                    callback.call(Integer.parseInt(callBackData.code), errorMessage, null);
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByLark(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByLark(authCode, callback);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
