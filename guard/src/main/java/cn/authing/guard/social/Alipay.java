package cn.authing.guard.social;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.OpenAuthTask;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class Alipay extends SocialAuthenticator {

    private static final String TAG = "Alipay";
    public static String appId;

    /**
     * 通用跳转授权业务 Demo
     */
    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> _login(config, context, callback));
    }

    public void _login(Config config, Context context, @NotNull AuthCallback<UserInfo> callback) {

        Activity activity = (Activity) context;

        // 传递给支付宝应用的业务参数
        final Map<String, String> bizParams = new HashMap<>();
        String aid = appId != null? appId : config.getSocialAppId(Const.EC_TYPE_ALIPAY);
        bizParams.put("url", "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=" + aid + "&scope=auth_user&state=init");

        // 支付宝回跳到您的应用时使用的 Intent Scheme。
        // 请设置为一个不和其它应用冲突的值，并在 AndroidManifest.xml 中为 AlipayResultActivity 的 android:scheme 属性
        // 指定相同的值。实际使用时请勿设置为 __alipaysdkdemo__ 。
        // 如果不设置，OpenAuthTask.execute() 在用户未安装支付宝，使用网页完成业务流程后，将无法回跳至您的应用。
        final String scheme = "__authing_guard__";

        // 防止在支付宝 App 被强行退出等意外情况下，OpenAuthTask.Callback 一定时间内无法释放，导致
        // Activity 泄漏
        final WeakReference<Activity> ctxRef = new WeakReference<>(activity);

        // 唤起授权业务
        final OpenAuthTask task = new OpenAuthTask(activity);
        task.execute(
                scheme,    // Intent Scheme
                OpenAuthTask.BizType.AccountAuth, // 业务类型
                bizParams, // 业务参数
                (i, s, bundle) -> {
                    final Context ref = ctxRef.get();
                    if (ref != null) {
                        handleResult(context, bundle, callback);
                    }
                },true); // 是否需要在用户未安装支付宝 App 时，使用 H5 中间页中转。建议设置为 true。
    }

    private void handleResult(Context context, Bundle bundle, @NotNull AuthCallback<UserInfo> callback) {
        if (bundle == null) {
            callback.call(Const.ERROR_CODE_10007, "Alipay auth failed", null);
            return;
        }

        if (!"SUCCESS".equals(bundle.get("result_code"))) {
            callback.call(Const.ERROR_CODE_10007, "Alipay auth failed", null);
            return;
        }
        ALog.i(TAG, "Auth onSuccess");
        String code = bundle.get("auth_code").toString();
        login(context, code, callback);
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByAlipay(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByAlipay(authCode, callback);
    }
}
