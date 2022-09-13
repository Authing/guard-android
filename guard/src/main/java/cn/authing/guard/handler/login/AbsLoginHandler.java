package cn.authing.guard.handler.login;

import android.content.Context;
import android.view.View;

import cn.authing.guard.ErrorTextView;
import cn.authing.guard.LoginButton;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.handler.BaseHandler;
import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.util.Util;

public abstract class AbsLoginHandler extends BaseHandler {

    protected static final String TAG = AbsLoginHandler.class.getSimpleName();
    protected AbsLoginHandler mNextHandler;
    protected LoginButton loginButton;
    protected final Context mContext;
    protected ILoginRequestCallBack mCallBack;

    public AbsLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        this.loginButton = loginButton;
        this.mCallBack = callback;
        this.mContext = loginButton.getContext();
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

    protected void showError(EditTextLayout editTextLayout, String errorMsg) {
        editTextLayout.showError("");
        clearError();
        if (editTextLayout.isErrorEnabled()) {
            editTextLayout.showError(errorMsg);
        } else {
            View v = Util.findViewByClass(loginButton, ErrorTextView.class);
            if (v != null) {
                Util.setErrorText(loginButton, errorMsg);
            } else {
                fireCallback(errorMsg);
            }
        }
        editTextLayout.showErrorBackGround();
    }

    protected void clearError() {
        Util.setErrorText(loginButton, "");
    }

    protected void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    protected void fireCallback(int code, String message, UserInfo userInfo) {
        if (null != mCallBack) {
            mCallBack.callback(code, message, userInfo);
        }
    }

}
