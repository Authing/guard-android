package cn.authing.guard.handler.bind;

import android.content.Context;
import android.view.View;

import cn.authing.guard.ErrorTextView;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.handler.BaseHandler;
import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.social.bind.SocialBindButton;
import cn.authing.guard.util.Util;

public abstract class AbsBindHandler extends BaseHandler {

    protected static final String TAG = AbsBindHandler.class.getSimpleName();
    protected final Context mContext;
    protected AbsBindHandler mNextHandler;
    protected SocialBindButton socialBindButton;
    protected IBindRequestCallBack mCallBack;

    public AbsBindHandler(SocialBindButton socialBindButton, IBindRequestCallBack callback) {
        this.socialBindButton = socialBindButton;
        this.mCallBack = callback;
        this.mContext = socialBindButton.getContext();
    }

    protected void setNextHandler(AbsBindHandler loginHandler) {
        mNextHandler = loginHandler;
    }

    protected void requestBind() {
        if (!bind() && null != mNextHandler) {
            mNextHandler.requestBind();
        }
    }

    abstract boolean bind();

    protected void showError(EditTextLayout editTextLayout, String errorMsg) {
        editTextLayout.showError("");
        clearError();
        if (editTextLayout.isErrorEnabled()) {
            editTextLayout.showError(errorMsg);
        } else {
            View v = Util.findViewByClass(socialBindButton, ErrorTextView.class);
            if (v != null) {
                Util.setErrorText(socialBindButton, errorMsg);
            } else {
                fireCallback(errorMsg);
            }
        }
        editTextLayout.showErrorBackGround();
    }

    protected void clearError() {
        Util.setErrorText(socialBindButton, "");
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
