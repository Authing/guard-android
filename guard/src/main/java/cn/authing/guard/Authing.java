package cn.authing.guard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.authing.guard.data.Config;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.SystemUtil;
import cn.authing.guard.util.Util;

public class Authing {

    private final static String TAG = "Authing";

    private static final String DEF_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xKeUgQ+Aoz7TLfAfs9+paePb5KIofVthEopwrXFkp8OCeocaTHt9ICjTT2QeJh6cZaDaArfZ873GPUn00eOIZ7Ae+TiA2BKHbCvloW3w5Lnqm70iSsUi5Fmu9/2+68GZRH9L7Mlh8cFksCicW2Y2W2uMGKl64GDcIq3au+aqJQIDAQAB";

    private static Context sAppContext;
    private static String scheme = "https";
    private static String sHost = "authing.cn"; // for private deployment
    private static String sWebSocketHost = "wss://openevent.authing.cn";
    private static boolean isOnPremises;
    private static String sPublicKey = DEF_PUBLIC_KEY;
    private static String sAppId;
    private static boolean isGettingConfig;
    private static boolean autoCheckNetWork;
    private static Config publicConfig;
    private static final Queue<Config.ConfigCallback> listeners = new ConcurrentLinkedQueue<>();
    private static UserInfo sCurrentUser;
    private static AuthProtocol authProtocol = AuthProtocol.EInHouse;

    public static void init(final Context context, String appId) {
        sAppContext = context.getApplicationContext();
        sAppId = appId;
        requestPublicConfig();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getScheme() {
        return scheme;
    }

    public static void setScheme(String scheme) {
        Authing.scheme = scheme;
    }

    public static String getHost() {
        return sHost;
    }

    public static void setHost(String host) {
        isOnPremises = true;
        Authing.sHost = host;
    }

    public static String getPublicKey() {
        return sPublicKey;
    }

    public static void setPublicKey(String publicKey) {
        Authing.sPublicKey = publicKey;
    }

    public static String getWebSocketHostHost() {
        return sWebSocketHost;
    }

    public static void setWebSocketHost(String host) {
        Authing.sWebSocketHost = host;
    }

    public static void setOnPremiseInfo(String host, String publicKey) {
        isOnPremises = true;
        Authing.sHost = host;
        Authing.sPublicKey = publicKey;
    }

    public static AuthProtocol getAuthProtocol() {
        return Authing.authProtocol;
    }

    public static void setAuthProtocol(AuthProtocol authProtocol) {
        Authing.authProtocol = authProtocol;
    }

    public enum AuthProtocol {
        EInHouse,
        EOIDC
    }

    public static boolean isAutoCheckNetWork() {
        return autoCheckNetWork;
    }

    public static void setAutoCheckNetWork(boolean autoCheckNetWork) {
        Authing.autoCheckNetWork = autoCheckNetWork;
    }

    public static void setIsOverseas() {
       sHost = "us.authing.co";
    }

    public static void autoLogin(AuthCallback<UserInfo> callback) {
        if (getCurrentUser() == null) {
            callback.call(Const.ERROR_CODE_10003, "Login failed", null);
        } else {
            String refreshToken = getCurrentUser().getRefreshToken();
            if (Util.isNull(refreshToken)) {
                AuthClient.getCurrentUser((code, message, userInfo) -> {
                    if (code != 200) {
                        fireCallBack(code, message);
                        callback.call(code, message, userInfo);
                    } else {
                        AuthClient.updateIdToken(callback);
                    }
                });
                return;
            }
            new OIDCClient().getNewAccessTokenByRefreshToken(refreshToken, (code, message, userInfo) -> {
                if (code != 200) {
                    fireCallBack(code, message);
                    callback.call(code, message, userInfo);
                } else {
                    AuthClient.getCurrentUserInfo(getCurrentUser(), (AuthCallback<UserInfo>) (code1, message1, userInfo1) -> {
                        if (code1 != 200) {
                            fireCallBack(code1, message1);
                        }
                        callback.call(code1, message1, userInfo1);
                    });
                }
            });
        }
    }

    private static void fireCallBack(int code, String message) {
        if (code == Const.EC_ACCOUNT_NOT_LOGIN) {
            ALog.d(TAG, "auto login token expired");
            Safe.logoutUser(sCurrentUser);
        }
        if (code == Const.EC_400 && message.contains("用户不存在")) {
            Safe.logoutUser(sCurrentUser);
        }
        sCurrentUser = null;
    }

    public static String getAppId() {
        return sAppId;
    }

    public static void getPublicConfig(Config.ConfigCallback callback) {
        // add listener first. otherwise callback might be fired in the other thread
        // and this listener is missed
        if (isGettingConfig) {
            listeners.add(callback);
            if (publicConfig != null) {
                listeners.clear();
                callback.call(publicConfig);
            }
        } else {
            callback.call(publicConfig);
        }
    }

    public static UserInfo getCurrentUser() {
        if (sCurrentUser == null) {
            sCurrentUser = Safe.loadUser();
        }
        return sCurrentUser;
    }

    public static void setCurrentUser(UserInfo userInfo) {
        sCurrentUser = userInfo;
    }

    public static void saveUser(UserInfo user) {
        sCurrentUser = user;
        Safe.saveUser(user);
    }

    private static void requestPublicConfig() {
        isGettingConfig = true;
        publicConfig = null;
        _requestPublicConfig();
    }

    private static void _requestPublicConfig() {
        String host = sHost;
        if (!Util.isIp(sHost) && !isOnPremises) {
            host = "console." + sHost;
        }
        String url = scheme + "://" + host + "/api/v2/applications/" + sAppId + "/public-config";
        Guardian.request(null, url, "get", null, null, false, (response) -> {
            try {
                if (response.getCode() == 200) {
                    JSONObject data = response.getData();
                    Config config = Config.parse(data);
                    config.setUserAgent(SystemUtil.getUserAgent(getAppContext()));
                    fireCallback(config);
                } else {
                    ALog.e(TAG, "Get public config failed for appId: " + sAppId + " Msg:" + response.getMessage());
                    fireCallback(null);
                    startListeningNetWork();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fireCallback(null);
            }
        });
    }

    private static void fireCallback(Config config) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            publicConfig = config;
            isGettingConfig = false;
            for (Config.ConfigCallback callback : listeners) {
                callback.call(config);
            }
            listeners.clear();
        });
    }

    public static boolean isGettingConfig() {
        return isGettingConfig;
    }

    public static boolean isConfigEmpty() {
        return publicConfig == null;
    }

    private static void startListeningNetWork() {
        if (!autoCheckNetWork || publicConfig != null){
            return;
        }
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        ConnectivityManager connMgr = (ConnectivityManager) getAppContext()
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return;
        }
        connMgr.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (isGettingConfig || publicConfig != null){
                    return;
                }
                new Handler().post(Authing::requestPublicConfig);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
            }

        });
    }
}
