package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.social.SocialLoginButton;
import cn.authing.guard.social.handler.Baidu;

public class BaiduLoginButton extends SocialLoginButton {
    public BaiduLoginButton(Context context) {
        this(context, null);
    }

    public BaiduLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public BaiduLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.ic_authing_baidu);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Baidu.getInstance();
    }
}
