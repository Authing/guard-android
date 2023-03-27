package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.social.handler.Line;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class LineLoginButton extends SocialLoginButton {
    public LineLoginButton(Context context) {
        this(context, null);
    }

    public LineLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AlipayLoginButton");

    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Line.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_line;
    }
}
