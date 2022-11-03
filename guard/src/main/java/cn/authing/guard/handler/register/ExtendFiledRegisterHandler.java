package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.Authing;
import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.RegisterExtendFiledEditText;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class ExtendFiledRegisterHandler extends AbsRegisterHandler {

    public ExtendFiledRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callBack) {
        super(loginButton, callBack);
    }

    @Override
    protected boolean register() {
        View extendFiledET = Util.findViewByClass(mRegisterButton, RegisterExtendFiledEditText.class);
        View passwordET = Util.findViewByClass(mRegisterButton, PasswordEditText.class);
        if ((extendFiledET != null && extendFiledET.isShown())
                && passwordET != null && passwordET.isShown()) {
            final String account = ((RegisterExtendFiledEditText) extendFiledET).getText().toString();
            final String password = ((PasswordEditText) passwordET).getText().toString();
            String fieldName = (String) extendFiledET.getTag();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                Util.setErrorText(mRegisterButton, "Account or password is invalid");
                fireCallback("Account or password is invalid");
                return false;
            }

            View v = Util.findViewByClass(mRegisterButton, PasswordConfirmEditText.class);
            if (v != null) {
                PasswordConfirmEditText passwordConfirmEditText = (PasswordConfirmEditText) v;
                if (!password.equals(passwordConfirmEditText.getText().toString())) {
                    Util.setErrorText(mRegisterButton, mContext.getResources().getString(R.string.authing_password_not_match));
                    fireCallback(mContext.getResources().getString(R.string.authing_password_not_match));
                    return false;
                }
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByExtendFiled(fieldName, account, password);
            return true;
        }
        return false;
    }

    private void registerByExtendFiled(String fieldName, String account, String password) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.registerByExtendField(fieldName, account, password, null, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().registerByExtendField(fieldName, account, password, null, this::fireCallback);
        }
        ALog.d(TAG, "register by extend filed");
    }

}
