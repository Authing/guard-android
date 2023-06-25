package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Twitter extends SocialAuthenticator {

    private static final String TAG = "Twitter";
    private TwitterAuthClient mTwitterAuthClient;
    private String consumerKey;
    private String consumerSecret;
    private String tokenSecret;

    private Twitter() {
    }

    public static Twitter getInstance() {
        return TwitterInstanceHolder.mInstance;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mTwitterAuthClient != null) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (consumerKey == null && config != null) {
                consumerKey = config.getSocialConsumerKey(Const.EC_TYPE_TWITTER);
            }

            if (consumerSecret == null && config != null) {
                consumerSecret = config.getSocialConsumerSecret(Const.EC_TYPE_TWITTER);
            }

            TwitterConfig twitterConfig = new TwitterConfig.Builder(context)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .twitterAuthConfig(new TwitterAuthConfig(consumerKey, consumerSecret))
                    .debug(true)
                    .build();
            com.twitter.sdk.android.core.Twitter.initialize(twitterConfig);
            if (mTwitterAuthClient != null) {
                mTwitterAuthClient.cancelAuthorize();
            }
            mTwitterAuthClient = new TwitterAuthClient();
            mTwitterAuthClient.authorize((Activity) context, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    ALog.i(TAG, "Auth onSuccess");
                    TwitterAuthToken authToken = result.data.getAuthToken();
                    String token = authToken.token;
                    tokenSecret = authToken.secret;
                    login(token, callback);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i(TAG, "Auth Failed, errorMessage is ：" + exception.toString());
                    callback.call(Const.ERROR_CODE_10030, "Login by Twitter failed", null);
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByTwitter(authCode, tokenSecret, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByTwitter(authCode, tokenSecret, callback);
    }

    private static final class TwitterInstanceHolder {
        static final Twitter mInstance = new Twitter();
    }

}
