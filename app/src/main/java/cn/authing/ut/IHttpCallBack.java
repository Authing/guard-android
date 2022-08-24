package cn.authing.ut;

import cn.authing.guard.data.UserInfo;

public interface IHttpCallBack {

    void showResult(String apiName, int code, String message, UserInfo data);
}
