package cn.authing.guard.social;

import android.content.Context;
import android.util.Log;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.WWAuthMessage;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
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
            iwwapi.registerApp(schema);

            final WWAuthMessage.Req req = new WWAuthMessage.Req();
            req.sch = (schema != null ) ? schema : config.getSocialSchema(Const.EC_TYPE_WECHAT_COM);
            req.agentId = (agentId != null ) ? agentId : config.getSocialAgentId(Const.EC_TYPE_WECHAT_COM);
            req.appId = (corpId != null ) ? corpId : config.getSocialAppId(Const.EC_TYPE_WECHAT_COM);
            req.state = Const.EC_TYPE_WECHAT_COM;
            iwwapi.sendMessage(req, resp -> {
                if (resp instanceof WWAuthMessage.Resp) {
                    WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
                    if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
                        Log.i(TAG, "登录取消");
                        fireCallback(callback, null);
                    } else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                        Log.i(TAG, "登录失败");
                        fireCallback(callback, null);
                    } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                        AuthContainer.AuthProtocol authProtocol = getAuthProtocol(context);
                        if (authProtocol == AuthContainer.AuthProtocol.EInHouse) {
                            AuthClient.loginByWecom(rsp.code, callback);
                        } else if (authProtocol == AuthContainer.AuthProtocol.EOIDC) {
                            OIDCClient.loginByWecom(rsp.code, callback);
                        }
                    }
                }
            });
        });
    }

    private static void fireCallback(AuthCallback<UserInfo> callback, UserInfo info) {
        if (callback != null) {
            callback.call(0, "", info);
        }
    }
}
