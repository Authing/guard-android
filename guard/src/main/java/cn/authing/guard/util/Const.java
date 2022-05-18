package cn.authing.guard.util;

import okhttp3.MediaType;

public class Const {
    public final static String NS_ANDROID = "http://schemas.android.com/apk/res/android";

    public static final String SDK_VERSION = "1.3.2";

    public final static int EC_MFA_REQUIRED = 1636;
    public final static int EC_FIRST_TIME_LOGIN = 1639;
    public final static int EC_CAPTCHA = 2000;

    public final static int EC_INCORRECT_VERIFY_CODE = 2001;
    public final static int EC_INCORRECT_CREDENTIAL = 2333;

    public static final String MFA_POLICY_SMS = "SMS";
    public static final String MFA_POLICY_EMAIL = "EMAIL";
    public static final String MFA_POLICY_OTP = "OTP";
    public static final String MFA_POLICY_FACE = "FACE";

    public static final String EC_TYPE_WECHAT = "wechat:mobile";
    public static final String EC_TYPE_WECHAT_COM = "wechatwork:mobile";
    public static final String EC_TYPE_ALIPAY = "alipay";
    public static final String EC_TYPE_LARK_INTERNAL = "lark-internal";
    public static final String EC_TYPE_LARK_PUBLIC = "lark-public";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
}
