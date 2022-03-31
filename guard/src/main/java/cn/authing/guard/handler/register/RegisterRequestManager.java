package cn.authing.guard.handler.register;

import cn.authing.guard.RegisterButton;
import cn.authing.guard.util.ALog;

public class RegisterRequestManager {

    private final AbsRegisterHandler mFirstRegisterHandler;
    private final EmailRegisterHandler mEmailRegisterHandler;

    public RegisterRequestManager(RegisterButton registerButton, IRegisterRequestCallBack callBack){
        mEmailRegisterHandler = new EmailRegisterHandler(registerButton, callBack);
        PhoneCodeRegisterHandler phoneCodeRegisterHandler = new PhoneCodeRegisterHandler(registerButton, callBack);
        mEmailRegisterHandler.setNextHandler(phoneCodeRegisterHandler);
        mFirstRegisterHandler = mEmailRegisterHandler;
    }

    public void requestRegister(){
        if (null == mFirstRegisterHandler){
            ALog.e("RegisterRequestManager", "init register handler error");
            return;
        }
        mFirstRegisterHandler.requestRegister();
    }

    public void setEmail(String email){
        if (null != mEmailRegisterHandler){
            mEmailRegisterHandler.setEmail(email);
        }
    }
}
