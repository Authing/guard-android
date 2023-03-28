package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.Slack;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class SlackLoginButton extends SocialLoginButton {
    public SlackLoginButton(Context context) {
        this(context, null);
    }

    public SlackLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlackLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Slack.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_slack;
    }
}
