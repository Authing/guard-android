package cn.authing.guard;

import cn.authing.guard.data.UserInfo;

public interface AuthCallback {
    void call(int code, String message, UserInfo userInfo);
}