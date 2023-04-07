package cn.authing.guard.social.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.api.entity.common.CommonConstant;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class HuaWei extends SocialAuthenticator {

    private static final String TAG = "HuaWei";
    // 华为帐号登录授权服务，提供静默登录接口silentSignIn，获取前台登录视图getSignInIntent，登出signOut等接口
    private AccountAuthService mAuthService;
    // 华为帐号登录授权参数
    private AccountAuthParams mAuthParam;
    private AuthCallback<UserInfo> callback;
    private List<Scope> scopes;

    private HuaWei() {
    }

    public static HuaWei getInstance() {
        return HuaWeiInstanceHolder.mInstance;
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        this.callback = callback;
        // 1、配置登录请求参数AccountAuthParams，包括请求用户的id(openid、unionid)、email、profile(昵称、头像)等;
        // 2、DEFAULT_AUTH_REQUEST_PARAM默认包含了id和profile（昵称、头像）的请求;
        // 3、如需要再获取用户邮箱，需要setEmail();
        // 4、通过setAuthorizationCode()来选择使用code模式，最终所有请求的用户信息都可以调服务器的接口获取；
        if (scopes == null) {
            mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                    .setEmail()
                    .setProfile()
                    //.setMobileNumber()
                    .setAuthorizationCode()
                    .createParams();
        } else {
            mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                    .setScopeList(scopes)
                    .setAuthorizationCode()
                    .createParams();
        }

        // 使用请求参数构造华为帐号登录授权服务AccountAuthService
        mAuthService = AccountAuthManager.getService(context, mAuthParam);

        // 使用静默登录进行华为帐号登录
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(authAccount -> {
            // 静默登录成功，处理返回的帐号对象AuthAccount，获取帐号信息并处理
            if (authAccount.getAuthorizationCode() == null) {
                ALog.e(TAG, "Auth Failed, code is null");
                callback.call(Const.ERROR_CODE_10025, "Auth Failed, code is null", null);
                return;
            }
            login(authAccount.getAuthorizationCode(), callback);
        });
        task.addOnFailureListener(e -> {
            // 静默登录失败，使用getSignInIntent()方法进行前台显式登录
            if (e instanceof ApiException) {
                ApiException apiException = (ApiException) e;
                Intent signInIntent = mAuthService.getSignInIntent();
                // 如果应用是全屏显示，即顶部无状态栏的应用，需要在Intent中添加如下参数：
                // intent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                // 具体详情可以参见应用调用登录接口的时候是全屏页面，为什么在拉起登录页面的过程中顶部的状态栏会闪一下？应该如何解决？
                signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                ((Activity) context).startActivityForResult(signInIntent, Const.HUAWEI_REQUEST);
            } else {
                ALog.e(TAG, "Auth Failed, onError = " + e.toString());
                callback.call(Const.ERROR_CODE_10025, "Login by Huawei failed", null);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Const.HUAWEI_REQUEST) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // 登录成功，获取到登录帐号信息对象authAccount
                ALog.i(TAG, "Auth onSuccess");
                AuthAccount authAccount = authAccountTask.getResult();
                if (authAccount.getAuthorizationCode() == null) {
                    ALog.e(TAG, "Auth Failed, code is null");
                    callback.call(Const.ERROR_CODE_10025, "Auth Failed, code is null", null);
                    return;
                }
                login(authAccount.getAuthorizationCode(), callback);
            } else {
                // 登录失败，status code标识了失败的原因，请参见API参考中的错误码了解详细错误原因
                ALog.e(TAG, "Auth Failed, onError errorCode = " + ((ApiException) authAccountTask.getException()).getStatusCode()
                        + " errorMsg = " + ((ApiException) authAccountTask.getException()).getStatusMessage());
                if (callback != null) {
                    callback.call(Const.ERROR_CODE_10025, "Login by Huawei failed", null);
                }
            }
        }
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByHuaWei(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByHuaWei(authCode, callback);
    }

    @SuppressLint("StaticFieldLeak")
    private static final class HuaWeiInstanceHolder {
        static final HuaWei mInstance = new HuaWei();
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }
}
