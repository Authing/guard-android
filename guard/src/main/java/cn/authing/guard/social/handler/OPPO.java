package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.heytap.msp.oauth.OAuthConstants;
import com.heytap.msp.oauth.bean.OAuthCodeResponse;
import com.heytap.msp.oauth.bean.OAuthRequest;
import com.heytap.msp.result.BaseErrorCode;
import com.heytap.msp.sdk.OAuthSdk;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class OPPO extends SocialAuthenticator {

    private static final String TAG = "OPPO";
    private String appId;
    private String scope = "profile openid";

    private OPPO() {
    }

    public static OPPO getInstance() {
        return OppoInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (appId == null && config != null) {
                appId = config.getSocialAppId(Const.EC_TYPE_OPPO);
            }

            OAuthRequest request = new OAuthRequest();
            //设置您的AppID(开放平台创建应用时生成的)
            request.setAppId(appId);
            //设置您创建的应用类型(APP,H5,FAST)
            request.setAppType(OAuthConstants.AuthAppType.APP);
            //设置您的请求Tag(可以为空)
            request.setRequestTag("oppo");
            //设置您的授权Scope(openid,profile)
            //request.setScope(OAuthConstants.AuthScope.AUTH_SCOPE_PROFILE);
            request.setScope(scope);
            //设置您的授权显示界面类型(popup，page)
            request.setDisplay(OAuthConstants.AuthShowType.AUTH_SHOW_TYPE_PAGE);
            //设置您的授权prompt,prompt的详细介绍请参考表格----prompt字段介绍
            request.setPrompt(OAuthConstants.Prompt.NONE);
            //传入授权请求参数，调用授权API
            OAuthSdk.requestOauthCode(request, response -> {
                if (response.getCode() == BaseErrorCode.ERROR_SUCCESS) {
                    OAuthCodeResponse oAuthCodeResponse = response.getResponse();
                    if (oAuthCodeResponse.getCode() == null) {
                        ALog.e(TAG, "Auth Failed, code is null");
                        callback.call(Const.ERROR_CODE_10027, "Auth Failed, code is null", null);
                        return;
                    }
                    ALog.i(TAG, "Auth success");
                    login(oAuthCodeResponse.getCode(), callback);
                } else {
                    ALog.e(TAG, "Auth Failed, errorCode is: " + response.getMessage() + ",errorMessage is: " + response.getMessage());
                    callback.call(Integer.parseInt(response.getMessage()), response.getMessage(), null);
                }
            });

        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByOppo(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByOppo(authCode, callback);
    }

    @SuppressLint("StaticFieldLeak")
    private static final class OppoInstanceHolder {
        static final OPPO mInstance = new OPPO();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
