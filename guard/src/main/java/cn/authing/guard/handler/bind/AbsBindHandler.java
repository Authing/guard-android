package cn.authing.guard.handler.bind;

import android.content.Context;

import cn.authing.guard.data.UserInfo;
import cn.authing.guard.handler.BaseHandler;
import cn.authing.guard.social.bind.SocialBindButton;

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

    protected void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    protected void fireCallback(int code, String message, UserInfo userInfo) {
        if (null != mCallBack) {
            mCallBack.callback(code, message, userInfo);
        }
    }

}
