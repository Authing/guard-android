package cn.authing.guard.social;

import android.content.Context;
import android.util.Log;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.WWAuthMessage;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;

public class WeCom extends SocialAuthenticator {

    private static final String TAG = "WeCom";

    public static String schema;
    public static String agentId;
    public static String corpId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        // TODO get from authing server

        IWWAPI iwwapi = WWAPIFactory.createWWAPI(context);
        iwwapi.registerApp(schema);

        final WWAuthMessage.Req req = new WWAuthMessage.Req();
        req.sch = schema;
        req.agentId = agentId;
        req.appId = corpId;
        req.state = "wecom";
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
                    // TODO get auth info from autthing server
                    fireCallback(callback, null);
                }
            }
        });
    }

    private static void fireCallback(AuthCallback<UserInfo> callback, UserInfo info) {
        if (callback != null) {
            callback.call(0, "", info);
        }
    }
}
