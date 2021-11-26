package cn.authing.guard.social;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import org.json.JSONException;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.UserInfo;

public abstract class SocialLoginButton extends ImageButton {

    protected SocialAuthenticator authenticator;
    protected AuthCallback<UserInfo> callback;
    protected AnimatedVectorDrawable backgroundDrawable;

    public SocialLoginButton(Context context) {
        this(context, null);
    }

    protected abstract SocialAuthenticator createAuthenticator();

    private void loginDone(int code, String message, UserInfo userInfo) {
        post(()->{
            backgroundDrawable.stop();
            setBackgroundResource(R.drawable.ic_authing_circle);
        });

        if (callback != null) {
            callback.call(code, message, userInfo);
        } else if (getContext() instanceof AuthActivity) {
            if (userInfo != null) {
                AuthActivity activity = (AuthActivity) getContext();
                Intent intent = new Intent();
                intent.putExtra("user", userInfo);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
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
            setBackgroundResource(R.drawable.ic_authing_circle);
        }
        backgroundDrawable = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        setOnClickListener((v -> {
            setBackground(backgroundDrawable);
            backgroundDrawable.start();
            if (authenticator != null) {
                authenticator.login(context, this::loginDone);
            }
        }));
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }
}
