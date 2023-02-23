package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;

public class WechatMiniProgramLoginButton extends SocialLoginButton {
    public WechatMiniProgramLoginButton(Context context) {
        this(context, null);
    }

    public WechatMiniProgramLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WechatMiniProgramLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return new WechatMiniProgram();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_wechat_miniprogram;
    }
}
