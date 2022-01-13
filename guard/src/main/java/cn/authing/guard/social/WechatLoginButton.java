package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;

public class WechatLoginButton extends SocialLoginButton {

    public WechatLoginButton(@NonNull Context context) {
        this(context, null);
    }

    @Override
    protected SocialAuthenticator createAuthenticator() {
        return new Wechat();
    }

    public WechatLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public WechatLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("WechatLoginButton");

        setImageResource(R.drawable.ic_authing_wechat);
    }
}
