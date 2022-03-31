package cn.authing.guard.handler.register;

import cn.authing.guard.data.UserInfo;

public interface IRegisterRequestCallBack {

    void callback(int code, String message, UserInfo userInfo);
}
