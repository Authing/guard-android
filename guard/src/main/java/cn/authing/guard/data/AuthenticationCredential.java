package cn.authing.guard.data;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationCredential {

    private String id;
    private String rawId;
    private String type;
    private Response response;
    private String authenticatorAttachment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getAuthenticatorAttachment() {
        return authenticatorAttachment;
    }

    public void setAuthenticatorAttachment(String authenticatorAttachment) {
        this.authenticatorAttachment = authenticatorAttachment;
    }

    public static class Response{
        private String authenticatorData;
        private String clientDataJSON;
        private String userHandle;
        private String signature;

        public String getAuthenticatorData() {
            return authenticatorData;
        }

        public void setAuthenticatorData(String authenticatorData) {
            this.authenticatorData = authenticatorData;
        }

        public String getClientDataJSON() {
            return clientDataJSON;
        }

        public void setClientDataJSON(String clientDataJSON) {
            this.clientDataJSON = clientDataJSON;
        }

        public String getUserHandle() {
            return userHandle;
        }

        public void setUserHandle(String userHandle) {
            this.userHandle = userHandle;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                if (getAuthenticatorData() != null) {
                    jsonObject.put("authenticatorData", getAuthenticatorData());
                }
                if (getClientDataJSON() != null) {
                    jsonObject.put("clientDataJSON", getClientDataJSON());
                }
                if (getUserHandle() != null) {
                    jsonObject.put("userHandle", getUserHandle());
                }
                if (getSignature() != null) {
                    jsonObject.put("signature", getSignature());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (getId() != null) {
                jsonObject.put("id", getId());
            }
            if (getRawId() != null) {
                jsonObject.put("rawId", getRawId());
            }
            if (getType() != null) {
                jsonObject.put("type", getType());
            }
            if (getResponse() != null) {
                jsonObject.put("response", getResponse().toJSON());
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
