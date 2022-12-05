package cn.authing.guard.handler.bind;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.EmailEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.SocialBindData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.social.bind.SocialBindButton;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class EmailCodeBindHandler extends AbsBindHandler {

    private String emailNumber;
    private String emailCode;

    public EmailCodeBindHandler(SocialBindButton socialBindButton, IBindRequestCallBack callback) {
        super(socialBindButton, callback);
    }

    @Override
    protected boolean bind() {
        View emailNumberET = Util.findViewByClass(socialBindButton, EmailEditText.class);
        View emailCodeET = Util.findViewByClass(socialBindButton, VerifyCodeEditText.class);
        if (emailNumberET != null && emailNumberET.isShown()) {
            EmailEditText emailNumberEditText = (EmailEditText) emailNumberET;
            emailNumber = emailNumberEditText.getText().toString();
        }
        if (emailCodeET != null && emailCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText) emailCodeET;
            emailCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(emailNumber) && !TextUtils.isEmpty(emailCode)) {
            socialBindButton.startLoadingVisualEffect();
            loginByEmailCode(emailNumber, emailCode);
            return true;
        }

        if (emailNumberET != null && emailNumberET.isShown()
                && emailCodeET != null && emailCodeET.isShown()) {
            EmailEditText emailNumberEditText = (EmailEditText) emailNumberET;
            if (!emailNumberEditText.isContentValid()) {
                fireCallback(mContext.getString(R.string.authing_invalid_phone_number));
                return false;
            }

            final String email = emailNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) emailCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                fireCallback(mContext.getString(R.string.authing_incorrect_verify_code));
                return false;
            }

            socialBindButton.startLoadingVisualEffect();
            loginByEmailCode(email, code);
            return true;
        }
        return false;
    }

    private void loginByEmailCode(String email, String verifyCode) {
        if (mContext instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) mContext;
            AuthFlow flow = activity.getFlow();
            UserInfo userInfo = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
            if (userInfo != null && userInfo.getSocialBindData() != null) {
                SocialBindData socialBindData = userInfo.getSocialBindData();
                AuthClient.bindWechatByEmailCode(socialBindData.getKey(), email, verifyCode, this::fireCallback);
                ALog.d(TAG, "bind by email code");
            }
        }
    }
}
