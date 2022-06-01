package cn.authing.guard.network;

import android.net.Uri;

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
import cn.authing.guard.util.Const;
import cn.authing.guard.util.PKCE;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OIDCClient {

    private static final String TAG = "OIDCClient";

    public static void buildAuthorizeUrl(AuthRequest authRequest, Callback<String> callback) {
        Authing.getPublicConfig(config -> {
            if (config != null && config.getRedirectUris().size() > 0) {
                authRequest.setRedirectURL(config.getRedirectUris().get(0));
            }
            callback.call(true, buildAuthorizeUrl(config, authRequest));
        });
    }

    public static String buildAuthorizeUrl(Config config, AuthRequest authRequest) {
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

    static void prepareLogin(Config config, @NotNull AuthCallback<AuthRequest> callback) {
        new Thread() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                AuthRequest authData = new AuthRequest();
                if (config.getRedirectUris().size() > 0) {
                    authData.setRedirectURL(config.getRedirectUris().get(0));
                }
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/auth?_authing_lang="
                        + Util.getLangHeader()
                        + "&app_id=" + Authing.getAppId()
                        + "&client_id=" + Authing.getAppId()
                        + "&nonce=" + authData.getNonce()
                        + "&redirect_uri=" + authData.getRedirectURL()
                        + "&response_type=" + authData.getResponse_type()
                        + "&scope=" + authData.getScope()
                        + "&prompt=consent"
                        + "&state=" + authData.getState()
                        + "&code_challenge=" + authData.getCodeChallenge()
                        + "&code_challenge_method=" + PKCE.getCodeChallengeMethod();
                Request.Builder builder = new Request.Builder();
                builder.url(url);

                Request request = builder.build();
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .followRedirects(false)
                        .followSslRedirects(false)
                        .build();
                Call call = client.newCall(request);
                okhttp3.Response response;
                try {
                    response = call.execute();
                    if (response.code() == 302) {
                        CookieManager.addCookies(response);
                        String location = response.header("location");
                        String uuid = Uri.parse(location).getLastPathSegment();
                        authData.setUuid(uuid);
                        long delta = System.currentTimeMillis() - now;
                        ALog.d(TAG, "prepareLogin cost:" + delta + "ms");
                        callback.call(200, "", authData);
                    } else {
                        String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                        ALog.w(TAG, "OIDC prepare login failed. " + response.code() + " message:" + s);
                        callback.call(response.code(), s,null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void registerByEmail(String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.registerByEmail(authRequest, email, password, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void registerByEmailCode(String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.registerByEmailCode(authRequest, email, password, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void registerByPhoneCode(String phone, String vCode, String password, @NotNull AuthCallback<UserInfo> callback) {
        registerByPhoneCode(null, phone, vCode, password, callback);
    }

    public static void registerByPhoneCode(String phoneCountryCode, String phone, String vCode, String password, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.registerByPhoneCode(authRequest, phoneCountryCode, phone, vCode, password, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByPhoneCode(String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByPhoneCode(null, phone, vCode, callback);
    }

    public static void loginByPhoneCode(String phoneCountryCode, String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByPhoneCode(authRequest, phoneCountryCode, phone, vCode, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByEmailCode(String email, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByEmailCode(authRequest, email, vCode, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByAccount(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByAccount(authRequest, account, password, ((c, m, data) -> {
                    ALog.d(TAG, "OIDCClient.loginByAccount cost:" + (System.currentTimeMillis() - now) + "ms");
                    callback.call(c, m, data);
                }));
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByOneAuth(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByOneAuth(authRequest, account, password, (AuthCallback<UserInfo>) (c, m, data) -> {
                    ALog.d(TAG, "OIDCClient.loginByOneAuth cost:" + (System.currentTimeMillis() - now) + "ms");
                    callback.call(c, m, data);
                });
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByWechat(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByWechat(authRequest, authCode, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByWecom(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByWecom(authRequest, authCode, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByAlipay(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByAlipay(authRequest, authCode, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void loginByLark(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByLark(authRequest, authCode, callback);
            } else {
                callback.call(code, message, null);
            }
        }));
    }

    public static void oidcInteraction(AuthRequest authData, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/interaction/oidc/" + authData.getUuid() + "/login";
                String body = "token=" + authData.getToken();
                _oidcInteraction(url, authData, body, callback);
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    private static void _oidcInteraction(String url, AuthRequest authData, String body, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        RequestBody requestBody = RequestBody.create(body, Const.FORM);
        builder.post(requestBody);
        String cookie = CookieManager.getCookie();
        if (!Util.isNull(cookie)) {
            builder.addHeader("cookie", cookie);
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            ALog.d(TAG, "_oidcInteraction cost:" + (System.currentTimeMillis() - now) + "ms");
            if (response.code() == 302) {
                CookieManager.addCookies(response);
                String location = response.header("location");
                oidcLogin(location, authData, callback);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                ALog.w(TAG, "oidcInteraction failed. " + response.code() + " message:" + s);
                callback.call(response.code(), s,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void oidcLogin(String url, AuthRequest authData, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        String cookie = CookieManager.getCookie();
        if (!Util.isNull(cookie)) {
            builder.addHeader("cookie", cookie);
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            ALog.d(TAG, "oidcLogin cost:" + (System.currentTimeMillis() - now) + "ms");
            if (response.code() == 302) {
                CookieManager.addCookies(response);
                String location = response.header("location");
                Uri uri = Uri.parse(location);
                String authCode = uri.getQueryParameter("code");
                if (authCode != null) {
                    OIDCClient.authByCode(authCode, authData, callback);
                } else if (uri.getLastPathSegment().equals("authz")) {
                    url = request.url().scheme() + "://" + request.url().host() + "/interaction/oidc/" + authData.getUuid() + "/confirm";
                    _oidcInteractionScopeConfirm(url, authData, callback);
                } else {
                    // might be another redirect to this api itself
                    url = request.url().scheme() + "://" + request.url().host() + location;
                    oidcLogin(url, authData, callback);
                }
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                ALog.w(TAG, "oidcLogin failed. " + response.code() + " message:" + s);
                callback.call(response.code(), s,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, e.toString(),null);
        }
    }

    private static void _oidcInteractionScopeConfirm(String url, AuthRequest authData, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        String body = authData.getScopesAsConsentBody();
        RequestBody requestBody = RequestBody.create(body, Const.FORM);
        builder.post(requestBody);
        String cookie = CookieManager.getCookie();
        if (!Util.isNull(cookie)) {
            builder.addHeader("cookie", cookie);
        }

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            ALog.d(TAG, "_oidcInteractionScopeConfirm cost:" + (System.currentTimeMillis() - now) + "ms");
            if (response.code() == 302) {
                CookieManager.addCookies(response);
                String location = response.header("location");
                oidcLogin(location, authData, callback);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                ALog.w(TAG, "oidcInteraction failed. " + response.code() + " message:" + s);
                callback.call(response.code(), s,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void authByCode(String code, AuthRequest authRequest, @NotNull AuthCallback<UserInfo> callback) {
        long now = System.currentTimeMillis();
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String secret = authRequest.getClientSecret();
                String body = "client_id="+Authing.getAppId()
                        + "&grant_type=authorization_code"
                        + "&code=" + code
                        + "&scope=" + authRequest.getScope()
                        + "&prompt=" + "consent"
                        + (secret == null ? "&code_verifier=" + authRequest.getCodeVerifier() : "&client_secret=" + secret)
                        + "&redirect_uri=" + URLEncoder.encode(authRequest.getRedirectURL(), "utf-8");
                Guardian.authRequest(url, "post", body, (data)-> {
                    ALog.d(TAG, "authByCode cost:" + (System.currentTimeMillis() - now) + "ms");
                    if (data.getCode() == 200) {
                        try {
                            UserInfo userInfo = UserInfo.createUserInfo(new UserInfo(), data.getData());
                            OIDCClient.getUserInfoByAccessToken(userInfo, callback);
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

    public static void getUserInfoByAccessToken(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        new Thread() {
            public void run() {
                _getUserInfoByAccessToken(userInfo, callback);
            }
        }.start();
    }

    public static void _getUserInfoByAccessToken(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
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

    public static void getNewAccessTokenByRefreshToken(String refreshToken, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/oidc/token";
                String body = "client_id=" + Authing.getAppId()
                        + "&grant_type=refresh_token"
                        + "&refresh_token=" + refreshToken;
                Guardian.authRequest(url, "post", body, (data)-> {
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

    public static void getAuthCode(@NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            authRequest.setToken(Authing.getCurrentUser().getIdToken());
            oidcInteraction(authRequest, callback);
        }));
    }
}
