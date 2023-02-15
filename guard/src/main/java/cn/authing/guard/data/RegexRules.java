package cn.authing.guard.data;

import java.io.Serializable;

public class RegexRules implements Serializable {

    private String key;
    private String appLevel;
    private String userPoolLevel;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAppLevel() {
        return appLevel;
    }

    public void setAppLevel(String appLevel) {
        this.appLevel = appLevel;
    }

    public String getUserPoolLevel() {
        return userPoolLevel;
    }

    public void setUserPoolLevel(String userPoolLevel) {
        this.userPoolLevel = userPoolLevel;
    }
}
