package cn.authing.guard.handler.bind;

import android.text.TextUtils;
import android.view.View;

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
            final String account = ((BindAccountEditText) accountET).getText().toString();
            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                fireCallback(accountET.getContext().getString(R.string.authing_account_or_password_empty));
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
