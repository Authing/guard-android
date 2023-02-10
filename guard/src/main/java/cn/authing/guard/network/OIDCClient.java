package cn.authing.guard.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.PKCE;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OIDCClient {

    private AuthRequest authRequest;

    private static final String TAG = "OIDCClient";

    public OIDCClient() {
        authRequest = new AuthRequest();
    }

    public OIDCClient(AuthRequest authRequest) {
        this.authRequest = authRequest;
        if (null == authRequest){
            this.authRequest = new AuthRequest();
        }
    }

    public void buildAuthorizeUrl(Callback<String> callback) {
        Authing.getPublicConfig(config -> {
            if (config == null){
                callback.call(false, "Config not found");
                return;
            }
            callback.call(true, buildAuthorizeUrl(config, authRequest));
        });
    }

    private String buildAuthorizeUrl(Config config, AuthRequest authRequest) {
        String secret = authRequest.getClientSecret();
        return Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/auth?_authing_lang="
                + Util.getLangHeader()
                + "&app_id=" + Authing.getAppId()
                + "&client_id=" + Authing.getAppId()
                + "&nonce=" + authRequest.getNonce()
                + "&redirect_uri=" + authRequest.getRedirectURL()
                + "&response_type=" + authRequest.getResponse_type()
                + "&scope=" + authRequest.getScope()
                + "&prompt=consent"
                + "&state=" + authRequest.getState()
                + (secret == null ? "&code_challenge=" + authRequest.getCodeChallenge() + "&code_challenge_method=" + PKCE.getCodeChallengeMethod() : "");
    }

    public void registerByExtendField(String fieldName, String account, String password, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByExtendField(authRequest, fieldName, account, password, context, callback);
    }

    public void registerByEmail(String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByEmail(authRequest, email, password, null, callback);
    }

    public void registerByEmail(String email, String password, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByEmail(authRequest, email, password, context, callback);
    }

    public void registerByEmailCode(String email, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByEmailCode(authRequest, email, vCode, null, callback);
    }

    public void registerByEmailCode(String email, String vCode, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByEmailCode(authRequest, email, vCode, context, callback);
    }

    public void registerByPhonePassword(String phoneCountryCode, String phone, String password, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByPhonePassword(authRequest, phoneCountryCode, phone, password, context, callback);
    }

    public void registerByPhoneCode(String phone, String vCode, String password, @NotNull AuthCallback<UserInfo> callback) {
        registerByPhoneCode(null, phone, vCode, password, callback);
    }

    public void registerByPhoneCode(String phoneCountryCode, String phone, String vCode, String password, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByPhoneCode(authRequest, phoneCountryCode, phone, vCode, password, null, callback);
    }

    public void registerByPhoneCode(String phoneCountryCode, String phone, String vCode, String password, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByPhoneCode(authRequest, phoneCountryCode, phone, vCode, password, context, callback);
    }

    public void loginByPhoneCode(String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByPhoneCode(null, phone, vCode, callback);
    }

    public void loginByPhoneCode(String phoneCountryCode, String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByPhoneCode(authRequest, phoneCountryCode, phone, vCode, true, null, callback);
    }

    public void loginByPhoneCode(String phoneCountryCode, String phone, String vCode, boolean autoRegister, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByPhoneCode(authRequest, phoneCountryCode, phone, vCode, autoRegister, context, callback);
    }

    public void loginByEmailCode(String email, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByEmailCode(authRequest, email, vCode, true, null, callback);
    }

    public void loginByEmailCode(String email, String vCode, boolean autoRegister, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByEmailCode(authRequest, email, vCode, autoRegister, context, callback);
    }

    public void loginByAccount(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        loginByAccount(account, password, true, null, callback);
    }

    public void loginByAccount(String account, String password, boolean autoRegister, String context, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        AuthClient.loginByAccount(authRequest, account, password, autoRegister, context, ((c, m, data) -> {
            ALog.d(TAG, "OIDCClient.loginByAccount cost:" + (System.currentTimeMillis() - now) + "ms");
            callback.call(c, m, data);
        }));
    }

    public void loginByOneAuth(String token, String accessToken, @NotNull AuthCallback<UserInfo> callback) {
        loginByOneAuth(token, accessToken, 0, callback);
    }

    public void loginByOneAuth(String token, String accessToken, int netWork, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        AuthClient.loginByOneAuth(authRequest, token, accessToken, netWork, (AuthCallback<UserInfo>) (c, m, data) -> {
            ALog.d(TAG, "OIDCClient.loginByOneAuth cost:" + (System.currentTimeMillis() - now) + "ms");
            callback.call(c, m, data);
        });
    }

    public void loginByWechat(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWechat(authRequest, authCode, callback);
    }

    public void loginByWechatWithBind(String authCode, String context, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWechatWithBind(authRequest, authCode, context, callback);
    }

    public void loginByWecom(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWecom(authRequest, authCode, callback);
    }

    public void loginByWecomAgency(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWecomAgency(authRequest, authCode, callback);
    }

    public void loginByAlipay(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByAlipay(authRequest, authCode, callback);
    }

    public void loginByLark(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByLark(authRequest, authCode, callback);
    }

    public void loginByGoogle(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByGoogle(authRequest, authCode, callback);
    }

    public void loginByFaceBook(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByFaceBook(authRequest, authCode, callback);
    }

    private static void error(Exception e, @NotNull AuthCallback<?> callback){
        e.printStackTrace();
        callback.call(Const.ERROR_CODE_10004, "JSON parse failed", null);
    }

    public void authByCode(String code, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
            if (config == null){
                callback.call(Const.ERROR_CODE_10002, "Config not found", null);
                return;
            }
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                RequestBody formBody = new FormBody.Builder()
                        .add("client_id",Authing.getAppId())
                        .add("grant_type", "authorization_code")
                        .add("code", code)
                        .add("scope", authRequest.getScope())
                        .add("prompt", "consent")
                        .add(secret == null ? "code_verifier" : "client_secret", secret == null ? authRequest.getCodeVerifier() : secret)
                        .add("redirect_uri", authRequest.getRedirectURL())
                        .build();
                Guardian.authRequest(url, "post", formBody, (data)-> {
                    ALog.d(TAG, "authByCode cost:" + (System.currentTimeMillis() - now) + "ms");
                    if (data.getCode() == 200) {
                        try {
                            UserInfo userInfo = UserInfo.createUserInfo(new UserInfo(), data.getData());
                            getUserInfoByAccessToken(userInfo, callback);
                        } catch (JSONException e) {
                            error(e, callback);
                        }
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
            } catch (Exception e) {
                error(e, callback);
            }
        });
    }

    public void getUserInfoByAccessToken(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        new Thread() {
            public void run() {
                _getUserInfoByAccessToken(userInfo, callback);
            }
        }.start();
    }

    public void authByToken(UserInfo userInfo, String token, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
            if (config == null){
                callback.call(Const.ERROR_CODE_10002, "Config not found", null);
                return;
            }
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                RequestBody formBody = new FormBody.Builder()
                        .add("client_id",Authing.getAppId())
                        .add("grant_type", "http://authing.cn/oidc/grant_type/authing_token")
                        .add("token", token)
                        .add("scope", authRequest.getScope())
                        .add("prompt", "consent")
                        .add(secret == null ? "code_verifier" : "client_secret", secret == null ? authRequest.getCodeVerifier() : secret)
                        .add("redirect_uri", authRequest.getRedirectURL())
                        .build();
                Guardian.authRequest(url, "post", formBody, (data)-> {
                    ALog.d(TAG, "authByToken cost:" + (System.currentTimeMillis() - now) + "ms");
                    if (data.getCode() == 200) {
                        try {
                            UserInfo newUserInfo = UserInfo.createUserInfo(userInfo == null ? new UserInfo() : userInfo, data.getData());
                            getUserInfoByAccessToken(newUserInfo, callback);
                        } catch (JSONException e) {
                            error(e, callback);
                        }
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
            } catch (Exception e) {
                error(e, callback);
            }
        });
    }

    public void _getUserInfoByAccessToken(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
            if (config == null){
                callback.call(Const.ERROR_CODE_10002, "Config not found", null);
                return;
            }
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/me";
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.addHeader("Authorization", "Bearer " + userInfo.getAccessToken());
                Request request = builder.build();
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Call call = client.newCall(request);
                okhttp3.Response response;
                response = call.execute();
                ALog.d(TAG, "getUserInfoByAccessToken cost:" + (System.currentTimeMillis() - now) + "ms");
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                if (response.code() == 200) {
                    Response resp = new Response();
                    JSONObject json;
                    try {
                        json = new JSONObject(s);
                        resp.setCode(200);
                        resp.setData(json);
                        AuthClient.createUserInfoFromResponse(userInfo, resp, callback);
                    } catch (JSONException e) {
                        error(e, callback);
                    }
                } else {
                    ALog.w(TAG, "_getUserInfoByAccessToken failed. " + response.code() + " message:" + s);
                    callback.call(response.code(), s,null);
                }
            } catch (Exception e) {
                error(e, callback);
            }
        });
    }

    public void getNewAccessTokenByRefreshToken(String refreshToken, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (config == null){
                callback.call(Const.ERROR_CODE_10002, "Config not found", null);
                return;
            }
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                RequestBody formBody = new FormBody.Builder()
                        .add("client_id",Authing.getAppId())
                        .add("grant_type", "refresh_token")
                        .add("refresh_token", refreshToken)
                        .add(secret == null ? "code_verifier" : "client_secret", secret == null ? authRequest.getCodeVerifier() : secret)
                        .build();
                Guardian.authRequest(url, "post", formBody, (data)-> {
                    if (data.getCode() == 200) {
                        UserInfo userInfo = Authing.getCurrentUser();
                        if (userInfo == null) {
                            userInfo = new UserInfo();
                        }
                        userInfo.parseTokens(data.getData());
                        callback.call(data.getCode(), data.getMessage(), userInfo);
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
            } catch (Exception e) {
                error(e, callback);
            }
        });
    }

}
