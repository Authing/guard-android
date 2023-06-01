package cn.authing.guard.data;

public class DeviceEvent {

    private String appId;
    private String userId;
    private String idTokenId;
    private String accessTokenId;
    private int logoutType;
    private long suspendEndTime;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdTokenId() {
        return idTokenId;
    }

    public void setIdTokenId(String idTokenId) {
        this.idTokenId = idTokenId;
    }

    public String getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(String accessTokenId) {
        this.accessTokenId = accessTokenId;
    }

    public int getLogoutType() {
        return logoutType;
    }

    public void setLogoutType(int logoutType) {
        this.logoutType = logoutType;
    }

    public long getSuspendEndTime() {
        return suspendEndTime;
    }

    public void setSuspendEndTime(long suspendEndTime) {
        this.suspendEndTime = suspendEndTime;
    }
}
