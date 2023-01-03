package cn.authing.guard.data;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationParams {

    private String ticket;
    private AuthenticationCredential authenticationCredential;
    private String authenticatorAttachment;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public AuthenticationCredential getAuthenticationCredential() {
        return authenticationCredential;
    }

    public void setAuthenticationCredential(AuthenticationCredential authenticationCredential) {
        this.authenticationCredential = authenticationCredential;
    }

    public String getAuthenticatorAttachment() {
        return authenticatorAttachment;
    }

    public void setAuthenticatorAttachment(String authenticatorAttachment) {
        this.authenticatorAttachment = authenticatorAttachment;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (getTicket() != null) {
                jsonObject.put("ticket", getTicket());
            }
            if (getAuthenticationCredential() != null) {
                jsonObject.put("authenticationCredential", getAuthenticationCredential().toJSON());
            }
            if (getAuthenticatorAttachment() != null) {
                jsonObject.put("authenticatorAttachment", getAuthenticatorAttachment());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}


