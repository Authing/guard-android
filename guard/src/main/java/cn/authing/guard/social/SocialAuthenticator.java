package cn.authing.guard.social;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;

public abstract class SocialAuthenticator {
    public abstract void login(Context context, @NotNull AuthCallback<UserInfo> callback);
}
