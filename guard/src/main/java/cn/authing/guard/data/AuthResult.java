package cn.authing.guard.data;

import cn.authing.guard.network.AuthRequest;

public class AuthResult {

    private String code;
    private AuthRequest request;

    public AuthResult(String code, AuthRequest request) {
        this.code = code;
        this.request = request;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AuthRequest getRequest() {
        return request;
    }

    public void setRequest(AuthRequest request) {
        this.request = request;
    }
}
