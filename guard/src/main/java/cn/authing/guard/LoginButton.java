package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.CustomEventButton;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.util.Util;

public class LoginButton extends CustomEventButton {

    public LoginButton(@NonNull Context context) {
        this(context, null);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_login);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(0xffffffff);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_button_background);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        setOnClickListener((v -> login()));
    }

    private void login() {
        Config config = Authing.getPublicConfig();
        if (config == null) {
            fireCallback(null);
            return;
        }

        View phoneNumberET = Util.findViewByClass(this, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            final String phone = ((PhoneNumberEditText) phoneNumberET).getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText();
            loginByPhoneCode(phone, code);
        } else {
            View accountET = Util.findViewByClass(this, AccountEditText.class);
            View passwordET = Util.findViewByClass(this, PasswordEditText.class);
            if (accountET != null && accountET.isShown()
                    && passwordET != null && passwordET.isShown()) {
                loginByAccount(((AccountEditText)accountET).getText().toString(),
                        ((PasswordEditText)passwordET).getText().toString());
            }
        }
    }

    private void loginByPhoneCode(String phone, String code) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
            return;
        }

        try {
            Config config = Authing.getPublicConfig();
            JSONObject body = new JSONObject();
            body.put("phone", phone);
            body.put("code", code);
            String url = "https://" + config.getIdentifier() + ".authing.cn/api/v2/login/phone-code";
            Guardian.post(url, body, (data)->{
                if (data != null) {
                    UserInfo userInfo;
                    try {
                        userInfo = createUserInfo(data.getData());
                        fireCallback(userInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        fireCallback(null);
                    }
                } else {
                    fireCallback(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            fireCallback(null);
        }
    }

    private void loginByAccount(String account, String password) {
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            return;
        }

        Config config = Authing.getPublicConfig();
        try {
            password = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("account", account);
            body.put("password", password);

            String url = "https://" + config.getIdentifier() + ".authing.cn/api/v2/login/account";
            Guardian.post(url, body, (data)->{
                if (data != null) {
                    UserInfo userInfo;
                    try {
                        userInfo = createUserInfo(data.getData());
                        fireCallback(userInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        fireCallback(null);
                    }
                } else {
                    fireCallback(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            fireCallback(null);
        }
    }

    private UserInfo createUserInfo(JSONObject data) throws JSONException {
        String id = data.getString("id");
        String username = data.getString("username");
        String phone = data.getString("phone");
        String email = data.getString("email");
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setName(username);
        userInfo.setPhone_number(phone);
        userInfo.setEmail(email);
        return userInfo;
    }

    private void fireCallback(UserInfo info) {
        if (callback != null) {
            if (info == null) {
                post(()-> callback.call(false, null));
            } else {
                post(()-> callback.call(true, info));
            }
        }
    }
}
