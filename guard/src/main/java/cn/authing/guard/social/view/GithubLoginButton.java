package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.Github;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class GithubLoginButton extends SocialLoginButton {
    public GithubLoginButton(Context context) {
        this(context, null);
    }

    public GithubLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GithubLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Github.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_github;
    }
}
