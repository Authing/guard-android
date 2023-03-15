package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.DouYin;

public class DouYinLoginButton extends SocialLoginButton {
    public DouYinLoginButton(Context context) {
        this(context, null);
    }

    public DouYinLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DouYinLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return DouYin.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_douyin;
    }
}
