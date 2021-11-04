package cn.authing.guard.social;

import android.content.Context;
import android.util.Log;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.WWAuthMessage;

import cn.authing.guard.Callback;
import cn.authing.guard.data.UserInfo;

public class WeCom {

    private static final String TAG = "WeCom";

    public static String schema;
    public static String agentId;
    public static String corpId;

    public static void login(Context context, Callback<UserInfo> callback) {
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
                    callback(callback, false, null);
                } else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                    Log.i(TAG, "登录失败");
                    callback(callback, false, null);
                } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                    // TODO get auth info from autthing server
                    callback(callback, true, null);
                }
            }
        });
    }

    private static void callback(Callback<UserInfo> callback, boolean ok, UserInfo data) {
        if (callback != null) {
            callback.call(ok, data);
        }
    }
}
