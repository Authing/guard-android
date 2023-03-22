package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;

public class Baidu extends SocialAuthenticator {

    private static final String TAG = "Baidu";
    private String appKey; // 应用的APP_KEY
    private com.baidu.api.Baidu baidu;

    private Baidu() {
    }

    public static Baidu getInstance() {
        return BaiduInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (appKey == null && config != null) {
                appKey = config.getSocialAppKey(Const.EC_TYPE_BAIDU);
            }

            if (TextUtils.isEmpty(appKey)){
                ALog.e(TAG, "appKey is null");
                callback.call(Const.ERROR_CODE_10014, "appKey is null", null);
                return;
            }

            baidu = new com.baidu.api.Baidu(appKey, context);
            baidu.authorize((Activity) context, false, true, new BaiduDialog.BaiduDialogListener() {
                @Override
                public void onComplete(Bundle bundle) {
                    ALog.i(TAG, "Auth onSuccess");
                    String access_token;
                    if (bundle.containsKey("access_token")) {
                        access_token = bundle.getString("access_token");
                    } else {
                        access_token = baidu.getAccessToken();
                    }
                    login(access_token, callback);
                }

                @Override
                public void onBaiduException(BaiduException e) {
                    ALog.e(TAG, "Auth Failed, onError");
                    callback.call(Const.ERROR_CODE_10014, "Login by Baidu failed", null);
                }

                @Override
                public void onError(BaiduDialogError baiduDialogError) {
                    ALog.e(TAG, "Auth Failed, onError");
                    callback.call(Const.ERROR_CODE_10014, "Login by Baidu failed", null);
                }

                @Override
                public void onCancel() {
                    ALog.e(TAG, "Auth Failed, onCancel");
                    callback.call(Const.ERROR_CODE_10014, "Login by Baidu canceled", null);
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByBaidu(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByBaidu(authCode, callback);
    }

    private static final class BaiduInstanceHolder {
        static final Baidu mInstance = new Baidu();
    }


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
