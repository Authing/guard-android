package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.Alipay;

public class AlipayLoginButton extends SocialLoginButton {
    public AlipayLoginButton(Context context) {
        this(context, null);
    }

    public AlipayLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlipayLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AlipayLoginButton");

    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return new Alipay();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_alipay;
    }
}
