package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Line extends SocialAuthenticator {

    private static final String TAG = "Line";
    private String channelID;
    private ArrayList<Scope> scopes;
    private AuthCallback<UserInfo> callback;
    private String idToken;

    private Line() {
    }

    public static Line getInstance() {
        return LineInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        Authing.getPublicConfig(config -> {
            if (channelID == null && config != null) {
                channelID = config.getSocialChannelID(Const.EC_TYPE_LINE);
            }

            if (channelID == null) {
                callback.call(Const.ERROR_CODE_10023, "channelID is null", null);
                return;
            }

            try {
                // App-to-app login
                if (scopes == null || scopes.isEmpty()) {
                    scopes = new ArrayList<>();
                    scopes.add(Scope.PROFILE);
                    scopes.add(Scope.OPENID_CONNECT);
                    scopes.add(Scope.OC_EMAIL);
                    //scopes.add(Scope.OC_PHONE_NUMBER);
                }

                Intent loginIntent = LineLoginApi.getLoginIntent(
                        context,
                        channelID,
                        new LineAuthenticationParams.Builder()
                                .scopes(scopes)
                                .nonce("1234") // nonce can be used to improve security
                                .build());
                ((Activity) context).startActivityForResult(loginIntent, Const.LINE_REQUEST);

            } catch (Exception e) {
                ALog.e("ERROR", e.toString());
                callback.call(Const.ERROR_CODE_10023, e.toString(), null);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Const.LINE_REQUEST && data != null) {
            LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
            switch (result.getResponseCode()) {
                case SUCCESS:
                    // Login successful
                    if (result.getLineCredential() == null) {
                        if (callback != null) {
                            callback.call(Const.ERROR_CODE_10023, "Login by Line failed", null);
                        }
                        return;
                    }
                    String accessToken = result.getLineCredential().getAccessToken().getTokenString();
                    if (TextUtils.isEmpty(accessToken)) {
                        if (callback != null) {
                            callback.call(Const.ERROR_CODE_10023, "Login by Line failed", null);
                        }
                        return;
                    }
                    ALog.i(TAG, "Auth onSuccess");
                    ALog.e("zjh", "accessToken = " + accessToken);
                    ALog.e("zjh", "idToken = " + (result.getLineIdToken() == null ? null : result.getLineIdToken().getRawString()));
                    idToken = (result.getLineIdToken() == null ? null : result.getLineIdToken().getRawString());
                    login(accessToken, callback);
                    break;
                case CANCEL:
                    // Login canceled by user
                    ALog.e(TAG, "Auth Canceled");
                    if (callback != null) {
                        callback.call(Const.ERROR_CODE_10023, "Login by Line canceled", null);
                    }
                    break;
                default:
                    // Login canceled due to other error
                    ALog.e(TAG, "Auth Failed, onError errorCode = " + result.getResponseCode()
                            + " errorMsg = " + result.getErrorData());
                    if (callback != null) {
                        callback.call(Const.ERROR_CODE_10023, "Login by Line failed", null);
                    }
            }
        }
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByLine(authCode, idToken, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByLine(authCode, idToken, callback);
    }

    @SuppressLint("StaticFieldLeak")
    private static final class LineInstanceHolder {
        static final Line mInstance = new Line();
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public ArrayList<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(ArrayList<Scope> scopes) {
        this.scopes = scopes;
    }

}
