package cn.authing.guard.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class AuthClient {
    public static void sendSms(String phone, @NotNull AuthCallback<?> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/sms/send";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), null));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void loginByPhoneCode(String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                body.put("code", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/login/phone-code";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void loginByAccount(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String encryptPassword = Util.encryptPassword(password);
                JSONObject body = new JSONObject();
                body.put("account", account);
                body.put("password", encryptPassword);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/login/account";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void registerByEmail(String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String encryptPassword = Util.encryptPassword(password);
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", encryptPassword);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/register/email";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void registerByPhoneCode(String phone, String password, String code, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String encryptPassword = Util.encryptPassword(password);
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                body.put("password", encryptPassword);
                body.put("code", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/register/phone-code";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void resetPasswordByEmail(String emailAddress, @NotNull AuthCallback<JSONObject> callback) {
        sendEmail(emailAddress, "RESET_PASSWORD", callback);
    }

    public static void sendEmail(String emailAddress, String scene, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", emailAddress);
                body.put("scene", scene);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/email/send";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void resetPasswordByEmailCode(String emailAddress, String code, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", emailAddress);
                body.put("code", code);
                body.put("newPassword", Util.encryptPassword(newPassword));
                String url = "https://" + config.getIdentifier()  + "." + Authing.getsHost() + "/api/v2/password/reset/email";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void loginByWechat(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String poolId = config.getUserPoolId();
                String url = "https://core." + Authing.getsHost() + "/connection/social/wechat:mobile/" + poolId + "/callback?code=" + authCode;
                Guardian.get(url, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void loginByAlipay(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String connId = "";
                List<SocialConfig> configs = config.getSocialConfigs();
                for (SocialConfig c : configs) {
                    String provider = c.getProvider();
                    if ("alipay".equals(provider)) {
                        connId = c.getId();
                        break;
                    }
                }
                JSONObject body = new JSONObject();
                body.put("connId", connId);
                body.put("code", authCode);
                String url = "https://" + config.getIdentifier() + "." + Authing.getsHost() + "/api/v2/ecConn/alipay/authByCode";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    private static void createUserInfoFromResponse(Response data, @NotNull AuthCallback<UserInfo> callback) {
        if (data.getCode() != 200) {
            callback.call(data.getCode(), data.getMessage(), null);
            return;
        }

        UserInfo userInfo;
        try {
            userInfo = UserInfo.createUserInfo(data.getData());
            callback.call(data.getCode(), data.getMessage(), userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.call(500, "Cannot parse data into UserInfo", null);
        }
    }
}
