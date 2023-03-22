package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.KuaiShou;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class KuaiShouLoginButton extends SocialLoginButton {
    public KuaiShouLoginButton(Context context) {
        this(context, null);
    }

    public KuaiShouLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KuaiShouLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return KuaiShou.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_kuaishou;
    }
}
