package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.social.SocialLoginButton;
import cn.authing.guard.social.handler.QQ;

public class QQLoginButton extends SocialLoginButton {
    public QQLoginButton(Context context) {
        this(context, null);
    }

    public QQLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public QQLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.ic_authing_qq);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return QQ.getInstance();
    }
}
