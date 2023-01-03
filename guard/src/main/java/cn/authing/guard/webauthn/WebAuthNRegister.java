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
import cn.authing.guard.data.RegistrationCredential;
import cn.authing.guard.data.RegistrationOptions;
import cn.authing.guard.data.RegistrationParams;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;
import webauthnkit.core.client.WebAuthAttestationCallback;
import webauthnkit.core.client.WebAuthManager;
import webauthnkit.core.data.AttestationConveyancePreference;
import webauthnkit.core.data.AuthenticatorAttestationResponse;
import webauthnkit.core.data.PublicKeyCredential;
import webauthnkit.core.data.UserVerificationRequirement;

public class WebAuthNRegister {

    private final FragmentActivity activity;
    private WebAuthManager webAuthManager;
    private String ticket;
    private final WebAuthNRegisterCallBack webAuthNRegisterCallBack;

    public WebAuthNRegister(FragmentActivity context, WebAuthNRegisterCallBack webAuthNRegisterCallBack) {
        this.activity = context;
        this.webAuthNRegisterCallBack = webAuthNRegisterCallBack;
        webAuthManager = new WebAuthManager(context);
    }

    public void startRegister() {
        AuthClient.bindBiometricRequest((AuthCallback<JSONObject>) (code, message, data) -> {
            if (code != 200) {
                if (webAuthNRegisterCallBack != null) {
                    webAuthNRegisterCallBack.onFailed(code, message);
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

            RegistrationOptions registrationOptions = parseOptions(data);

            if (webAuthManager == null) {
                webAuthManager = new WebAuthManager(activity);
            }

            UserVerificationRequirement userVerification = UserVerificationRequirement.Required;
            RegistrationOptions.AuthenticatorSelection authenticatorSelection = registrationOptions.getAuthenticatorSelection();
            if (authenticatorSelection != null && !TextUtils.isEmpty(authenticatorSelection.getUserVerification())) {
                String uv = authenticatorSelection.getUserVerification();
                if ("preferred".equals(uv)) {
                    userVerification = UserVerificationRequirement.Preferred;
                } else if ("discouraged".equals(uv)) {
                    userVerification = UserVerificationRequirement.Discouraged;
                }
            }

            AttestationConveyancePreference att = AttestationConveyancePreference.Direct;
            String attestation = registrationOptions.getAttestation();
            if (!TextUtils.isEmpty(attestation)) {
                if ("none".equals(attestation)) {
                    att = AttestationConveyancePreference.None;
                }
                if ("indirect".equals(attestation)) {
                    att = AttestationConveyancePreference.Indirect;
                }
            }

            webAuthManager.startAttestation(registrationOptions.getUser().getId(),
                    registrationOptions.getUser().getName(),
                    registrationOptions.getUser().getDisplayName(),
                    "",
                    registrationOptions.getRp().getId(),
                    registrationOptions.getRp().getName(),
                    registrationOptions.getChallenge(),
                    userVerification,
                    att,
                    new WebAuthAttestationCallback() {
                        @Override
                        public void onResult(@NonNull PublicKeyCredential<AuthenticatorAttestationResponse> rep) {
                            register(ticket, rep);
                        }

                        @Override
                        public void onError(@NonNull String error) {
                            if (webAuthNRegisterCallBack != null) {
                                webAuthNRegisterCallBack.onFailed(Const.ERROR_CODE_10010, error);
                            }
                        }
                    });
        });
    }

    private void register(String ticket, PublicKeyCredential<AuthenticatorAttestationResponse> rep) {
        RegistrationParams registrationParams = new RegistrationParams();
        registrationParams.setTicket(ticket);
        RegistrationCredential registrationCredential = new RegistrationCredential();
        registrationCredential.setId(rep.getId());
        registrationCredential.setRawId(Util.encodeBase64URL(rep.getRawId()));
        RegistrationCredential.Response response = new RegistrationCredential.Response();
        response.setAttestationObject(Util.encodeBase64URL(rep.getResponse().getAttestationObject()));
        response.setClientDataJSON(Util.encodeBase64URL(rep.getResponse().getClientDataJSON().getBytes()));
        registrationCredential.setResponse(response);
        registrationCredential.setType("public-key");
        registrationParams.setRegistrationCredential(registrationCredential);
        registrationParams.setAuthenticatorCode("fingerprint");

        AuthClient.bindBiometric(registrationParams, (AuthCallback<JSONObject>) (code, message, data) -> {
            if (code != 200) {
                if (webAuthNRegisterCallBack != null) {
                    webAuthNRegisterCallBack.onFailed(code, message);
                }
                return;
            }

            if (webAuthNRegisterCallBack != null) {
                webAuthNRegisterCallBack.onSuccess();
            }
        });
    }

    private RegistrationOptions parseOptions(JSONObject data) {
        RegistrationOptions registrationOptions = new RegistrationOptions();
        try {
            if (data.has("registrationOptions")) {
                JSONObject registrationOption = data.getJSONObject("registrationOptions");
                if (registrationOption.has("challenge")) {
                    registrationOptions.setChallenge(registrationOption.getString("challenge"));
                }
                if (registrationOption.has("user")) {
                    RegistrationOptions.User user = new RegistrationOptions.User();
                    JSONObject userObject = registrationOption.getJSONObject("user");
                    if (userObject.has("id")) {
                        user.setId(userObject.getString("id"));
                    }
                    if (userObject.has("name")) {
                        user.setName(userObject.getString("name"));
                    }
                    if (userObject.has("displayName")) {
                        user.setDisplayName(userObject.getString("displayName"));
                    }
                    registrationOptions.setUser(user);
                }
                if (registrationOption.has("rp")) {
                    RegistrationOptions.RP rp = new RegistrationOptions.RP();
                    JSONObject rpObject = registrationOption.getJSONObject("rp");
                    if (rpObject.has("id")) {
                        rp.setId(rpObject.getString("id"));
                    }
                    if (rpObject.has("name")) {
                        rp.setName(rpObject.getString("name"));
                    }
                    registrationOptions.setRp(rp);
                }
                if (registrationOption.has("pubKeyCredParams") && !registrationOption.isNull("pubKeyCredParams")) {
                    JSONArray pubKeyCredParams = registrationOption.getJSONArray("pubKeyCredParams");
                    registrationOptions.setPubKeyCredParams(toPubKeyCredList(pubKeyCredParams));
                }
                if (registrationOption.has("timeout")) {
                    registrationOptions.setTimeout(registrationOption.getInt("timeout"));
                }
                if (registrationOption.has("attestation")) {
                    registrationOptions.setAttestation(registrationOption.getString("attestation"));
                }
                if (registrationOption.has("excludeCredentials") && !registrationOption.isNull("excludeCredentials")) {
                    JSONArray excludeCredentials = registrationOption.getJSONArray("excludeCredentials");
                    registrationOptions.setExcludeCredentials(toExcludeCredentialsList(excludeCredentials));
                }

                if (registrationOption.has("authenticatorSelection")) {
                    RegistrationOptions.AuthenticatorSelection authenticatorSelection = new RegistrationOptions.AuthenticatorSelection();
                    JSONObject obj = registrationOption.getJSONObject("authenticatorSelection");
                    String userVerification = obj.getString("userVerification");
                    authenticatorSelection.setUserVerification(userVerification);
                    String residentKey = obj.getString("residentKey");
                    authenticatorSelection.setResidentKey(residentKey);
                    boolean requireResidentKey = obj.getBoolean("requireResidentKey");
                    authenticatorSelection.setRequireResidentKey(requireResidentKey);
                    registrationOptions.setAuthenticatorSelection(authenticatorSelection);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return registrationOptions;
    }

    private static List<RegistrationOptions.PubKeyCred> toPubKeyCredList(JSONArray array) throws JSONException {
        List<RegistrationOptions.PubKeyCred> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            RegistrationOptions.PubKeyCred pubKeyCred = new RegistrationOptions.PubKeyCred();
            JSONObject obj = array.getJSONObject(i);
            String type = obj.getString("type");
            pubKeyCred.setType(type);
            int alg = obj.getInt("alg");
            pubKeyCred.setAlg(alg);
            list.add(pubKeyCred);
        }
        return list;
    }

    private static List<RegistrationOptions.ExcludeCredentials> toExcludeCredentialsList(JSONArray array) throws JSONException {
        List<RegistrationOptions.ExcludeCredentials> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            RegistrationOptions.ExcludeCredentials excludeCredentials = new RegistrationOptions.ExcludeCredentials();
            JSONObject obj = array.getJSONObject(i);
            String id = obj.getString("id");
            excludeCredentials.setId(id);
            String type = obj.getString("type");
            excludeCredentials.setType(type);
            list.add(excludeCredentials);
        }
        return list;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (webAuthManager != null && webAuthManager.getConsentUI() != null) {
            webAuthManager.getConsentUI().onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface WebAuthNRegisterCallBack {
        void onSuccess();

        void onFailed(int code, String message);
    }
}
