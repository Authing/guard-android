package cn.authing.guard.handler.bind;

import cn.authing.guard.data.UserInfo;

public interface IBindRequestCallBack {

     void callback(int code, String message, UserInfo userInfo);

}
