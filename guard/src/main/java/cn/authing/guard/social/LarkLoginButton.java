package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;

public class LarkLoginButton extends SocialLoginButton {
    public LarkLoginButton(Context context) {
        this(context, null);
    }

    public LarkLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LarkLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("LarkLoginButton");

        setImageResource(R.drawable.ic_authing_lark);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return new Lark();
    }
}
