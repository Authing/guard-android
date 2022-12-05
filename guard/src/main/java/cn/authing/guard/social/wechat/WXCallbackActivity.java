package cn.authing.guard.social.wechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.social.Wechat;
import cn.authing.guard.util.ALog;

public class WXCallbackActivity extends AppCompatActivity implements IWXAPIEventHandler {

    public static final String TAG = WXCallbackActivity.class.getSimpleName();

    private static AuthCallback<UserInfo> callback;
    private static AuthCallback<String> authCodeCallBack;
    private static boolean onlyGetAuthCode;
    private static String context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Wechat.api = WXAPIFactory.createWXAPI(this, Wechat.appId);
        Wechat.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        callback = null;
    }

    @Override
    public void onReq(BaseReq baseReq) {
        ALog.d(TAG, "onReq: ");
        finish();
    }

    public static void setContext(String context) {
        WXCallbackActivity.context = context;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Wechat.api.handleIntent(intent, this);
    }

    public static void setCallback(AuthCallback<UserInfo> callback) {
        WXCallbackActivity.callback = callback;
    }

    public static void setAuthCodeCallback(AuthCallback<String> callback) {
        WXCallbackActivity.authCodeCallBack = callback;
    }

    public static void setOnlyGetAuthCode(boolean onlyGetAuthCode) {
        WXCallbackActivity.onlyGetAuthCode = onlyGetAuthCode;
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ALog.d(TAG, "Got wechat code: " + ((SendAuth.Resp) resp).code);
                Authing.AuthProtocol authProtocol = Authing.getAuthProtocol();
                if (onlyGetAuthCode) {
                    if (authCodeCallBack != null) {
                        authCodeCallBack.call(200, "success", ((SendAuth.Resp) resp).code);
                    }
                } else {
                    if (authProtocol == Authing.AuthProtocol.EInHouse) {
                        AuthClient.loginByWechatWithBind(((SendAuth.Resp) resp).code, context, callback);
                    } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
                        new OIDCClient().loginByWechatWithBind(((SendAuth.Resp) resp).code, context, callback);
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ALog.i(TAG, "wechat user canceled");
                if (onlyGetAuthCode) {
                    if (authCodeCallBack != null) {
                        authCodeCallBack.call(BaseResp.ErrCode.ERR_USER_CANCEL, "canceled", null);
                    }
                } else {
                    if (callback != null) {
                        callback.call(BaseResp.ErrCode.ERR_USER_CANCEL, "canceled", null);
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ALog.w(TAG, "wechat user denied auth");
                if (onlyGetAuthCode) {
                    if (authCodeCallBack != null) {
                        authCodeCallBack.call(BaseResp.ErrCode.ERR_AUTH_DENIED, "denied", null);
                    }
                } else {
                    if (callback != null) {
                        callback.call(BaseResp.ErrCode.ERR_AUTH_DENIED, "denied", null);
                    }
                }
                break;
            default:
                break;
        }

        finish();
    }

}
