package cn.authing.guard.social;

import android.content.Context;

import cn.authing.guard.Callback;
import cn.authing.guard.data.UserInfo;

public abstract class SocialAuthenticator {
    public abstract void login(Context context, Callback<UserInfo> callback);
}
