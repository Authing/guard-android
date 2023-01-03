package cn.authing.guard.data;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationParams {

    private String ticket;
    private RegistrationCredential registrationCredential;
    private String authenticatorCode;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public RegistrationCredential getRegistrationCredential() {
        return registrationCredential;
    }

    public void setRegistrationCredential(RegistrationCredential registrationCredential) {
        this.registrationCredential = registrationCredential;
    }

    public String getAuthenticatorCode() {
        return authenticatorCode;
    }

    public void setAuthenticatorCode(String authenticatorCode) {
        this.authenticatorCode = authenticatorCode;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (getTicket() != null) {
                jsonObject.put("ticket", getTicket());
            }
            if (getRegistrationCredential() != null) {
                jsonObject.put("registrationCredential", getRegistrationCredential().toJSON());
            }
            if (getAuthenticatorCode() != null) {
                jsonObject.put("authenticatorCode", getAuthenticatorCode());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}


