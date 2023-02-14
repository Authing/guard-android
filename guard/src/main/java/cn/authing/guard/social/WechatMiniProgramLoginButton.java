package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;

public class WechatMiniProgramLoginButton extends SocialLoginButton {
    public WechatMiniProgramLoginButton(Context context) {
        this(context, null);
    }

    public WechatMiniProgramLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public WechatMiniProgramLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.ic_authing_wechat_miniprogram);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return new WechatMiniProgram();
    }
}
