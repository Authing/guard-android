package cn.authing.guard.social.callback.douyinapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bytedance.sdk.open.aweme.CommonConstants;
import com.bytedance.sdk.open.aweme.authorize.model.Authorization;
import com.bytedance.sdk.open.aweme.common.handler.IApiEventHandler;
import com.bytedance.sdk.open.aweme.common.model.BaseReq;
import com.bytedance.sdk.open.aweme.common.model.BaseResp;
import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.api.DouYinOpenApi;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.social.view.SocialLoginButton;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class DouYinCallBackActivity extends Activity implements IApiEventHandler {

    public static final String TAG = DouYinCallBackActivity.class.getSimpleName();
    private static AuthCallback<UserInfo> callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DouYinOpenApi douYinOpenApi = DouYinOpenApiFactory.create(this);
        douYinOpenApi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        // 授权成功可以获得authCode
        if (resp.getType() == CommonConstants.ModeType.SEND_AUTH_RESPONSE) {
            Authorization.Response response = (Authorization.Response) resp;
            if (resp.isSuccess()) {
                ALog.i(TAG, "Auth success");
                callback.call(SocialLoginButton.AUTH_SUCCESS, "Auth success", null);
                login(response.authCode);
            } else {
                ALog.e(TAG, "Auth Failed: errorCode = " + response.errorCode + " errorMsg = " + response.errorMsg);
                callback.call(Const.ERROR_CODE_10017, "Login by dou yin failed", null);
            }
            finish();
        }
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
        if (callback != null) {
            ALog.e(TAG, "Auth Failed");
            callback.call(Const.ERROR_CODE_10017, "Login by dou yin failed", null);
        }
        finish();
    }

    public static void setCallback(AuthCallback<UserInfo> callback) {
        DouYinCallBackActivity.callback = callback;
    }

    private void login(String code) {
        Authing.AuthProtocol authProtocol = Authing.getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByDouYin(code, callback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByDouYin(code, callback);
        }
    }
}
