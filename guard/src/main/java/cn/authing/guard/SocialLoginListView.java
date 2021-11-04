package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.authing.guard.data.UserInfo;
import cn.authing.guard.social.WechatLoginButton;
import cn.authing.guard.util.Util;

public class SocialLoginListView extends LinearLayout {

    protected Callback<UserInfo> callback;

    public SocialLoginListView(Context context) {
        this(context, null);
    }

    public SocialLoginListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialLoginListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SocialLoginListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        int length = (int) Util.dp2px(context, 44);
        int m = (int) Util.dp2px(context, 8);
        WechatLoginButton wechatLoginButton = new WechatLoginButton(context);
        LayoutParams lp = new LayoutParams(length, length);
        lp.setMargins(m, 0, m, 0);
        wechatLoginButton.setLayoutParams(lp);
        addView(wechatLoginButton);
        wechatLoginButton.setOnLoginListener((ok, data) -> {
            fireCallback(data);
        });

        WeComLoginButton weComLoginButton = new WeComLoginButton(context);
        lp = new LayoutParams(length, length);
        lp.setMargins(m, 0, m, 0);
        weComLoginButton.setLayoutParams(lp);
        addView(weComLoginButton);
        weComLoginButton.setOnLoginListener((ok, data) -> {
            fireCallback(data);
        });
    }

    public void setOnLoginListener(Callback<UserInfo> callback) {
        this.callback = callback;
    }

    private void fireCallback(UserInfo info) {
        if (callback != null) {
            if (info == null) {
                callback.call(false, null);
            } else {
                callback.call(true, info);
            }
        }
    }
}
