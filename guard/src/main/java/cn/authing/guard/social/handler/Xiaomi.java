package cn.authing.guard.social.handler;

import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Xiaomi extends SocialAuthenticator {

    private static final String TAG = "Xiaomi";
    private long appId;
    private String redirectUri;
    private int[] scope;
    private String state = "1234";
    private XiaomiOAuthResults results;
    private AsyncTask waitResultTask;
    private final Executor mExecutor = Executors.newCachedThreadPool();
    private AuthCallback<UserInfo> callback;

    private Xiaomi() {
    }

    public static Xiaomi getInstance() {
        return XiaomiInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (appId == 0 && config != null) {
                appId = Long.parseLong(config.getSocialAppId(Const.EC_TYPE_XIAOMI));
            }
            if (redirectUri == null && config != null) {
                redirectUri = config.getSocialRedirectUrl(Const.EC_TYPE_XIAOMI);
            }

            if (scope == null) {
                scope = new int[]{XiaomiOAuthConstants.SCOPE_OPEN_ID,
                        XiaomiOAuthConstants.SCOPE_PROFILE};
            }

            XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                    .setAppId(appId)
                    .setRedirectUrl(redirectUri)
                    .setScope(scope)
                    .setState(state)
                    //.setKeepCookies(false) // 不调的话默认是false
                    //.setNoMiui(false) // 不调的话默认是false
                    //.setSkipConfirm(false) // 不调的话默认是false
                    //.setPhoneNumAutoFill(context.getApplicationContext(), true)
                    .setSkipConfirm(true)
                    .startGetOAuthCode((Activity) context);
            waitAndShowFutureResult(future);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private <V> void waitAndShowFutureResult(final XiaomiOAuthFuture<V> future) {
        waitResultTask = new AsyncTask<Void, Void, V>() {
            Exception e;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected V doInBackground(Void... params) {
                V v = null;
                try {
                    v = future.getResult();
                } catch (IOException | OperationCanceledException | XMAuthericationException e1) {
                    this.e = e1;
                }
                return v;
            }

            @Override
            protected void onPostExecute(V v) {
                if (v != null) {
                    if (v instanceof XiaomiOAuthResults) {
                        results = (XiaomiOAuthResults) v;
                    }
                    ALog.i(TAG, "Auth success");
                    login(results.getCode(), callback);
                } else if (e != null) {
                    ALog.e(TAG, "Auth Failed, errorMsg  = " + e);
                    if (callback != null) {
                        callback.call(Const.ERROR_CODE_10021, "Login by Xiaomi failed", null);
                    }
                } else {
                    ALog.e(TAG, "Auth Canceled");
                    if (callback != null) {
                        callback.call(Const.ERROR_CODE_10021, "Login by Xiaomi canceled", null);
                    }
                }
            }
        }.executeOnExecutor(mExecutor);
    }


    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByXiaomi(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByXiaomi(authCode, callback);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (waitResultTask != null && !waitResultTask.isCancelled()) {
            waitResultTask.cancel(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static final class XiaomiInstanceHolder {
        static final Xiaomi mInstance = new Xiaomi();
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public int[] getScope() {
        return scope;
    }

    public void setScope(int[] scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
