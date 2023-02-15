package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;

public class FaceBookLoginButton extends SocialLoginButton {
    public FaceBookLoginButton(Context context) {
        this(context, null);
    }

    public FaceBookLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceBookLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return FaceBook.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_facebook;
    }
}
