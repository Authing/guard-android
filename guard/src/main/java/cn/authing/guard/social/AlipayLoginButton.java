package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;

public class AlipayLoginButton extends SocialLoginButton {
    public AlipayLoginButton(Context context) {
        this(context, null);
    }

    public AlipayLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public AlipayLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AlipayLoginButton");

        setImageResource(R.drawable.ic_authing_alipay);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return new Alipay();
    }
}
