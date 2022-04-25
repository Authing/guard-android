package cn.authing.guard.social;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;

public abstract class SocialAuthenticator {

    private AuthContainer.AuthProtocol authProtocol = AuthContainer.AuthProtocol.EInHouse;

    public abstract void login(Context context, @NotNull AuthCallback<UserInfo> callback);

    public void setAuthProtocol(AuthContainer.AuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }

    protected AuthContainer.AuthProtocol getAuthProtocol(Context context) {
        if (!(context instanceof AuthActivity)) {
            return authProtocol;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        return flow.getAuthProtocol();
    }
}
