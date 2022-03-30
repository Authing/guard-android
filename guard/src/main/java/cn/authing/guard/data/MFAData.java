package cn.authing.guard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.network.Guardian;

public class MFAData implements Serializable {

    private String mfaToken;
    private String email;
    private String phone;
    private String phoneCountryCode;
    private boolean faceMfaEnabled;
    private boolean totpMfaEnabled;

    private List<String> applicationMfa = new ArrayList<>();

    public static MFAData create(JSONObject data) {
        MFAData mfaData = new MFAData();

        try {
            if (data.has("mfaToken")) {
                String v = data.getString("mfaToken");
                Guardian.MFA_TOKEN = v;
                mfaData.setMfaToken(v);
            }
            if (data.has("email")) {
                String v = data.getString("email");
                mfaData.setEmail(v);
            }
            if (data.has("phone")) {
                String v = data.getString("phone");
                mfaData.setPhone(v);
            }
            if (data.has("phoneCountryCode")) {
                String v = data.getString("phoneCountryCode");
                mfaData.setPhoneCountryCode(v);
            }
            if (data.has("faceMfaEnabled")) {
                boolean v = data.getBoolean("faceMfaEnabled");
                mfaData.setFaceMfaEnabled(v);
            }
            if (data.has("totpMfaEnabled")) {
                boolean v = data.getBoolean("totpMfaEnabled");
                mfaData.setTotpMfaEnabled(v);
            }
            if (data.has("applicationMfa")) {
                List<String> options = toMfaOptions(data.getJSONArray("applicationMfa"));
                mfaData.setApplicationMfa(options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mfaData;
    }

    public String getMfaToken() {
        return mfaToken;
    }

    public void setMfaToken(String mfaToken) {
        this.mfaToken = mfaToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public boolean isFaceMfaEnabled() {
        return faceMfaEnabled;
    }

    public void setFaceMfaEnabled(boolean faceMfaEnabled) {
        this.faceMfaEnabled = faceMfaEnabled;
    }

    public boolean isTotpMfaEnabled() {
        return totpMfaEnabled;
    }

    public void setTotpMfaEnabled(boolean totpMfaEnabled) {
        this.totpMfaEnabled = totpMfaEnabled;
    }

    public List<String> getApplicationMfa() {
        return applicationMfa;
    }

    public void setApplicationMfa(List<String> applicationMfa) {
        this.applicationMfa = applicationMfa;
    }

    private static List<String> toMfaOptions(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("mfaPolicy")) {
                String policy = obj.getString("mfaPolicy");
                list.add(policy);
            }
        }
        return list;
    }
}
