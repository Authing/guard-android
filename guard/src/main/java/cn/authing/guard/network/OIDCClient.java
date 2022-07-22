package cn.authing.guard.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.ALog;
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

    public void registerByEmail(String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByEmail(authRequest, email, password, callback);
    }

    public void registerByEmailCode(String email, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByEmailCode(authRequest, email, vCode, callback);
    }

    public void registerByPhoneCode(String phone, String vCode, String password, @NotNull AuthCallback<UserInfo> callback) {
        registerByPhoneCode(null, phone, vCode, password, callback);
    }

    public void registerByPhoneCode(String phoneCountryCode, String phone, String vCode, String password, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.registerByPhoneCode(authRequest, phoneCountryCode, phone, vCode, password, callback);
    }

    public void loginByPhoneCode(String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByPhoneCode(null, phone, vCode, callback);
    }

    public void loginByPhoneCode(String phoneCountryCode, String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByPhoneCode(authRequest, phoneCountryCode, phone, vCode, callback);
    }

    public void loginByEmailCode(String email, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByEmailCode(authRequest, email, vCode, callback);
    }

    public void loginByAccount(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        AuthClient.loginByAccount(authRequest, account, password, ((c, m, data) -> {
            ALog.d(TAG, "OIDCClient.loginByAccount cost:" + (System.currentTimeMillis() - now) + "ms");
            callback.call(c, m, data);
        }));
    }

    public void loginByOneAuth(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        AuthClient.loginByOneAuth(authRequest, account, password, (AuthCallback<UserInfo>) (c, m, data) -> {
            ALog.d(TAG, "OIDCClient.loginByOneAuth cost:" + (System.currentTimeMillis() - now) + "ms");
            callback.call(c, m, data);
        });
    }

    public void loginByWechat(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWechat(authRequest, authCode, callback);
    }

    public void loginByWecom(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByWecom(authRequest, authCode, callback);
    }

    public void loginByAlipay(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByAlipay(authRequest, authCode, callback);
    }

    public void loginByLark(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByLark(authRequest, authCode, callback);
    }

    public void authByCode(String code, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                RequestBody formBody = new FormBody.Builder()
                        .add("client_id",Authing.getAppId())
                        .add("grant_type", "authorization_code")
                        .add("code", code)
                        .add("scope", authRequest.getScope())
                        .add("prompt", "consent")
                        .add(secret == null ? "code_verifier=" : "client_secret", secret == null ? authRequest.getCodeVerifier() : secret)
                        .add("redirect_uri", URLEncoder.encode(authRequest.getRedirectURL(), "utf-8"))
                        .build();
                Guardian.authRequest(url, "post", formBody, (data)-> {
                    ALog.d(TAG, "authByCode cost:" + (System.currentTimeMillis() - now) + "ms");
                    if (data.getCode() == 200) {
                        try {
                            UserInfo userInfo = UserInfo.createUserInfo(new UserInfo(), data.getData());
                            getUserInfoByAccessToken(userInfo, callback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.call(500, "Cannot parse data into UserInfo", null);
                        }
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    public void authByToken(UserInfo userInfo, String token, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                RequestBody formBody = new FormBody.Builder()
                        .add("client_id",Authing.getAppId())
                        .add("grant_type", "http://authing.cn/oidc/grant_type/authing_token")
                        .add("token", token)
                        .add("scope", authRequest.getScope())
                        .add("prompt", "consent")
                        .add(secret == null ? "code_verifier=" : "client_secret", secret == null ? authRequest.getCodeVerifier() : secret)
                        .add("redirect_uri", URLEncoder.encode(authRequest.getRedirectURL(), "utf-8"))
                        .build();
                Guardian.authRequest(url, "post", formBody, (data)-> {
                    ALog.d(TAG, "authByToken cost:" + (System.currentTimeMillis() - now) + "ms");
                    if (data.getCode() == 200) {
                        try {
                            UserInfo newUserInfo = UserInfo.createUserInfo(userInfo == null ? new UserInfo() : userInfo, data.getData());
                            getUserInfoByAccessToken(newUserInfo, callback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.call(500, "Cannot parse data into UserInfo", null);
                        }
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
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

    public void _getUserInfoByAccessToken(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
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
                        e.printStackTrace();
                        callback.call(500, s,null);
                    }
                } else {
                    ALog.w(TAG, "_getUserInfoByAccessToken failed. " + response.code() + " message:" + s);
                    callback.call(response.code(), s,null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    public void getNewAccessTokenByRefreshToken(String refreshToken, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                RequestBody formBody = new FormBody.Builder()
                        .add("client_id",Authing.getAppId())
                        .add("grant_type", "refresh_token")
                        .add("refresh_token", refreshToken)
                        .add(secret == null ? "code_verifier=" : "client_secret", secret == null ? authRequest.getCodeVerifier() : secret)
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
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

}
