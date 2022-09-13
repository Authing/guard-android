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
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.social.SocialLoginButton;
import cn.authing.guard.social.Wechat;
import cn.authing.guard.util.ALog;

public class WXCallbackActivity extends AppCompatActivity implements IWXAPIEventHandler {

    public static final String TAG = WXCallbackActivity.class.getSimpleName();

    private static AuthCallback<UserInfo> callback;

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

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ALog.i(TAG, "Auth success");
                callback.call(SocialLoginButton.AUTH_SUCCESS, "Auth success", null);
                Authing.AuthProtocol authProtocol = Authing.getAuthProtocol();
                if (authProtocol == Authing.AuthProtocol.EInHouse) {
                    AuthClient.loginByWechat(((SendAuth.Resp) resp).code, callback);
                } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
                    new OIDCClient().loginByWechat(((SendAuth.Resp) resp).code, callback);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ALog.d(TAG, "wechat user canceled");
                if (callback != null) {
                    callback.call(BaseResp.ErrCode.ERR_USER_CANCEL, getString(R.string.authing_cancelled_by_user), null);
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ALog.w(TAG, "wechat user denied auth");
                if (callback != null) {
                    callback.call(BaseResp.ErrCode.ERR_AUTH_DENIED, "", null);
                }
                break;
            default:
                break;
        }

        finish();
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

}
