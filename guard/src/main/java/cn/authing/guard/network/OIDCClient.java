package cn.authing.guard.network;

import android.net.Uri;
import android.util.Log;

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
import cn.authing.guard.util.Const;
import cn.authing.guard.util.PKCE;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OIDCClient {

    private static final String TAG = "AuthClientInternal";

    public static void buildAuthorizeUrl(AuthRequest authRequest, Callback<String> callback) {
        Authing.getPublicConfig(config -> callback.call(true, buildAuthorizeUrl(config, authRequest)));
    }

    public static String buildAuthorizeUrl(Config config, AuthRequest authRequest) {
        return Authing.getSchema() + "://" + Util.getHost(config) + "/oidc/auth?_authing_lang="
                + Util.getLangHeader()
                + "&app_id=" + Authing.getAppId()
                + "&client_id=" + Authing.getAppId()
                + "&nonce=" + authRequest.getNonce()
                + "&redirect_uri=" + authRequest.getRedirectURL()
                + "&response_type=" + authRequest.getResponse_type()
                + "&scope=" + authRequest.getScope()
                + "&prompt=consent"
                + "&state=" + authRequest.getState()
                + "&code_challenge=" + authRequest.getCodeChallenge()
                + "&code_challenge_method=" + PKCE.getCodeChallengeMethod();
    }

    static void prepareLogin(Config config, @NotNull AuthCallback<AuthRequest> callback) {
        new Thread() {
            @Override
            public void run() {
                AuthRequest authData = new AuthRequest();
                if (config.getRedirectUris().size() > 0) {
                    authData.setRedirectURL(config.getRedirectUris().get(0));
                }
                String url = Authing.getSchema() + "://" + Util.getHost(config) + "/oidc/auth?_authing_lang="
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
                        callback.call(200, "", authData);
                    } else {
                        String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                        Log.w(TAG, "OIDC prepare login failed. " + response.code() + " message:" + s);
                        callback.call(response.code(), s,null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void loginByPhoneCode(String phone, String vCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByPhoneCode(authRequest, phone, vCode, callback);
            } else {
                callback.call(code, message, null);
            }
        })));
    }

    public static void loginByAccount(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> OIDCClient.prepareLogin(config, (code, message, authRequest) -> {
            if (code == 200) {
                AuthClient.loginByAccount(authRequest, account, password, callback);
            } else {
                callback.call(code, message, null);
            }
        })));
    }

    public static void oidcInteraction(AuthRequest authData, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String url = Authing.getSchema() + "://" + Util.getHost(config) + "/interaction/oidc/" + authData.getUuid() + "/login";
                String body = "token=" + authData.getToken();
                _oidcInteraction(url, authData, body, callback);
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    private static void _oidcInteraction(String url, AuthRequest authData, String body, @NotNull AuthCallback<UserInfo> callback) {
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
            if (response.code() == 302) {
                CookieManager.addCookies(response);
                String location = response.header("location");
                oidcLogin(location, authData, callback);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                Log.w(TAG, "oidcInteraction failed. " + response.code() + " message:" + s);
                callback.call(response.code(), s,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void oidcLogin(String url, AuthRequest authData, @NotNull AuthCallback<UserInfo> callback) {
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
                Log.w(TAG, "oidcLogin failed. " + response.code() + " message:" + s);
                callback.call(response.code(), s,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, e.toString(),null);
        }
    }

    private static void _oidcInteractionScopeConfirm(String url, AuthRequest authData, @NotNull AuthCallback<UserInfo> callback) {
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
            if (response.code() == 302) {
                CookieManager.addCookies(response);
                String location = response.header("location");
                oidcLogin(location, authData, callback);
            } else {
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                Log.w(TAG, "oidcInteraction failed. " + response.code() + " message:" + s);
                callback.call(response.code(), s,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void authByCode(String code, AuthRequest authRequest, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getSchema() + "://" + Util.getHost(config) + "/oidc/token";
                String body = "client_id="+Authing.getAppId()
                        + "&grant_type=authorization_code"
                        + "&code=" + code
                        + "&scope=" + authRequest.getScope()
                        + "&prompt=" + "consent"
                        + "&code_verifier=" + authRequest.getCodeVerifier()
                        + "&redirect_uri=" + URLEncoder.encode(authRequest.getRedirectURL(), "utf-8");
                Guardian.authRequest(url, "post", body, (data)-> {
                    if (data.getCode() == 200) {
                        AuthClient.createUserInfoFromResponse(data, (c, m, info) -> OIDCClient.getUserInfoByAccessToken(info, callback));
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
        Authing.getPublicConfig(config -> {
            try {
                String url = Authing.getSchema() + "://" + Util.getHost(config) + "/oidc/me";
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                builder.addHeader("Authorization", "Bearer " + userInfo.getAccessToken());
                Request request = builder.build();
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Call call = client.newCall(request);
                okhttp3.Response response;
                response = call.execute();
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
                    Log.w(TAG, "_getUserInfoByAccessToken failed. " + response.code() + " message:" + s);
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
                String url = Authing.getSchema() + "://" + Util.getHost(config) + "/oidc/token";
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
}
