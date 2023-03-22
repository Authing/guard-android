package cn.authing.guard.util;

import okhttp3.MediaType;

public class Const {
    public final static String NS_ANDROID = "http://schemas.android.com/apk/res/android";

    public static final String SDK_TAG = "Guard-Android@";
    public static final String SDK_VERSION = "1.5.3";

    // error code
    public final static int EC_MFA_REQUIRED = 1636;
    public final static int EC_FIRST_TIME_LOGIN = 1639;
    public final static int EC_CAPTCHA = 2000;
    public final static int EC_SOCIAL_BIND_LOGIN = 1640;
    public final static int EC_SOCIAL_BIND_REGISTER = 1641;
    public final static int EC_SOCIAL_BIND_SELECT = 2921;
    public final static int EC_VERIFY_EMAIL = 2042;
    public final static int EC_ACCOUNT_LOCKED = 2005;
    public final static int EC_ACCOUNT_EXIST = 2004;
    public final static int EC_ACCOUNT_NOT_LOGIN = 2020;
    public final static int EC_400 = 400;
    public final static int EC_422 = 422;
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
    public final static int ERROR_CODE_10011 = 10011; // webauthn error
    public final static int ERROR_CODE_10012 = 10012; // Weibo auth failed
    public final static int ERROR_CODE_10013 = 10013; // QQ auth failed
    public final static int ERROR_CODE_10014 = 10014; // 百度 auth failed
    public final static int ERROR_CODE_10015 = 10015; // Linkedin auth failed
    public final static int ERROR_CODE_10016 = 10016; // 钉钉 auth failed
    public final static int ERROR_CODE_10017 = 10017; // 抖音 auth failed
    public final static int ERROR_CODE_10018 = 10018; // Github auth failed
    public final static int ERROR_CODE_10019 = 10019; // Gitee auth failed
    public final static int ERROR_CODE_10020 = 10020; // GitLab auth failed
    public final static int ERROR_CODE_10021 = 10020; // 小米 auth failed
    public final static int ERROR_CODE_10022 = 10021; // 快手 auth failed

    // mfa
    public static final String MFA_POLICY_SMS = "SMS";
    public static final String MFA_POLICY_EMAIL = "EMAIL";
    public static final String MFA_POLICY_OTP = "OTP";
    public static final String MFA_POLICY_FACE = "FACE";

    // social login
    public static final String EC_TYPE_WECHAT = "wechat:mobile";
    public static final String EC_TYPE_WECHAT_COM = "wechatwork:mobile";
    public static final String EC_TYPE_WECHAT_MINI_PROGRAM = "wechat:miniprogram:app-launch";
    public static final String EC_TYPE_WECHAT_COM_AGENCY = "wechatwork:agency:mobile";
    public static final String EC_TYPE_ALIPAY = "alipay";
    public static final String EC_TYPE_LARK_INTERNAL = "lark-internal";
    public static final String EC_TYPE_LARK_PUBLIC = "lark-public";
    public static final String EC_TYPE_YI_DUN = "yidun";
    public static final String EC_TYPE_GOOGLE = "google:mobile";
    public static final String EC_TYPE_FACEBOOK = "facebook:mobile";
    public static final String EC_TYPE_QQ = "qq:mobile";
    public static final String EC_TYPE_WEIBO = "weibo:mobile";
    public static final String EC_TYPE_BAIDU = "baidu:mobile";
    public static final String EC_TYPE_LINKEDIN = "linkedin:mobile";
    public static final String EC_TYPE_DING_TALK = "dingtalk:mobile";
    public static final String EC_TYPE_DOU_YIN = "douyin:mobile";
    public static final String EC_TYPE_GITHUB= "github:mobile";
    public static final String EC_TYPE_GITEE= "gitee:mobile";
    public static final String EC_TYPE_GITLAB= "gitlab:mobile";
    public static final String EC_TYPE_XIAOMI= "xiaomi:mobile";
    public static final String EC_TYPE_KUAI_SHOU= "kuaishou:mobile";

    public static final String TYPE_WECHAT = "wechat";
    public static final String TYPE_WECHAT_MINI_PROGRAM = "wechat-miniprogram";
    public static final String TYPE_WECHAT_COM = "wecom";
    public static final String TYPE_WECHAT_COM_AGENCY = "wecom-agency";
    public static final String TYPE_ALIPAY = "alipay";
    public static final String TYPE_LARK = "lark";
    public static final String TYPE_GOOGLE = "google";
    public static final String TYPE_FACEBOOK = "facebook";
    public static final String TYPE_QQ = "qq";
    public static final String TYPE_WEIBO = "weibo";
    public static final String TYPE_BAIDU = "baidu";
    public static final String TYPE_FINGER = "fingerprint";
    public static final String TYPE_LINKEDIN = "linkedin";
    public static final String TYPE_DING_TALK = "dingtalk";
    public static final String TYPE_DOU_YIN = "douyin";
    public static final String TYPE_GITHUB = "github";
    public static final String TYPE_GITEE = "gitee";
    public static final String TYPE_GITLAB = "gitlab";
    public static final String TYPE_XIAOMI = "xiaomi";
    public static final String TYPE_KUAI_SHOU = "kuaishou";
    public static final String TYPE_FACE = "face";

    // network
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    // request code
    public static final int REQUEST_CODE_QR = 101;
    public static final int REQUEST_MFA_BINDING = 102;
    public static final int LINKEDIN_REQUEST = 2000;
    public static final int GITHUB_REQUEST = 2001;
    public static final int GITEE_REQUEST = 2002;
    public static final int GITLAB_REQUEST = 2003;

    public static final int SOCIAL_DIALOG_MAX_HEIGHT = 800;
}
