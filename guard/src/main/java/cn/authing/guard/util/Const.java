package cn.authing.guard.util;

import okhttp3.MediaType;

public class Const {
    public final static String NS_ANDROID = "http://schemas.android.com/apk/res/android";

    public static final String SDK_TAG = "Guard-Android@";
    public static final String SDK_VERSION = "1.4.2";

    public final static int EC_MFA_REQUIRED = 1636;
    public final static int EC_FIRST_TIME_LOGIN = 1639;
    public final static int EC_CAPTCHA = 2000;
    public final static int EC_VERIFY_EMAIL = 2042;
    public final static int EC_ACCOUNT_LOCKED = 2005;
    public final static int EC_ACCOUNT_EXIST = 2004;
    public final static int EC_ACCOUNT_NOT_LOGIN = 2020;
    public final static int EC_400 = 400;

    public final static int EC_INCORRECT_VERIFY_CODE = 2001;
    public final static int EC_INCORRECT_CREDENTIAL = 2333;

    public static final String MFA_POLICY_SMS = "SMS";
    public static final String MFA_POLICY_EMAIL = "EMAIL";
    public static final String MFA_POLICY_OTP = "OTP";
    public static final String MFA_POLICY_FACE = "FACE";

    public static final String EC_TYPE_WECHAT = "wechat:mobile";
    public static final String EC_TYPE_WECHAT_COM = "wechatwork:mobile";
    public static final String EC_TYPE_WECHAT_COM_AGENCY = "wechatwork:agency:mobile";
    public static final String EC_TYPE_ALIPAY = "alipay";
    public static final String EC_TYPE_LARK_INTERNAL = "lark-internal";
    public static final String EC_TYPE_LARK_PUBLIC = "lark-public";
    public static final String EC_TYPE_YI_DUN = "yidun";
    public static final String EC_TYPE_GOOGLE = "google:mobile";

    public static final String TYPE_WECHAT = "wechat";
    public static final String TYPE_WECHAT_COM = "wecom";
    public static final String TYPE_WECHAT_COM_AGENCY = "wecom-agency";
    public static final String TYPE_ALIPAY = "alipay";
    public static final String TYPE_LARK = "lark";
    public static final String TYPE_GOOGLE = "google";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
}
