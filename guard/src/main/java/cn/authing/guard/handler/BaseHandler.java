package cn.authing.guard.handler;

import cn.authing.guard.Authing;

public class BaseHandler {

    protected Authing.AuthProtocol getAuthProtocol() {
        return Authing.getAuthProtocol();
    }
}
