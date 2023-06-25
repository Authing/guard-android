package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.Twitter;

public class TwitterLoginButton extends SocialLoginButton {
    public TwitterLoginButton(Context context) {
        this(context, null);
    }

    public TwitterLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwitterLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Twitter.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_twitter;
    }
}
