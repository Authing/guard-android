package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class FaceBook extends SocialAuthenticator {

    public static final int RC_SIGN_IN = 64206;
    private static final String TAG = "FaceBook";
    private CallbackManager callbackManager;

    private FaceBook() {
    }

    public static FaceBook getInstance() {
        return MInstanceHolder.mInstance;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        ALog.i(TAG, "Auth onSuccess");
                        login(loginResult.getAccessToken().getToken(), callback);
                    }

                    @Override
                    public void onCancel() {
                        ALog.e(TAG, "Auth Failed, onCancel");
                        callback.call(Const.ERROR_CODE_10010, "Login by FaceBook canceled", null);
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        ALog.e(TAG, "Auth Failed, errorMessage is" + exception.getMessage());
                        callback.call(Const.ERROR_CODE_10010, "Login by FaceBook failed", null);
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "user_friends", "email"));
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByFaceBook(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByFaceBook(authCode, callback);
    }

    private static final class MInstanceHolder {
        static final FaceBook mInstance = new FaceBook();
    }

}
