package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.Finger;

public class FingerLoginButton extends SocialLoginButton {
    public FingerLoginButton(Context context) {
        this(context, null);
    }

    public FingerLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FingerLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("FingerLoginButton");

    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return new Finger();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_mfa_finger;
    }
}
