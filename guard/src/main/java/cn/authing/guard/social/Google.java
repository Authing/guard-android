package cn.authing.guard.social;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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

public class Google extends SocialAuthenticator{

    public static final int RC_SIGN_IN = 1000;
    private Context context;
    private AuthCallback<UserInfo> callback;
    private GoogleLoginReceiver googleLoginReceiver;

    @Override
    public void login(Context context, @NonNull AuthCallback<UserInfo> callback) {
        this.context = context;
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (config == null) {
                return;
            }

            registerReceiver();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestServerAuthCode(config.getSocialClientId(Const.EC_TYPE_GOOGLE))
                    .requestEmail()
                    .requestId()
                    .requestProfile()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);

            Intent intent = googleSignInClient.getSignInIntent();
            ((Activity)context).startActivityForResult(intent, RC_SIGN_IN);
        });

    }

    private void registerReceiver(){
        if (context == null){
            return;
        }
        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction("cn.authing.guard.broadcast.GOOGLE_LOGIN");
        googleLoginReceiver =new GoogleLoginReceiver();
        context.registerReceiver(googleLoginReceiver, intentFilter);
    }

    public void unregisterReceiver(){
        if (context == null || googleLoginReceiver == null){
            return;
        }
        context.unregisterReceiver(googleLoginReceiver);
    }

    private void handleSignInResult(Context context, @Nullable Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            login(context, account.getServerAuthCode(), callback);
        } catch (ApiException e) {
            ALog.e("Google", e.toString());
            callback.call(500, "Login by google failed", null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver();
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByGoogle(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByGoogle(authCode, callback);
    }

    private class GoogleLoginReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            handleSignInResult(context, intent);
        }
    }
}
