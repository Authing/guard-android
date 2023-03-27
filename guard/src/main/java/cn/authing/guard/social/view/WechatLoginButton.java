package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.social.handler.SocialAuthenticator;
import cn.authing.guard.social.handler.Wechat;

public class WechatLoginButton extends SocialLoginButton {

    public WechatLoginButton(@NonNull Context context) {
        this(context, null);
    }

    @Override
    protected SocialAuthenticator createAuthenticator() {
        return new Wechat();
    }

    public WechatLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WechatLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("WechatLoginButton");

    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_wechat;
    }
}