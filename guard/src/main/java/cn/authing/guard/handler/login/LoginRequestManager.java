package cn.authing.guard.handler.login;

import cn.authing.guard.LoginButton;
import cn.authing.guard.util.ALog;

public class LoginRequestManager {

    private final PhoneCodeLoginHandler mPhoneCodeLoginHandler;
    private final AbsLoginHandler mFirstLoginHandler;

    public LoginRequestManager(LoginButton loginButton, ILoginRequestCallBack callBack, boolean autoRegister){
        AccountLoginHandler mAccountLoginHandler = new AccountLoginHandler(loginButton, callBack, autoRegister);
        mPhoneCodeLoginHandler = new PhoneCodeLoginHandler(loginButton, callBack, autoRegister);
        EmailCodeLoginHandler mEmailCodeLoginHandler = new EmailCodeLoginHandler(loginButton, callBack, autoRegister);
        mAccountLoginHandler.setNextHandler(mPhoneCodeLoginHandler);
        mPhoneCodeLoginHandler.setNextHandler(mEmailCodeLoginHandler);
        mFirstLoginHandler = mAccountLoginHandler;
    }

    public void requestLogin(){
        if (null == mFirstLoginHandler){
            ALog.e("LoginRequestManager", "init login handler error");
            return;
        }
        mFirstLoginHandler.requestLogin();
    }

    public void setPhoneNumber(String phoneNumber){
        if (null != mPhoneCodeLoginHandler){
            mPhoneCodeLoginHandler.setPhoneNumber(phoneNumber);
        }
    }
}
