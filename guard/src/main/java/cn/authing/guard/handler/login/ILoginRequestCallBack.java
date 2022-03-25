package cn.authing.guard.handler.login;

import cn.authing.guard.data.UserInfo;

public interface ILoginRequestCallBack {

     void callback(int code, String message, UserInfo userInfo);

}
