package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.Amazon;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class AmazonLoginButton extends SocialLoginButton {
    public AmazonLoginButton(Context context) {
        this(context, null);
    }

    public AmazonLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmazonLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Amazon.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_amazon;
    }
}
