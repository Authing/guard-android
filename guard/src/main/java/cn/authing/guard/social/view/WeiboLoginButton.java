package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.social.SocialLoginButton;
import cn.authing.guard.social.handler.Weibo;

public class WeiboLoginButton extends SocialLoginButton {
    public WeiboLoginButton(Context context) {
        this(context, null);
    }

    public WeiboLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeiboLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Weibo.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_weibo;
    }
}
