package cn.authing.guard.network;

import static cn.authing.guard.util.Const.EC_MFA_REQUIRED;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.MFAData;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.GlobalCountDown;
import cn.authing.guard.util.Util;

public class AuthClient {
    public static void sendSms(String phone, @NotNull AuthCallback<?> callback) {
        if (GlobalCountDown.countDown != 0) {
            callback.call(500, Authing.getAppContext().getString(R.string.authing_sms_already_sent), null);
            return;
        }

        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/sms/send";
                Guardian.post(url, body, (data)-> {
                    if (data.getCode() == 200) {
                        GlobalCountDown.start();
                    }
                    callback.call(data.getCode(), data.getMessage(), null);
                });
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
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/login/phone-code";
                Guardian.post(url, body, (data)-> {
                    if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                        Safe.saveAccount(phone);
                    }
                    createUserInfoFromResponse(data, callback);
                });
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
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/login/account";
                Guardian.post(url, body, (data)-> {
                    if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                        Safe.saveAccount(account);
//                        Safe.savePassword(password);
                    }
                    createUserInfoFromResponse(data, callback);
                });
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
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/register/email";
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
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/register/phone-code";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void sendResetPasswordEmail(String emailAddress, @NotNull AuthCallback<JSONObject> callback) {
        sendEmail(emailAddress, "RESET_PASSWORD", callback);
    }

    public static void sendMFAEmail(String emailAddress, @NotNull AuthCallback<JSONObject> callback) {
        sendEmail(emailAddress, "MFA_VERIFY", callback);
    }

    public static void sendEmail(String emailAddress, String scene, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", emailAddress);
                body.put("scene", scene);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/email/send";
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
                String url = "https://" + config.getIdentifier()  + "." + Authing.getHost() + "/api/v2/password/reset/email";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void resetPasswordByPhoneCode(String phone, String code, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                body.put("code", code);
                body.put("newPassword", Util.encryptPassword(newPassword));
                String url = "https://" + config.getIdentifier()  + "." + Authing.getHost() + "/api/v2/password/reset/sms";
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
                String url = "https://core." + Authing.getHost() + "/connection/social/wechat:mobile/" + poolId + "/callback?code=" + authCode;
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
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/ecConn/alipay/authByCode";
                Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void bindEmail(String email, String code, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("emailCode", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/users/email/bind";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void mfaCheck(String phone, String email, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                if (phone != null)
                    body.put("phone", phone);
                if (email != null)
                    body.put("email", email);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/applications/mfa/check";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void mfaVerifyByPhone(String phone, String code, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                body.put("code", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/applications/mfa/sms/verify";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void mfaVerifyByEmail(String email, String code, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("code", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/applications/mfa/email/verify";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    private static void createUserInfoFromResponse(Response data, @NotNull AuthCallback<UserInfo> callback) {
        int code = data.getCode();
        if (code == 200) {
            UserInfo userInfo;
            try {
                userInfo = UserInfo.createUserInfo(data.getData());
                callback.call(code, data.getMessage(), userInfo);
            } catch (JSONException e) {
                e.printStackTrace();
                callback.call(500, "Cannot parse data into UserInfo", null);
            }
        } else if (code == EC_MFA_REQUIRED) {
            MFAData mfaData = MFAData.create(data.getData());
            UserInfo userInfo = new UserInfo();
            userInfo.setMfaData(mfaData);
            callback.call(code, data.getMessage(), userInfo);
        } else {
            callback.call(code, data.getMessage(), null);
        }
    }
}
