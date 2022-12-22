package cn.authing.guard.social;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.FingerManager;

public class Finger extends SocialAuthenticator {

    private static final String TAG = "Finger";
    public static String appId;

    /**
     * 通用跳转授权业务 Demo
     */
    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        FingerManager fingerManager = new FingerManager();
        fingerManager.startBiometric((AppCompatActivity) context, new FingerManager.FingerCallback() {
            @Override
            public void onError() {

            }

            @Override
            public void onSucceeded() {
                login(context, "", callback);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }
}
