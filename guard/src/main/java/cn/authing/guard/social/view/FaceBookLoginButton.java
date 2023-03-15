package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.FaceBook;

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
