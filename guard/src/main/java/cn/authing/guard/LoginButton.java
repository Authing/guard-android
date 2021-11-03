package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private final ImageView loadingView;

    public LoginButton(@NonNull Context context) {
        this(context, null);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TextView textView = new TextView(context);
        textView.setId(R.id.login_button_text_view);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        textView.setLayoutParams(lp);
        addView(textView);

        loadingView = new ImageView(context);
        loadingView.setImageResource(R.drawable.ic_authing_animated_loading);
        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.LEFT_OF, textView.getId());
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        loadingView.setLayoutParams(lp);
        loadingView.setVisibility(View.GONE);
        addView(loadingView);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            textView.setText(R.string.authing_login);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            textView.setTextColor(0xffffffff);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_button_background);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        setOnClickListener((v -> login()));
    }

    public void login() {
        if (loadingView.getVisibility() == View.VISIBLE) {
            return;
        }

        startLoginVisualEffect();

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
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            loginByPhoneCode(phone, code);
        } else {
            View accountET = Util.findViewByClass(this, AccountEditText.class);
            View passwordET = Util.findViewByClass(this, PasswordEditText.class);
            if (accountET != null && accountET.isShown()
                    && passwordET != null && passwordET.isShown()) {
                loginByAccount(((AccountEditText)accountET).getEditText().getText().toString(),
                        ((PasswordEditText)passwordET).getEditText().getText().toString());
            }
        }
    }

    private void loginByPhoneCode(String phone, String code) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
            fireCallback(null);
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
            fireCallback(null);
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
        stopLoginVisualEffect();

        if (callback != null) {
            if (info == null) {
                post(()-> callback.call(false, null));
            } else {
                post(()-> callback.call(true, info));
            }
        }
    }

    private void startLoginVisualEffect() {
        setEnabled(false);
        loadingView.setVisibility(View.VISIBLE);
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable)loadingView.getDrawable();
        drawable.start();
    }

    private void stopLoginVisualEffect() {
        post(()->{
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable)loadingView.getDrawable();
            drawable.stop();
            loadingView.setVisibility(View.GONE);
            setEnabled(true);
        });
    }
}
