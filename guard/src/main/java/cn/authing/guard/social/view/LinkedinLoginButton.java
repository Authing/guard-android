package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.social.SocialLoginButton;
import cn.authing.guard.social.handler.Linkedin;

public class LinkedinLoginButton extends SocialLoginButton {
    public LinkedinLoginButton(Context context) {
        this(context, null);
    }

    public LinkedinLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkedinLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Linkedin.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_linkedin;
    }
}
