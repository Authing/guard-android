package cn.authing.guard.data;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationCredential {

    private String id;
    private String rawId;
    private String type;
    private Response response;

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

    public static class Response{
        private String attestationObject;
        private String clientDataJSON;

        public String getAttestationObject() {
            return attestationObject;
        }

        public void setAttestationObject(String attestationObject) {
            this.attestationObject = attestationObject;
        }

        public String getClientDataJSON() {
            return clientDataJSON;
        }

        public void setClientDataJSON(String clientDataJSON) {
            this.clientDataJSON = clientDataJSON;
        }

        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            try {
                if (getAttestationObject() != null) {
                    jsonObject.put("attestationObject", getAttestationObject());
                }
                if (getClientDataJSON() != null) {
                    jsonObject.put("clientDataJSON", getClientDataJSON());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
