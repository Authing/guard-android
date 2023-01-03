package cn.authing.guard.webauthn;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.AuthenticationCredential;
import cn.authing.guard.data.AuthenticationOptions;
import cn.authing.guard.data.AuthenticationParams;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;
import webauthnkit.core.client.WebAuthAssertionCallback;
import webauthnkit.core.client.WebAuthManager;
import webauthnkit.core.data.AuthenticatorAssertionResponse;
import webauthnkit.core.data.PublicKeyCredential;
import webauthnkit.core.data.UserVerificationRequirement;

public class WebAuthNAuthentication {

    private final FragmentActivity activity;
    private WebAuthManager webAuthManager;
    private String ticket;
    private final WebAuthNAuthenticationCallBack webAuthNVerifyCallBack;

    public WebAuthNAuthentication(FragmentActivity context, WebAuthNAuthenticationCallBack webAuthNVerifyCallBack) {
        this.activity = context;
        this.webAuthNVerifyCallBack = webAuthNVerifyCallBack;
        this.webAuthManager = new WebAuthManager(context);
    }

    public void startAuthentication() {
        AuthClient.biometricAuthenticationRequest((AuthCallback<JSONObject>) (code, message, data) -> {
            if (code != 200) {
                if (webAuthNVerifyCallBack != null) {
                    webAuthNVerifyCallBack.onFailed(code, message);
                }
                return;
            }

            if (data.has("ticket")) {
                try {
                    ticket = data.getString("ticket");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            AuthenticationOptions authenticationOptions = parseOptions(data);
            if (webAuthManager == null) {
                webAuthManager = new WebAuthManager(activity);
            }

            UserVerificationRequirement userVerification = UserVerificationRequirement.Required;
            String uv = authenticationOptions.getUserVerification();
            if (!TextUtils.isEmpty(uv)) {
                if ("preferred".equals(uv)) {
                    userVerification = UserVerificationRequirement.Preferred;
                } else if ("discouraged".equals(uv)) {
                    userVerification = UserVerificationRequirement.Discouraged;
                }
            }

            webAuthManager.startAssertion(authenticationOptions.getRpId(),
                    authenticationOptions.getChallenge(),
                    "",
                    userVerification,
                    new WebAuthAssertionCallback() {
                        @Override
                        public void onResult(@NonNull PublicKeyCredential<AuthenticatorAssertionResponse> rep) {
                            authentication(ticket, rep);
                        }

                        @Override
                        public void onError(@NonNull String error) {
                            if (webAuthNVerifyCallBack != null) {
                                webAuthNVerifyCallBack.onFailed(Const.ERROR_CODE_10010, error);
                            }
                        }
                    });
        });
    }

    private void authentication(String ticket, PublicKeyCredential<AuthenticatorAssertionResponse> rep) {
        AuthenticationParams authenticationParams = new AuthenticationParams();
        authenticationParams.setTicket(ticket);
        AuthenticationCredential authenticationCredential = new AuthenticationCredential();
        authenticationCredential.setId(rep.getId());
        authenticationCredential.setRawId(Util.encodeBase64URL(rep.getRawId()));
        AuthenticationCredential.Response response = new AuthenticationCredential.Response();
        response.setAuthenticatorData(Util.encodeBase64URL(rep.getResponse().getAuthenticatorData()));
        response.setClientDataJSON(Util.encodeBase64URL(rep.getResponse().getClientDataJSON().getBytes()));
        response.setSignature(Util.encodeBase64URL(rep.getResponse().getSignature()));
        response.setUserHandle(Util.encodeBase64URL(Util.encodeBase64URL(rep.getResponse().getUserHandle()).getBytes()));
        authenticationCredential.setResponse(response);
        authenticationCredential.setType("public-key");
        authenticationParams.setAuthenticationCredential(authenticationCredential);
        authenticationParams.setAuthenticatorAttachment("platform");

        AuthClient.biometricAuthentication(authenticationParams, (AuthCallback<JSONObject>) (code, message, data) -> {
            if (code != 200) {
                if (webAuthNVerifyCallBack != null) {
                    webAuthNVerifyCallBack.onFailed(code, message);
                }
                return;
            }

            if (webAuthNVerifyCallBack != null) {
                webAuthNVerifyCallBack.onSuccess(code, message, data);
            }
        });
    }

    private AuthenticationOptions parseOptions(JSONObject data) {
        AuthenticationOptions authenticationOptions = new AuthenticationOptions();
        try {
            if (data.has("authenticationOptions")) {
                JSONObject authenticationObject = data.getJSONObject("authenticationOptions");
                if (authenticationObject.has("challenge")) {
                    authenticationOptions.setChallenge(authenticationObject.getString("challenge"));
                }

                if (authenticationObject.has("allowCredentials") && !authenticationObject.isNull("allowCredentials")) {
                    JSONArray allowCredentials = authenticationObject.getJSONArray("allowCredentials");
                    authenticationOptions.setAllowCredentials(toAllowCredentialsList(allowCredentials));
                }
                if (authenticationObject.has("timeout")) {
                    authenticationOptions.setTimeout(authenticationObject.getInt("timeout"));
                }
                if (authenticationObject.has("userVerification")) {
                    authenticationOptions.setUserVerification(authenticationObject.getString("userVerification"));
                }
                if (authenticationObject.has("rpId")) {
                    authenticationOptions.setRpId(authenticationObject.getString("rpId"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return authenticationOptions;
    }

    private static List<AuthenticationOptions.AllowCredentials> toAllowCredentialsList(JSONArray array) throws JSONException {
        List<AuthenticationOptions.AllowCredentials> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            AuthenticationOptions.AllowCredentials allowCredentials = new AuthenticationOptions.AllowCredentials();
            JSONObject obj = array.getJSONObject(i);
            String id = obj.getString("id");
            allowCredentials.setId(id);
            String type = obj.getString("type");
            allowCredentials.setType(type);
            list.add(allowCredentials);
        }
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (webAuthManager != null && webAuthManager.getConsentUI() != null) {
            webAuthManager.getConsentUI().onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface WebAuthNAuthenticationCallBack {
        void onSuccess(int code, String message, JSONObject data);

        void onFailed(int code, String message);
    }
}
