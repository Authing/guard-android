package cn.authing.guard;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class Authing {

    private final static String TAG = "Authing";

    private static Context sAppContext;
    private static String schema = "https";
    private static String sHost = "authing.cn"; // for private deployment
    private static String sAppId;
    private static boolean isGettingConfig;
    private static Config publicConfig;
    private static final Queue<Config.ConfigCallback> listeners = new ConcurrentLinkedQueue<>();
    private static UserInfo sCurrentUser;

    public static void init(final Context context, String appId) {
        sAppContext = context.getApplicationContext();
        sAppId = appId;
        requestPublicConfig();
        Analyzer.reportSDKUsage();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getSchema() {
        return schema;
    }

    public static void setSchema(String schema) {
        Authing.schema = schema;
    }

    public static String getHost() {
        return sHost;
    }

    public static void autoLogin(AuthCallback<UserInfo> callback) {
        if (getCurrentUser() == null) {
            callback.call(500, "no user logged in", null);
        } else {
            // TODO need to use refresh token to get latest access token
            AuthClient.getCurrentUserInfo(((code, message, userInfo) -> {
                if (code != 200) {
                    ALog.d(TAG, "auto login token expired");
                    Safe.logoutUser(sCurrentUser);
                    sCurrentUser = null;
                }
                callback.call(code, message, userInfo);
            }));
        }
    }

    public static void setHost(String sHost) {
        Authing.sHost = sHost;
    }

    public static String getAppId() {
        return sAppId;
    }

    public static void getPublicConfig(Config.ConfigCallback callback) {
        // add listener first. otherwise callback might be fired in the other thread
        // and this listener is missed
        if (isGettingConfig) {
            listeners.add(callback);
        }

        if (publicConfig != null) {
            listeners.clear();
            callback.call(publicConfig);
        }
    }

    private static void requestPublicConfig() {
        isGettingConfig = true;
        publicConfig = null;
        _requestPublicConfig();
    }

    public static UserInfo getCurrentUser() {
        if (sCurrentUser == null) {
            sCurrentUser = Safe.loadUser();
        }
        return sCurrentUser;
    }

    public static void saveUser(UserInfo user) {
        sCurrentUser = user;
        Safe.saveUser(user);
    }

    public static void logout(AuthCallback<JSONObject> callback) {
        AuthClient.logout(((code, message, data) -> {
            Safe.logoutUser(sCurrentUser);
            sCurrentUser = null;
            callback.call(code, message,data);
        }));
    }

    private static void _requestPublicConfig() {
        String host = sHost;
        if (!Util.isIp(sHost)) {
            host = "console." + sHost;
        }
        String url = schema + "://" + host + "/api/v2/applications/" + sAppId + "/public-config";
        Guardian.request(null, url, "get", null, (response)->{
            try {
                if (response.getCode() == 200) {
                    JSONObject data = response.getData();
                    publicConfig = Config.parse(data);
                    fireCallback(publicConfig);
                } else {
                    Log.e(TAG, "Get public config failed for appId: " + sAppId + " Msg:" + response.getMessage());
                    fireCallback(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fireCallback(null);
            }
        });
    }

    private static void fireCallback(Config config) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()->{
            for (Config.ConfigCallback callback : listeners) {
                callback.call(config);
            }
            listeners.clear();
            isGettingConfig = false;
        });
    }
}
