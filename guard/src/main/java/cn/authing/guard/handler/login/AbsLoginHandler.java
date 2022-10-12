package cn.authing.guard.handler.login;

import android.content.Context;

import cn.authing.guard.LoginButton;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.handler.BaseHandler;

public abstract class AbsLoginHandler extends BaseHandler {

    protected static final String TAG = AbsLoginHandler.class.getSimpleName();
    protected AbsLoginHandler mNextHandler;
    protected LoginButton loginButton;
    protected final Context mContext;
    protected ILoginRequestCallBack mCallBack;
    private final boolean autoRegister;

    public AbsLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback, boolean autoRegister) {
        this.loginButton = loginButton;
        this.mCallBack = callback;
        this.mContext = loginButton.getContext();
        this.autoRegister = autoRegister;
    }

    protected void setNextHandler(AbsLoginHandler loginHandler) {
        mNextHandler = loginHandler;
    }

    protected void requestLogin() {
        if (!login() && null != mNextHandler) {
            mNextHandler.requestLogin();
        }
    }

    abstract boolean login();

    protected void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    protected void fireCallback(int code, String message, UserInfo userInfo) {
        if (null != mCallBack) {
            mCallBack.callback(code, message, userInfo);
        }
    }

    public boolean isAutoRegister() {
        return autoRegister;
    }

}
