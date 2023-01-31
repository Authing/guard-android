package cn.authing.guard.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.webkit.WebSettings;

import cn.authing.guard.R;

public class SystemUtil {

    public static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        if (null != userAgent){
            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 检查指纹
     */
    public static boolean checkFingerprintEnable(Activity activity){
        if (!SystemUtil.isHardwareDetected(activity)){
            ToastUtil.showCenter(activity, activity.getString(R.string.authing_enable_fingerprint));
            return false;
        }
        if (!SystemUtil.hasEnrolledFingerprints(activity)){
            ToastUtil.showCenter(activity, activity.getString(R.string.authing_at_least_one_fingerprint));
            Util.openSettingUI(activity);
            return false;
        }
//        if (!SystemUtil.isKeyguardSecure(activity)){
//            ToastUtil.showCenter(activity, activity.getString(R.string.authing_enable_fingerprint_unlock));
//            Util.openSettingUI(activity);
//            return false;
//        }
        return true;
    }


    /**
     * 是否支持指纹
     */
    public boolean supportFingerprint(Context context) {
        KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        if (!fingerprintManager.isHardwareDetected()) {
            //不支持指纹功能
            return false;
        } else if (!keyguardManager.isKeyguardSecure()) {
            //未设置锁屏
            return false;
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            //系统设置中未添加指纹
            return false;
        }
        return true;
    }


    /**
     * 判断指纹硬件是否存在并且功能正常
     */
    public static boolean isHardwareDetected(Context context){
        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        return fingerprintManager.isHardwareDetected();
    }

    /**
     * 判断是否至少有一个指纹登记
     */
    public static boolean hasEnrolledFingerprints(Context context){
        FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
        return fingerprintManager.hasEnrolledFingerprints();
    }

    /**
     * 判断是否设置锁屏，需要先设置锁屏并添加一个指纹
     */
    public static boolean isKeyguardSecure(Context context){
        KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);
        return keyguardManager.isKeyguardSecure();
    }



}
