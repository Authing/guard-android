package cn.authing.guard.wxapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.social.Wechat;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    public static final String TAG = WXEntryActivity.class.getSimpleName();

    private static Callback<UserInfo> callback;

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
        Log.d(TAG, "onReq: ");
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Log.d(TAG, "Got wechat code: " + ((SendAuth.Resp) resp).code);
                getUserInfo(((SendAuth.Resp) resp).code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
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

    public static void setCallback(Callback<UserInfo> callback) {
        WXEntryActivity.callback = callback;
    }

    private void getUserInfo(String code) {
        Authing.getPublicConfig((config -> {
            String poolId = config.getUserPoolId();
            String url = "https://core.authing.cn/connection/social/wechat:mobile/" + poolId + "/callback?code=" + code;
            Guardian.get(url, (response)->{
                if (response != null && response.getCode() == 200) {
                    try {
                        UserInfo userInfo = UserInfo.createUserInfo(response.getData());
                        fireCallback(userInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    fireCallback(null);
                }
            });
        }));
    }

    private void fireCallback(UserInfo info) {
        if (callback != null) {
            callback.call(true, info);
        }
    }
}
