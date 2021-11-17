package cn.authing.guard.social;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.alipay.sdk.app.OpenAuthTask;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;

public class Alipay extends SocialAuthenticator {

    public static String appId;

    /**
     * 通用跳转授权业务 Demo
     */
    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {

        Activity activity = (Activity) context;

        // 传递给支付宝应用的业务参数
        final Map<String, String> bizParams = new HashMap<>();
        bizParams.put("url", "https://authweb.alipay.com/auth?auth_type=PURE_OAUTH_SDK&app_id=" + appId + "&scope=auth_user&state=init");

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
                        handleResult(bundle, callback);
                    }
                },true); // 是否需要在用户未安装支付宝 App 时，使用 H5 中间页中转。建议设置为 true。
    }

    private static void handleResult(Bundle bundle, @NotNull AuthCallback<UserInfo> callback) {
        if (bundle == null) {
            callback.call(500, "alipay error", null);
            return;
        }

        if (!"SUCCESS".equals(bundle.get("result_code"))) {
            callback.call(500, "alipay auth error", null);
            return;
        }

        String code = bundle.get("auth_code").toString();
        AuthClient.loginByAlipay(code, callback);
    }
}
