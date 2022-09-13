package cn.authing.guard.oneclick;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.social.SocialLoginButton;

public class OneClickLoginButton extends SocialLoginButton {

    public OneClickLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public OneClickLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OneClickLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("OneClickLoginButton");

    }

    @Override
    protected SocialAuthenticator createAuthenticator() {
        return new OneClick(getContext());
    }

    @Override
    protected int getImageRes() {
        return 0;
    }
}
