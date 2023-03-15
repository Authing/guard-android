package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Google extends SocialAuthenticator {

    public static final int RC_SIGN_IN = 1000;
    private AuthCallback<UserInfo> callback;
    private String serverClientId;

    private Google() {
    }

    public static Google getInstance() {
        return GoogleInstanceHolder.mInstance;
    }

    private static class GoogleInstanceHolder {
        private static final Google mInstance = new Google();
    }

    @Override
    public void login(Context context, @NonNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (config == null) {
                ALog.d("Google", "Config not found");
                return;
            }

            if (serverClientId == null) {
                serverClientId = config.getSocialClientId(Const.EC_TYPE_GOOGLE);
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestServerAuthCode(serverClientId)
                    .requestEmail()
                    .requestId()
                    .requestProfile()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);

            Intent intent = googleSignInClient.getSignInIntent();
            ((Activity) context).startActivityForResult(intent, RC_SIGN_IN);
        });

    }

    public void onActivityResult(Context context, int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Google.RC_SIGN_IN && data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                login(context, account.getServerAuthCode(), callback);
            } catch (ApiException e) {
                ALog.e("Google", e.toString());
                callback.call(e.getStatusCode(), e.getMessage(), null);
            }
        }
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByGoogle(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByGoogle(authCode, callback);
    }

    public String getServerClientId() {
        return serverClientId;
    }

    public void setServerClientId(String serverClientId) {
        this.serverClientId = serverClientId;
    }
}
