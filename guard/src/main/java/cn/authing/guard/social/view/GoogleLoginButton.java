package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.Google;

public class GoogleLoginButton extends SocialLoginButton {

    public GoogleLoginButton(Context context) {
        this(context, null);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected SocialAuthenticator createAuthenticator() {
        return Google.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_google;
    }
}
