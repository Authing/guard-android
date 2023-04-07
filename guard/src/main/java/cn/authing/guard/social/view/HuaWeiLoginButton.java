package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.HuaWei;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class HuaWeiLoginButton extends SocialLoginButton {
    public HuaWeiLoginButton(Context context) {
        this(context, null);
    }

    public HuaWeiLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HuaWeiLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return HuaWei.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_huawei;
    }
}
