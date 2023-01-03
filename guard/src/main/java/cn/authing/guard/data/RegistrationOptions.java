package cn.authing.guard.data;

import java.util.List;

public class RegistrationOptions {

    private String challenge;
    private RP rp;
    private User user;
    private List<PubKeyCred> pubKeyCredParams;
    private int timeout;
    private String attestation;
    private List<ExcludeCredentials> excludeCredentials;
    private AuthenticatorSelection authenticatorSelection;

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public RP getRp() {
        return rp;
    }

    public void setRp(RP rp) {
        this.rp = rp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<PubKeyCred> getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public void setPubKeyCredParams(List<PubKeyCred> pubKeyCredParams) {
        this.pubKeyCredParams = pubKeyCredParams;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public List<ExcludeCredentials> getExcludeCredentials() {
        return excludeCredentials;
    }

    public void setExcludeCredentials(List<ExcludeCredentials> excludeCredentials) {
        this.excludeCredentials = excludeCredentials;
    }

    public AuthenticatorSelection getAuthenticatorSelection() {
        return authenticatorSelection;
    }

    public void setAuthenticatorSelection(AuthenticatorSelection authenticatorSelection) {
        this.authenticatorSelection = authenticatorSelection;
    }

    public static class RP{
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class User{
        private String id;
        private String name;
        private String displayName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class PubKeyCred{
        private int alg;
        private String type;

        public int getAlg() {
            return alg;
        }

        public void setAlg(int alg) {
            this.alg = alg;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class ExcludeCredentials{
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

    public static class AuthenticatorSelection{
        private String userVerification;
        private String residentKey;
        private boolean requireResidentKey;

        public String getUserVerification() {
            return userVerification;
        }

        public void setUserVerification(String userVerification) {
            this.userVerification = userVerification;
        }

        public String getResidentKey() {
            return residentKey;
        }

        public void setResidentKey(String residentKey) {
            this.residentKey = residentKey;
        }

        public boolean isRequireResidentKey() {
            return requireResidentKey;
        }

        public void setRequireResidentKey(boolean requireResidentKey) {
            this.requireResidentKey = requireResidentKey;
        }
    }
}


