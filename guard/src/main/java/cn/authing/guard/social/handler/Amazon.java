package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.workflow.RequestContext;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Amazon extends SocialAuthenticator {

    private static final String TAG = "Amazon";
    private RequestContext requestContext;

    private Amazon() {
    }

    public static Amazon getInstance() {
        return AmazonInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        if (requestContext == null) {
            requestContext = RequestContext.create(context);
        }
        requestContext.registerListener(new AuthorizeListener() {

            /*授权已成功完成。*/
            @Override
            public void onSuccess(AuthorizeResult result) {
                /*您的应用现已获得请求范围授权*/
                ALog.i(TAG, "Auth success");
                login(result.getAccessToken(), callback);
            }

            /*尝试授权
            应用时出错。*/
            @Override
            public void onError(AuthError ae) {
                /*提示用户发生错误*/
                ALog.e(TAG, "Auth Failed, errCode = " + ae.getType() + " errMessage" + ae.getMessage());
                callback.call(Const.ERROR_CODE_10028, "Login by Amazon failed", null);
            }

            /*授权未完成便已取消。*/
            @Override
            public void onCancel(AuthCancellation cancellation) {
                /*将UI重新设置为随时登录状态*/
                ALog.i(TAG, "Auth canceled");
                callback.call(Const.ERROR_CODE_10028, "Login by Amazon canceled", null);
            }
        });
        AuthorizationManager.authorize(new AuthorizeRequest
                .Builder(requestContext)
                .addScopes(ProfileScope.profile(), ProfileScope.userId(), ProfileScope.postalCode())
                .build());
    }

    public void onCreate(Context context) {
        try {
            Class.forName("com.amazon.identity.auth.device.api.workflow.RequestContext");
            requestContext = RequestContext.create(context);
        } catch (ClassNotFoundException e) {
            //ALog.e(TAG, e.toString());
        }
    }

    public void onResume() {
        if (requestContext != null) {
            requestContext.onResume();
        }
    }

    public void signOut(Context context) {
        AuthorizationManager.signOut(context.getApplicationContext(), new Listener<Void, AuthError>() {
            @Override
            public void onSuccess(Void response) {
                // 设置退出状态UI
            }

            @Override
            public void onError(AuthError authError) {
                // 记录错误
            }
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByAmazon(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByAmazon(authCode, callback);
    }

    @SuppressLint("StaticFieldLeak")
    private static final class AmazonInstanceHolder {
        static final Amazon mInstance = new Amazon();
    }

}
