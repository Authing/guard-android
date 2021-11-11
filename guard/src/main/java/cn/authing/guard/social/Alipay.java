package cn.authing.guard.social;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.alipay.sdk.app.OpenAuthTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.Guardian;

public class Alipay extends SocialAuthenticator{

    private static final String TAG = "Alipay";

    public static String appId;

    /**
     * 通用跳转授权业务 Demo
     */
    @Override
    public void login(Context context, Callback<UserInfo> callback) {

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

    private static void handleResult(Bundle bundle, Callback<UserInfo> callback) {
        if (bundle == null) {
            fireCallback(callback, null);
            return;
        }

        if (!"SUCCESS".equals(bundle.get("result_code"))) {
            fireCallback(callback, null);
            return;
        }

        Authing.getPublicConfig(config -> _handleResult(config, bundle, callback));
    }

    private static void _handleResult(Config config, Bundle bundle, Callback<UserInfo> callback) {
        if (config == null) {
            fireCallback(callback, null);
            return;
        }

        String connId = "";
        List<SocialConfig> configs = config.getSocialConfigs();
        for (SocialConfig c : configs) {
            String provider = c.getProvider();
            if ("alipay".equals(provider)) {
                connId = c.getId();
                break;
            }
        }

        try {
            String code = bundle.get("auth_code").toString();
            JSONObject body = new JSONObject();
            body.put("connId", connId);
            body.put(" s", code);
            String url = "https://" + config.getIdentifier() + ".authing.cn/api/v2/ecConn/alipay/authByCode";
            Guardian.post(url, body, (response) -> {
                if (response != null && response.getCode() == 200) {
                    try {
                        UserInfo userInfo = UserInfo.createUserInfo(response.getData());
                        Log.d(TAG, "Got user info by alipay:" + userInfo.toString());
                        fireCallback(callback, userInfo);
                    } catch (JSONException e) {
                        Log.d(TAG, "Cannot get user info by alipay with exception:" + e);
                        e.printStackTrace();
                        fireCallback(callback, null);
                    }
                } else {
                    Log.d(TAG, "Cannot get user info by alipay");
                    fireCallback(callback, null);
                }
            });
        } catch (Exception e) {
            fireCallback(callback, null);
        }
    }

    private static void fireCallback(Callback<UserInfo> callback, UserInfo info) {
        if (callback != null) {
            callback.call(true, info);
        }
    }
}
