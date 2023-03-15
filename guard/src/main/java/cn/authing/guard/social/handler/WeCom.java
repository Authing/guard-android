package cn.authing.guard.social.handler;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.WWAuthMessage;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class WeCom extends SocialAuthenticator {

    private static final String TAG = "WeCom";

    private final String type;
    private String schema;
    private String agentId;
    private String corpId;

    public WeCom() {
        type = Const.EC_TYPE_WECHAT_COM;
    }

    public WeCom(String type) {
        if (!TextUtils.isEmpty(type) && Const.TYPE_WECHAT_COM_AGENCY.equals(type)) {
            this.type = Const.EC_TYPE_WECHAT_COM_AGENCY;
        } else {
            this.type = Const.EC_TYPE_WECHAT_COM;
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            IWWAPI iwwapi = WWAPIFactory.createWWAPI(context);

            String sch = schema;
            if (sch == null && config != null){
                sch = config.getSocialSchema(type);
            }
            iwwapi.registerApp(sch);

            final WWAuthMessage.Req req = new WWAuthMessage.Req();
            req.sch = sch;
            req.agentId = agentId;
            if (agentId == null && config != null){
                req.agentId = config.getSocialAgentId(type);
            }
            req.appId = corpId;
            if (corpId == null && config != null){
                req.appId = config.getSocialAppId(type);
            }
            req.state = type;
            iwwapi.sendMessage(req, resp -> {
                if (resp instanceof WWAuthMessage.Resp) {
                    WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
                    if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
                        ALog.i(TAG, context.getString(R.string.authing_cancelled_by_user));
                        fireCallback(callback, context.getString(R.string.authing_cancelled_by_user));
                    } else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                        ALog.i(TAG, "登录失败");
                        fireCallback(callback, context.getString(R.string.authing_auth_failed));
                    } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                        ALog.i(TAG, "Auth success");
                        login(context, rsp.code, callback);
                    } else {
                        ALog.e(TAG, "Auth failed");
                        fireCallback(callback, context.getString(R.string.authing_auth_failed));
                    }
                } else {
                    ALog.e(TAG, "Auth failed, resp is null");
                    fireCallback(callback, context.getString(R.string.authing_auth_failed));
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
            AuthClient.loginByWecom(authCode, callback);
        } else if (Const.EC_TYPE_WECHAT_COM_AGENCY.equals(type)) {
            AuthClient.loginByWecomAgency(authCode, callback);
        }
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
            new OIDCClient().loginByWecom(authCode, callback);
        } else if (Const.EC_TYPE_WECHAT_COM_AGENCY.equals(type)) {
            new OIDCClient().loginByWecomAgency(authCode, callback);
        }
    }

    private void fireCallback(AuthCallback<UserInfo> callback, String message) {
        if (callback != null) {
            callback.call(500, message, null);
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }
}
