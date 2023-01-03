package cn.authing.guard.data;

import java.util.List;

public class AuthenticationOptions {

    private String challenge;
    private int timeout;
    private String userVerification;
    private String rpId;
    private List<AllowCredentials> allowCredentials;

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUserVerification() {
        return userVerification;
    }

    public void setUserVerification(String userVerification) {
        this.userVerification = userVerification;
    }

    public String getRpId() {
        return rpId;
    }

    public void setRpId(String rpId) {
        this.rpId = rpId;
    }

    public List<AllowCredentials> getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(List<AllowCredentials> allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public static class AllowCredentials{
        private String id;
        private String type;
        private String[] transports;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String[] getTransports() {
            return transports;
        }

        public void setTransports(String[] transports) {
            this.transports = transports;
        }
    }
}
