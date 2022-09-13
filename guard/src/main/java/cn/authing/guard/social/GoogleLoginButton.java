package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;

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
        return new Google();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_google;
    }
}
