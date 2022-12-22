package cn.authing.guard.handler.bind;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.SocialBindData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.social.bind.BindAccountEditText;
import cn.authing.guard.social.bind.SocialBindButton;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class AccountBindHandler extends AbsBindHandler {

    public AccountBindHandler(SocialBindButton socialBindButton, IBindRequestCallBack callback) {
        super(socialBindButton, callback);
    }

    @Override
    protected boolean bind() {
        View accountET = Util.findViewByClass(socialBindButton, BindAccountEditText.class);
        View passwordET = Util.findViewByClass(socialBindButton, PasswordEditText.class);
        if (accountET != null && accountET.isShown()
                && passwordET != null && passwordET.isShown()) {
            boolean showError = false;
            AccountEditText accountEditText = ((AccountEditText) accountET);
            final String account = accountEditText.getText().toString();
            if (TextUtils.isEmpty(account)) {
                showError(accountEditText, mContext.getString(R.string.authing_account_empty));
                showError = true;
            }

            PasswordEditText passwordEditText = ((PasswordEditText) passwordET);
            final String password = passwordEditText.getText().toString();
            if (TextUtils.isEmpty(password)) {
                showError(passwordEditText, mContext.getString(R.string.authing_password_empty));
                showError = true;
            }

            if (showError){
                return false;
            }

            socialBindButton.startLoadingVisualEffect();
            loginByAccount(account, password);
            return true;
        }
        return false;
    }

    private void loginByAccount(String account, String password) {
        if (mContext instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) mContext;
            AuthFlow flow = activity.getFlow();
            UserInfo userInfo = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
            if (userInfo != null && userInfo.getSocialBindData() != null) {
                SocialBindData socialBindData = userInfo.getSocialBindData();
                AuthClient.bindWechatByAccount(socialBindData.getKey(), account, password, this::fireCallback);
                ALog.d(TAG, "bind by account");
            }
        }
    }
}
