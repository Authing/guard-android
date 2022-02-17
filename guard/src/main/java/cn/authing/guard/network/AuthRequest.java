package cn.authing.guard.network;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.Authing;
import cn.authing.guard.util.PKCE;
import cn.authing.guard.util.Util;

public class AuthRequest {
    private String client_id;
    private String finish_login_url;
    private String nonce;
    private String redirect_url;
    private String response_type;
    private String scope;
    private String state;
    private String uuid;
    private String _authing_lang;
    private String codeVerifier;
    private String codeChallenge;

    private String token;

    public AuthRequest() {
        client_id = Authing.getAppId();
        nonce = Util.randomString(10);
        redirect_url = "https://console.authing.cn/console/get-started/" + Authing.getAppId();
        response_type = "code";
        scope = "openid profile email phone username address offline_access role extended_fields";
        state = Util.randomString(10);
        _authing_lang = Util.getLangHeader();
        codeVerifier = PKCE.generateCodeVerifier();
        codeChallenge = PKCE.generateCodeChallenge(codeVerifier);
    }

    public JSONObject asJSONObject() {
        JSONObject o = new JSONObject();
        try {
            o.put("app_id", client_id);
            o.put("client_id", client_id);
            o.put("finish_login_url", finish_login_url);
            o.put("nonce", nonce);
            o.put("redirect_url", redirect_url);
            o.put("scope", scope);
            o.put("state", state);
            o.put("uuid", uuid);
            o.put("_authing_lang", _authing_lang);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return o;
    }

    public String getScopesAsConsentBody() {
        String[] scopes = scope.split(" ");
        if (scopes.length == 0) {
            return scope;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : scopes) {
            sb.append("consent[acceptedScopes][]=");
            sb.append(s);
            sb.append("&");
        }
        return sb.toString();
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getFinish_login_url() {
        return finish_login_url;
    }

    public void setFinish_login_url(String finish_login_url) {
        this.finish_login_url = finish_login_url;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getRedirectURL() {
        return redirect_url;
    }

    public void setRedirectURL(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getResponse_type() {
        return response_type;
    }

    public void setResponse_type(String response_type) {
        this.response_type = response_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
        finish_login_url = "/interaction/oidc/" + uuid + "/login";
    }

    public String get_authing_lang() {
        return _authing_lang;
    }

    public void set_authing_lang(String _authing_lang) {
        this._authing_lang = _authing_lang;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
