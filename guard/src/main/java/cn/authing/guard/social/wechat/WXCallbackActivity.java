package cn.authing.guard.social.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

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
                if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM){
                    handlerWechatMiniProgram(resp);
                } else {
                    handlerWechat(resp);
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

    private void handlerWechat(BaseResp resp){
        SendAuth.Resp sendAuthResp = (SendAuth.Resp) resp;
        String code = sendAuthResp.code;
        ALog.d(TAG, "Got wechat code: " + code);
        if (onlyGetAuthCode) {
            if (authCodeCallBack != null) {
                authCodeCallBack.call(200, "success", code);
            }
        } else {
            Authing.AuthProtocol authProtocol = Authing.getAuthProtocol();
            if (authProtocol == Authing.AuthProtocol.EInHouse) {
                AuthClient.loginByWechatWithBind(code, context, callback);
            } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
                new OIDCClient().loginByWechatWithBind(code, context, callback);
            }
        }
    }

    private void handlerWechatMiniProgram(BaseResp resp){
        WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
        String extraData = launchMiniProResp.extMsg;
        ALog.d(TAG, "Got wechat miniProgram extMsg: " + extraData);
        if (TextUtils.isEmpty(extraData)){
            if (callback != null) {
                callback.call(BaseResp.ErrCode.ERR_AUTH_DENIED, "extraData is empty", null);
            }
            return;
        }

        JSONObject jsonExtraData;
//        String iv = null;
//        String encryptedData = null;
        String code = null;
        String phoneInfoCode = null;
        try {
            jsonExtraData = new JSONObject(extraData);
//            if (jsonExtraData.has("iv")) {
//                iv = jsonExtraData.getString("iv");
//            }
//            if (jsonExtraData.has("encryptedData")) {
//                encryptedData = jsonExtraData.getString("encryptedData");
//            }
            if (jsonExtraData.has("code")) {
                code = jsonExtraData.getString("code");
            }
            if (jsonExtraData.has("phoneInfoCode")) {
                phoneInfoCode = jsonExtraData.getString("phoneInfoCode");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Authing.AuthProtocol authProtocol = Authing.getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByWechatMiniProgram(code, phoneInfoCode, callback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByWechatMiniProgram(code, phoneInfoCode, callback);
        }
    }

}
