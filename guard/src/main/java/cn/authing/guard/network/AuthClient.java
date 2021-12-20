package cn.authing.guard.network;

import static cn.authing.guard.util.Const.EC_FIRST_TIME_LOGIN;
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
                Guardian.post(url, body, (data)-> {
                    if (data.getCode() == 200) {
                        // after register, login immediately to get access token
                        JSONObject loginBody = new JSONObject();
                        try {
                            loginBody.put("account", email);
                            loginBody.put("password", encryptPassword);
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        String loginUrl = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/login/account";
                        Guardian.post(loginUrl, loginBody, (loginData) -> createUserInfoFromResponse(loginData, callback));
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
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

    public static void resetPasswordByFirstTimeLoginToken(String token, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("token", token);
                body.put("password", Util.encryptPassword(newPassword));
                String url = "https://" + config.getIdentifier()  + "." + Authing.getHost() + "/api/v2/users/password/reset-by-first-login-token";
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
                String url = "https://core." + Authing.getHost() + "/connection/social/wechat:mobile/" + poolId + "/callback?code=" + authCode + "&app_id=" + Authing.getAppId();
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

    public static void loginByOneClick(String token, String accessToken, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("token", token);
                body.put("accessToken", accessToken);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/ecConn/oneAuth/login";
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

    public static void bindPhone(String phone, String code, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("phone", phone);
                body.put("phoneCode", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/users/phone/bind";
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

    public static void mfaVerifyByOTP(String code, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("authenticatorType", "totp");
                body.put("totp", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/mfa/totp/verify";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void mfaVerifyByRecoveryCode(String code, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("authenticatorType", "totp");
                body.put("recoveryCode", code);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/mfa/totp/recovery";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void changePassword(String oldPassword, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("oldPassword", Util.encryptPassword(oldPassword));
                body.put("newPassword", Util.encryptPassword(newPassword));
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/password/update";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void updateUser(JSONObject body, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/users/profile/update";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void updateCustomUserInfo(UserInfo userInfo, JSONObject customData, @NotNull AuthCallback<JSONObject> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("targetType", "USER");
                body.put("targetId", userInfo.getId());
                body.put("data", customData);
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/udvs/set";
                Guardian.post(url, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    public static void getUserDefinedData(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig((config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("targetType", "USER");
                body.put("targetId", userInfo.getId());
                String url = "https://" + config.getIdentifier() + "." + Authing.getHost() + "/api/v2/udvs/get";
                Guardian.post(url, body, (data)-> {
                    if (data.getCode() == 200) {
                        JSONObject obj = data.getData();
                        try {
                            userInfo.parseCustomData(obj.getJSONArray("result"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.call(data.getCode(), data.getMessage(), userInfo);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        }));
    }

    private static void createUserInfoFromResponse(Response data, @NotNull AuthCallback<UserInfo> callback) {
        int code = data.getCode();
        try {
            if (code == 200) {
                UserInfo userInfo;
                userInfo = UserInfo.createUserInfo(data.getData());
                Guardian.ACCESS_TOKEN = userInfo.getAccessToken();
                if (Util.isNull(Guardian.ACCESS_TOKEN)) {
                    callback.call(code, data.getMessage(), userInfo);
                } else {
                    getUserDefinedData(userInfo, callback);
                }
            } else if (code == EC_MFA_REQUIRED) {
                MFAData mfaData = MFAData.create(data.getData());
                UserInfo userInfo = new UserInfo();
                userInfo.setMfaData(mfaData);
                callback.call(code, data.getMessage(), userInfo);
            } else if (code == EC_FIRST_TIME_LOGIN) {
                JSONObject o = data.getData();
                if (o.has("token")) {
                    String token = o.getString("token");
                    UserInfo userInfo = new UserInfo();
                    userInfo.setFirstTimeLoginToken(token);
                    callback.call(code, data.getMessage(), userInfo);
                } else {
                    callback.call(code, data.getMessage(), null);
                }
            } else {
                callback.call(code, data.getMessage(), null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callback.call(500, "Cannot parse data into UserInfo", null);
        }
    }
}
