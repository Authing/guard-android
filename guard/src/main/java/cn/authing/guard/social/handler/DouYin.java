package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.social.callback.douyinapi.DouYinCallBackActivity;
import cn.authing.guard.util.Const;

public class DouYin extends SocialAuthenticator {

    private static final String TAG = "DouYin";
    private String clientKey;
    private String scope;
    private String state;
    private String callerLocalEntry;

    private DouYin() {
    }

    public static DouYin getInstance() {
        return DouYinInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (clientKey == null && config != null) {
                clientKey = config.getSocialClientKey(Const.EC_TYPE_DOU_YIN);
            }

            DouYinCallBackActivity.setCallback(callback);
            DouYinOpenApiFactory.init(new DouYinOpenConfig(clientKey));

            DouYinOpenApi douyinOpenApi = DouYinOpenApiFactory.create((Activity) context);
            Authorization.Request request = new Authorization.Request();
            // 用户授权时必选权限
            request.scope = scope;
            // 用于保持请求和回调的状态，授权请求后原样带回给第三方。
            //request.state = "ww";
            if (state != null) {
                request.state = state;
            }
            // 第三方指定自定义的回调类 Activity
            if (callerLocalEntry != null) {
                request.callerLocalEntry = callerLocalEntry;
            }
            // 优先使用抖音app进行授权，如果抖音app因版本或者其他原因无法授权，则使用web页授权
            douyinOpenApi.authorize(request);
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
    }

    @SuppressLint("StaticFieldLeak")
    private static final class DouYinInstanceHolder {
        static final DouYin mInstance = new DouYin();
    }


    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCallerLocalEntry() {
        return callerLocalEntry;
    }

    public void setCallerLocalEntry(String callerLocalEntry) {
        this.callerLocalEntry = callerLocalEntry;
    }
}
