package cn.authing.guard.social;

import android.content.Context;
import androidx.annotation.NonNull;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.WWAuthMessage;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class WeCom extends SocialAuthenticator {

    private static final String TAG = "WeCom";

    public static String schema;
    public static String agentId;
    public static String corpId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            IWWAPI iwwapi = WWAPIFactory.createWWAPI(context);
            String sch = (schema != null ) ? schema : config.getSocialSchema(Const.EC_TYPE_WECHAT_COM);;
            iwwapi.registerApp(sch);

            final WWAuthMessage.Req req = new WWAuthMessage.Req();
            req.sch = sch;
            req.agentId = (agentId != null ) ? agentId : config.getSocialAgentId(Const.EC_TYPE_WECHAT_COM);
            req.appId = (corpId != null ) ? corpId : config.getSocialAppId(Const.EC_TYPE_WECHAT_COM);
            req.state = Const.EC_TYPE_WECHAT_COM;
            iwwapi.sendMessage(req, resp -> {
                if (resp instanceof WWAuthMessage.Resp) {
                    WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
                    if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
                        ALog.i(TAG, "登录取消");
                        fireCallback(callback, null);
                    } else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                        ALog.i(TAG, "登录失败");
                        fireCallback(callback, null);
                    } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                        ALog.i(TAG, "Auth onSuccess");
                        login(context, rsp.code, callback);
                    }
                } else {
                    ALog.e(TAG, "Auth Failed, resp error");
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWecom(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        OIDCClient.loginByWecom(authCode, callback);
    }

    private void fireCallback(AuthCallback<UserInfo> callback, UserInfo info) {
        if (callback != null) {
            callback.call(0, "", info);
        }
    }
}
