package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.dingtalk.openauth.AuthLoginParam;
import com.android.dingtalk.openauth.DDAuthApiFactory;
import com.android.dingtalk.openauth.IDDAuthApi;
import com.android.dingtalk.openauth.utils.DDAuthConstant;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class DingTalk extends SocialAuthenticator {

    private static final String TAG = "DingTalk";
    private String appKey;
    private String redirectUrl;
    private String scope = "openid";
    private String responseType = "code";
    private String nonce = "";
    private String state = "";
    private String prompt = "consent";
    private Context context;
    private AuthCallback<UserInfo> callback;

    private DingTalk() {
    }

    @SuppressLint("StaticFieldLeak")
    private static final class LinkedinInstanceHolder {
        static final DingTalk mInstance = new DingTalk();
    }

    public static DingTalk getInstance() {
        return LinkedinInstanceHolder.mInstance;
    }

    public void onActivityResult(Intent intent) {
        String authCode = intent.getStringExtra(DDAuthConstant.CALLBACK_EXTRA_AUTH_CODE);
        String state = intent.getStringExtra(DDAuthConstant.CALLBACK_EXTRA_STATE);
        String error = intent.getStringExtra(DDAuthConstant.CALLBACK_EXTRA_ERROR);
        if (!TextUtils.isEmpty(authCode)) {
            // 授权成功
            ALog.i(TAG, "Auth onSuccess");
            login(authCode, callback);
        } else {
            // 授权失败
            ALog.e(TAG, "Auth Failed, errorMessage is" + error);
            if (callback != null) {
                callback.call(Const.ERROR_CODE_10016, "Login by DingTalk failed", null);
            }
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.context = context;
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (appKey == null && config != null) {
                appKey = config.getSocialAppKey(Const.EC_TYPE_DING_TALK);
            }
            if (redirectUrl == null && config != null) {
                redirectUrl = config.getSocialRedirectUrl(Const.EC_TYPE_DING_TALK);
            }

            AuthLoginParam.AuthLoginParamBuilder builder = AuthLoginParam.AuthLoginParamBuilder.newBuilder();
            builder.appId(appKey);
            builder.redirectUri(redirectUrl);
            builder.responseType(responseType);
            builder.scope(scope);
            builder.nonce(nonce);
            builder.state(state);
            builder.prompt(prompt);
            IDDAuthApi authApi = DDAuthApiFactory.createDDAuthApi(context, builder.build());
            authApi.authLogin();
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByDingTalk(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByDingTalk(authCode, callback);
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

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}