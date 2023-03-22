package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.kwai.auth.ILoginListener;
import com.kwai.auth.KwaiAuthAPI;
import com.kwai.auth.common.InternalResponse;
import com.kwai.auth.common.KwaiConstants;
import com.kwai.auth.login.kwailogin.KwaiAuthRequest;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class KuaiShou extends SocialAuthenticator implements ILoginListener {

    private static final String TAG = "KuaiShou";
    private String state = "1234";
    private AuthCallback<UserInfo> callback;

    private KuaiShou() {
    }

    public static KuaiShou getInstance() {
        return KuaiShouInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            // STATE安全参数，标识和用户或者设备相关的授权请求。建议开发者实现
            // KwaiConstants.LoginType.APP通过快手App登录授权，KwaiConstants.LoginType.H5通过H5页面登录授权
            // 请求授权，支持两个平台KwaiConstants.Platform.KWAI_APP（快手主站）、KwaiConstants.Platform.NEBULA_APP（快手极速版），未设置的默认通过快手主站授权
            // 设置了两个平台且同时安装了快手主站和快手极速版，则按传入顺序调起
            // KwaiConstants.LoginType.APP使用快手应用授权，KwaiConstants.LoginType.H5使用前端页面通过手机号和验证码授权
            KwaiAuthRequest request = new KwaiAuthRequest.Builder()
                    .setState(state)
                    .setAuthMode(KwaiConstants.AuthMode.AUTHORIZE)
                    .setLoginType(KwaiConstants.LoginType.APP)
                    .setPlatformArray(new String[]{KwaiConstants.Platform.KWAI_APP, KwaiConstants.Platform.NEBULA_APP})
                    .build();
            KwaiAuthAPI.getInstance().sendRequest((Activity) context, request, this);
        });
    }


    @Override
    public void onSuccess(@NonNull InternalResponse internalResponse) {
        if (internalResponse.getCode() == null) {
            ALog.e(TAG, "Auth Failed, code is null");
            if (callback != null) {
                callback.call(Const.ERROR_CODE_10022, "Login by Kuaishou failed", null);
            }
            return;
        }
        login(internalResponse.getCode(), callback);
    }

    @Override
    public void onFailed(String s, int i, String s1) {
        ALog.e(TAG, "Auth Failed");
        if (callback != null) {
            callback.call(Const.ERROR_CODE_10022, "Login by Xiaomi Kuaishou failed, errCode = " + i + " errMessage = " + s1, null);
        }
    }

    @Override
    public void onCancel() {
        ALog.e(TAG, "Auth Canceled");
        if (callback != null) {
            callback.call(Const.ERROR_CODE_10022, "Login by Xiaomi Kuaishou canceled", null);
        }
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByKuaiShou(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByKuaiShou(authCode, callback);
    }

    @SuppressLint("StaticFieldLeak")
    private static final class KuaiShouInstanceHolder {
        static final KuaiShou mInstance = new KuaiShou();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
