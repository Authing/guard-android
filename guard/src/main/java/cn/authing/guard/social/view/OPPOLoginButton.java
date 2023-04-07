package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.OPPO;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class OPPOLoginButton extends SocialLoginButton {
    public OPPOLoginButton(Context context) {
        this(context, null);
    }

    public OPPOLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OPPOLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return OPPO.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_oppo;
    }
}
