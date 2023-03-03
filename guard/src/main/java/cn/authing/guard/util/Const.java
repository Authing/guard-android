package cn.authing.guard.util;

import okhttp3.MediaType;

public class Const {
    public final static String NS_ANDROID = "http://schemas.android.com/apk/res/android";

    public static final String SDK_TAG = "Guard-Android@";
    public static final String SDK_VERSION = "1.4.8";

    // error code
    public final static int EC_MFA_REQUIRED = 1636;
    public final static int EC_FIRST_TIME_LOGIN = 1639;
    public final static int EC_CAPTCHA = 2000;
    public final static int EC_SOCIAL_BIND_LOGIN = 1640;
    public final static int EC_SOCIAL_BIND_REGISTER = 1641;
    public final static int EC_SOCIAL_BIND_SELECT = 2921;
    public final static int EC_INCORRECT_VERIFY_CODE = 2001;
    public final static int EC_INCORRECT_CREDENTIAL = 2333;
    public final static int ERROR_CODE_10001 = 10001; // Network error
    public final static int ERROR_CODE_10002 = 10002; // Config not found
    public final static int ERROR_CODE_10003 = 10003; // Login failed
    public final static int ERROR_CODE_10004 = 10004; // JSON parse failed
    public final static int ERROR_CODE_10005 = 10005; // OnClick auth failed
    public final static int ERROR_CODE_10006 = 10006; // OnClick login cancelled
    public final static int ERROR_CODE_10007 = 10007; // Alipay auth failed
    public final static int ERROR_CODE_10008 = 10008; // 在 60 秒内已发送短信验证码
    public final static int ERROR_CODE_10009 = 10009; // Upload avatar failed
    public final static int ERROR_CODE_10010 = 10010; // FaceBook auth failed
    public final static int ERROR_CODE_10012 = 10012; // Weibo auth failed
    public final static int ERROR_CODE_10013 = 10013; // QQ auth failed

    // mfa
    public static final String MFA_POLICY_SMS = "SMS";
    public static final String MFA_POLICY_EMAIL = "EMAIL";
    public static final String MFA_POLICY_OTP = "OTP";
    public static final String MFA_POLICY_FACE = "FACE";

    // social login
    public static final String EC_TYPE_WECHAT = "wechat:mobile";
    public static final String EC_TYPE_WECHAT_MINI_PROGRAM = "wechat:miniprogram:app-launch";
    public static final String EC_TYPE_WECHAT_COM = "wechatwork:mobile";
    public static final String EC_TYPE_WECHAT_COM_AGENCY = "wechatwork:agency:mobile";
    public static final String EC_TYPE_ALIPAY = "alipay";
    public static final String EC_TYPE_LARK_INTERNAL = "lark-internal";
    public static final String EC_TYPE_LARK_PUBLIC = "lark-public";
    public static final String EC_TYPE_YI_DUN = "yidun";
    public static final String EC_TYPE_GOOGLE = "google:mobile";
    public static final String EC_TYPE_FACE_BOOK = "facebook:mobile";
    public static final String EC_TYPE_QQ = "qq:mobile";
    public static final String EC_TYPE_WEIBO = "weibo:mobile";

    // network
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
}
