package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.social.handler.Lark;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class LarkLoginButton extends SocialLoginButton {
    public LarkLoginButton(Context context) {
        this(context, null);
    }

    public LarkLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LarkLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("LarkLoginButton");
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Lark.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_lark;
    }
}
