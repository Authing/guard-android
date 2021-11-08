package cn.authing.guard.social;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import cn.authing.guard.Callback;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;

public abstract class SocialLoginButton extends ImageButton {

    protected SocialAuthenticator authenticator;
    protected Callback<UserInfo> callback;
    protected AnimatedVectorDrawable backgroundDrawable;

    public SocialLoginButton(Context context) {
        this(context, null);
    }

    protected abstract SocialAuthenticator createAuthenticator();

    private void loginDone(UserInfo userInfo) {
        post(()->{
            backgroundDrawable.stop();
            setBackgroundResource(R.drawable.authing_social_button_background);
        });

        if (callback != null) {
            if (userInfo == null) {
                callback.call(false, null);
            } else {
                callback.call(true, userInfo);
            }
        }
    }

    public SocialLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public SocialLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        authenticator = createAuthenticator();
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_social_button_background);
        }
        backgroundDrawable = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        setOnClickListener((v -> {
            setBackground(backgroundDrawable);
            backgroundDrawable.start();
            if (authenticator != null) {
                authenticator.login(context, (ok, data) -> loginDone(data));
            }
        }));
    }

    public void setOnLoginListener(Callback<UserInfo> callback) {
        this.callback = callback;
    }
}
