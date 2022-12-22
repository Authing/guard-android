package cn.authing.guard.handler.bind;

import cn.authing.guard.social.bind.SocialBindButton;
import cn.authing.guard.util.ALog;

public class BindRequestManager {

    private final PhoneCodeBindHandler mPhoneCodeBindHandler;
    private final AbsBindHandler mFirstBindHandler;

    public BindRequestManager(SocialBindButton socialBindButton, IBindRequestCallBack callBack) {
        AccountBindHandler mAccountBindHandler = new AccountBindHandler(socialBindButton, callBack);
        mPhoneCodeBindHandler = new PhoneCodeBindHandler(socialBindButton, callBack);
        EmailCodeBindHandler mEmailCodeBindHandler = new EmailCodeBindHandler(socialBindButton, callBack);
        mAccountBindHandler.setNextHandler(mPhoneCodeBindHandler);
        mPhoneCodeBindHandler.setNextHandler(mEmailCodeBindHandler);
        mFirstBindHandler = mAccountBindHandler;
    }

    public void requestBind() {
        if (null == mFirstBindHandler) {
            ALog.e("BindRequestManager", "init bind handler error");
            return;
        }
        mFirstBindHandler.requestBind();
    }

    public void setPhoneNumber(String phoneNumber) {
        if (null != mPhoneCodeBindHandler) {
            mPhoneCodeBindHandler.setPhoneNumber(phoneNumber);
        }
    }
}
