package cn.authing.guard.network;

import static cn.authing.guard.util.Const.EC_FIRST_TIME_LOGIN;
import static cn.authing.guard.util.Const.EC_MFA_REQUIRED;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.Application;
import cn.authing.guard.data.MFAData;
import cn.authing.guard.data.Organization;
import cn.authing.guard.data.Resource;
import cn.authing.guard.data.Role;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.GlobalCountDown;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class AuthClient {

    private static final String TAG = "AuthClient";

    enum PasswordStrength {
        EWeak,
        EMedium,
        EStrong
    }

    public static void registerByEmail(String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        registerByEmail(null, email, password, callback);
    }

    public static void registerByEmail(AuthRequest authData, String email, String password, @NotNull AuthCallback<UserInfo> callback) {
        try {
            String encryptPassword = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", encryptPassword);
            body.put("forceLogin", true);
            Guardian.post("/api/v2/register/email", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(email);
                }
                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void registerByEmailCode(String email, String code, @NotNull AuthCallback<UserInfo> callback) {
        registerByEmailCode(null, email, code, callback);
    }

    public static void registerByEmailCode(AuthRequest authData, String email, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("code", code);
            body.put("forceLogin", true);
            Guardian.post("/api/v2/register/email-code", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(email);
                }
                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void registerByUserName(String username, String password, @NotNull AuthCallback<UserInfo> callback) {
        try {
            String encryptPassword = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", encryptPassword);
            body.put("forceLogin", true);
            Guardian.post("/api/v2/register/username", body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void registerByPhoneCode(String phone, String code, String password, @NotNull AuthCallback<UserInfo> callback) {
        registerByPhoneCode(null, phone, code, password, callback);
    }

    public static void registerByPhoneCode(String phoneCountryCode, String phone, String code, String password, @NotNull AuthCallback<UserInfo> callback) {
        registerByPhoneCode(null, phoneCountryCode, phone, code, password, callback);
    }

    public static void registerByPhoneCode(AuthRequest authData, String phoneCountryCode, String phone, String code, String password, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            if (!Util.isNull(phoneCountryCode)){
                body.put("phoneCountryCode", phoneCountryCode);
            }
            body.put("phone", phone);
            if (!Util.isNull(password)) {
                String encryptPassword = Util.encryptPassword(password);
                body.put("password", encryptPassword);
            }
            body.put("code", code);
            body.put("forceLogin", true);
            Guardian.post("/api/v2/register/phone-code", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(phone);
                    Safe.savePhoneCountryCode(phoneCountryCode);
                }
                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void sendSms(String phone, @NotNull AuthCallback<?> callback) {
        sendSms(null, phone, callback);
    }

    public static void sendSms(String phoneCountryCode, String phone, @NotNull AuthCallback<?> callback) {
        if (GlobalCountDown.isCountingDown(phone+phoneCountryCode)) {
            callback.call(500, Authing.getAppContext().getString(R.string.authing_sms_already_sent), null);
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("phone", phone);
            if (!Util.isNull(phoneCountryCode)) {
                body.put("phoneCountryCode", phoneCountryCode);
            }
            Guardian.post("/api/v2/sms/send", body, (data)-> {
                if (data.getCode() == 200) {
                    GlobalCountDown.start(phone+phoneCountryCode);
                }
                callback.call(data.getCode(), data.getMessage(), null);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByPhoneCode(String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        loginByPhoneCode(null, phone, code, callback);
    }

    public static void loginByPhoneCode(String phoneCountryCode, String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        loginByPhoneCode(null, phoneCountryCode, phone, code, callback);
    }

    public static void loginByPhoneCode(AuthRequest authData, String phoneCountryCode, String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            if (!Util.isNull(phoneCountryCode)){
                body.put("phoneCountryCode", phoneCountryCode);
            }
            body.put("phone", phone);
            body.put("code", code);
            Guardian.post("/api/v2/login/phone-code", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(phone);
                    Safe.savePhoneCountryCode(phoneCountryCode);
                }
                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByEmailCode(String email, String code, @NotNull AuthCallback<UserInfo> callback) {
        loginByEmailCode(null, email, code, callback);
    }

    public static void loginByEmailCode(AuthRequest authData, String email, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("code", code);
            Guardian.post("/api/v2/login/email-code", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(email);
                }
                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByAccount(String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        loginByAccount(null, account, password, callback);
    }

    public static void loginByAccount(AuthRequest authData, String account, String password, @NotNull AuthCallback<UserInfo> callback) {
        try {
            long now = System.currentTimeMillis();
            String encryptPassword = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("account", account);
            body.put("password", encryptPassword);
            Guardian.post("/api/v2/login/account", body, (data)-> {
                ALog.d(TAG, "loginByAccount cost:" + (System.currentTimeMillis() - now) + "ms");
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(account);
//                        Safe.savePassword(password);
                }

                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByLDAP(String username, String password, @NotNull AuthCallback<UserInfo> callback) {
        try {
            String encryptPassword = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", encryptPassword);
            Guardian.post("/api/v2/login/ldap", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(username);
                }
                createUserInfoFromResponse(data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByAD(String username, String password, @NotNull AuthCallback<UserInfo> callback) {
        try {
            String encryptPassword = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", encryptPassword);
            Guardian.post("/api/v2/login/ad", body, (data)-> {
                if (data.getCode() == 200 || data.getCode() == EC_MFA_REQUIRED) {
                    Safe.saveAccount(username);
                }
                createUserInfoFromResponse(data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void sendResetPasswordEmail(String emailAddress, @NotNull AuthCallback<JSONObject> callback) {
        sendEmail(emailAddress, "RESET_PASSWORD", callback);
    }

    public static void sendMFAEmail(String emailAddress, @NotNull AuthCallback<JSONObject> callback) {
        sendEmail(emailAddress, "MFA_VERIFY", callback);
    }

    public static void sendEmail(String emailAddress, String scene, @NotNull AuthCallback<JSONObject> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", emailAddress);
            body.put("scene", scene);
            Guardian.post("/api/v2/email/send", body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void uploadAvatar(InputStream in, @NotNull AuthCallback<UserInfo> callback) {
        Uploader.uploadImage(in, (ok, uploadedUrl)->{
            if (ok && !Util.isNull(uploadedUrl)) {
                try {
                    JSONObject body = new JSONObject();
                    body.put("photo", uploadedUrl);
                    updateProfile(body, callback);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.call(500, "Exception", null);
                }
            } else {
                callback.call(500, "upload avatar failed", null);
            }
        });
    }

    public static void resetPasswordByPhoneCode(String phone, String code, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        resetPasswordByPhoneCode(null, phone, code, newPassword, callback);
    }

    public static void resetPasswordByPhoneCode(String phoneCountryCode, String phone, String code, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        try {
            JSONObject body = new JSONObject();
            if (!Util.isNull(phoneCountryCode)){
                body.put("phoneCountryCode", phoneCountryCode);
            }
            body.put("phone", phone);
            body.put("code", code);
            body.put("newPassword", Util.encryptPassword(newPassword));
            String endpoint = "/api/v2/password/reset/sms";
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void resetPasswordByEmailCode(String emailAddress, String code, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", emailAddress);
            body.put("code", code);
            body.put("newPassword", Util.encryptPassword(newPassword));
            String endpoint = "/api/v2/password/reset/email";
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void resetPasswordByFirstTimeLoginToken(String token, String newPassword, @NotNull AuthCallback<JSONObject> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("token", token);
            body.put("password", Util.encryptPassword(newPassword));
            String endpoint = "/api/v2/users/password/reset-by-first-login-token";
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByWechat(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByWechat(null, authCode, callback);
    }

    public static void loginByWechat(AuthRequest authData, String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("connId", config.getSocialConnectionId("wechat:mobile"));
                body.put("code", authCode);
                Guardian.post("/api/v2/ecConn/wechatMobile/authByCode", body, (data)-> {
                    startOidcInteraction(authData, data, callback);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    public static void loginByWecom(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByWecom(null, authCode, callback);
    }

    public static void loginByWecom(AuthRequest authData, String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("connId", config.getSocialConnectionId(Const.EC_TYPE_WECHAT_COM));
                body.put("code", authCode);
                String endpoint = "/api/v2/ecConn/wechat-work/authByCode";
                Guardian.post(endpoint, body, (data)-> {
                    startOidcInteraction(authData, data, callback);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    public static void loginByAlipay(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByAlipay(null, authCode, callback);
    }

    public static void loginByAlipay(AuthRequest authData, String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                JSONObject body = new JSONObject();
                body.put("connId", config.getSocialConnectionId("alipay"));
                body.put("code", authCode);
                String endpoint = "/api/v2/ecConn/alipay/authByCode";
                Guardian.post(endpoint, body, (data)-> {
                    startOidcInteraction(authData, data, callback);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    public static void loginByLark(String authCode, @NotNull AuthCallback<UserInfo> callback) {
        loginByLark(null, authCode, callback);
    }

    public static void loginByLark(AuthRequest authData, String authCode, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            try {
                JSONObject body = new JSONObject();
                String connId = config.getSocialConnectionId(Const.EC_TYPE_LARK_INTERNAL);
                connId = TextUtils.isEmpty(connId) ? config.getSocialConnectionId(Const.EC_TYPE_LARK_PUBLIC) : connId;
                body.put("connId", connId);
                body.put("code", authCode);
                String endpoint = "/api/v2/ecConn/lark/authByCode";
                Guardian.post(endpoint, body, (data)-> {
                    startOidcInteraction(authData, data, callback);
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }

    public static void loginByOneAuth(String token, String accessToken, @NotNull AuthCallback<UserInfo> callback) {
        loginByOneAuth(null, token, accessToken, callback);
    }

    public static void loginByOneAuth(AuthRequest authData, String token, String accessToken, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("token", token);
            body.put("accessToken", accessToken);
            Guardian.post("/api/v2/ecConn/oneAuth/login", body, (data)-> {
                startOidcInteraction(authData, data, callback);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void bindEmail(String email, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("emailCode", code);
            String endpoint = "/api/v2/users/email/bind";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void unbindEmail(@NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            String endpoint = "/api/v2/users/email/unbind";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void bindPhone(String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        bindPhone(null, phone, code, callback);
    }

    public static void bindPhone(String phoneCountryCode, String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            if (!Util.isNull(phoneCountryCode)){
                body.put("phoneCountryCode", phoneCountryCode);
            }
            body.put("phone", phone);
            body.put("phoneCode", code);
            String endpoint = "/api/v2/users/phone/bind";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void unbindPhone(@NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            String endpoint = "/api/v2/users/phone/unbind";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void updatePhone(String phoneCountryCode, String phone, String code,
                                   String oldPhoneCountryCode, String oldPhone, String oldCode,
                                   @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            if (!Util.isNull(phoneCountryCode)){
                body.put("phoneCountryCode", phoneCountryCode);
            }
            body.put("phone", phone);
            body.put("phoneCode", code);
            if (!Util.isNull(oldPhoneCountryCode)){
                body.put("oldPhoneCountryCode", oldPhoneCountryCode);
            }
            if (!Util.isNull(oldPhone)){
                body.put("oldPhone", oldPhone);
            }
            if (!Util.isNull(oldCode)){
                body.put("oldPhoneCode", oldCode);
            }
            String url = "/api/v2/users/phone/update";
            Guardian.post(url, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static PasswordStrength computePasswordSecurityLevel(String password) {
        if (password.length() < 6) {
            return PasswordStrength.EWeak;
        }

        boolean hasEnglish = Validator.hasEnglish(password);
        boolean hasNumber = Validator.hasNumber(password);
        boolean hasSpecialChar = Validator.hasSpecialCharacter(password);
        if (hasEnglish && hasNumber && hasSpecialChar) {
            return PasswordStrength.EStrong;
        } else if ((hasEnglish && hasNumber) ||
                (hasEnglish && hasSpecialChar) ||
                (hasNumber && hasSpecialChar)) {
            return PasswordStrength.EMedium;
        } else {
            return PasswordStrength.EWeak;
        }
    }

    public static void getSecurityLevel(@NotNull AuthCallback<JSONObject> callback) {
        try {
            String endpoint = "/api/v2/users/me/security-level";
            Guardian.get(endpoint, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void listRoles(@NotNull AuthCallback<List<Role>> callback) {
        listRoles(null, callback);
    }

    public static void listRoles(String namespace, @NotNull AuthCallback<List<Role>> callback) {
        try {
            String endpoint = "/api/v2/users/me/roles"
                    + (TextUtils.isEmpty(namespace) ? "" : "?namespace=" + namespace);
            Guardian.get(endpoint, (data)-> {
                if (data.getCode() == 200) {
                    try {
                        JSONArray array = data.getData().getJSONArray("data");
                        List<Role> roles = Role.parse(array);
                        UserInfo userInfo = Authing.getCurrentUser();
                        if (userInfo != null) {
                            userInfo.setRoles(roles);
                        }
                        callback.call(data.getCode(), data.getMessage(), roles);
                    } catch (JSONException e) {
                        callback.call(500, "Exception", null);
                    }
                } else {
                    callback.call(data.getCode(), data.getMessage(), null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void listApplications(@NotNull AuthCallback<List<Application>> callback) {
        listApplications(1, 100, callback);
    }

    public static void listApplications(int page, int limit, @NotNull AuthCallback<List<Application>> callback) {
        try {
            String endpoint = "/api/v2/users/me/applications/allowed?page="
                    + page + "&limit=" + limit;
            Guardian.get(endpoint, (data)-> {
                if (data.getCode() == 200) {
                    try {
                        JSONArray array = data.getData().getJSONArray("list");
                        List<Application> apps = Application.parse(array);
                        UserInfo userInfo = Authing.getCurrentUser();
                        if (userInfo != null) {
                            userInfo.setApplications(apps);
                        }
                        callback.call(data.getCode(), data.getMessage(), apps);
                    } catch (JSONException e) {
                        callback.call(500, "Exception", null);
                    }
                } else {
                    callback.call(data.getCode(), data.getMessage(), null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void listAuthorizedResources(String namespace, @NotNull AuthCallback<List<Resource>> callback) {
        listAuthorizedResources(namespace, null, callback);
    }

    public static void listAuthorizedResources(String namespace, String resourceType, @NotNull AuthCallback<List<Resource>> callback) {
        try {
            String endpoint = "/api/v2/users/resource/authorized";
            JSONObject body = new JSONObject();
            body.put("namespace", namespace);
            if (resourceType != null) {
                body.put("resourceType", resourceType);
            }
            Guardian.post(endpoint, body, (data)-> {
                if (data.getCode() == 200) {
                    try {
                        JSONArray array = data.getData().getJSONArray("list");
                        List<Resource> resources = Resource.parse(array);
                        UserInfo userInfo = Authing.getCurrentUser();
                        if (userInfo != null) {
                            userInfo.setResources(resources);
                        }
                        callback.call(data.getCode(), data.getMessage(), resources);
                    } catch (JSONException e) {
                        callback.call(500, "Exception", null);
                    }
                } else {
                    callback.call(data.getCode(), data.getMessage(), null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void listOrgs(@NotNull AuthCallback<List<Organization[]>> callback) {
        try {
            UserInfo userInfo = Authing.getCurrentUser();
            if (userInfo == null) {
                callback.call(2020, "", null);
            } else {
                String endpoint = "/api/v2/users/" + userInfo.getId() + "/orgs";
                Guardian.get(endpoint, (data)-> {
                    if (data.getCode() == 200) {
                        try {
                            JSONArray array = data.getData().getJSONArray("data");
                            List<Organization[]> organizations = Organization.parse(array);
                            userInfo.setOrganizations(organizations);
                            callback.call(data.getCode(), data.getMessage(), organizations);
                        } catch (JSONException e) {
                            callback.call(500, "Exception", null);
                        }
                    } else {
                        callback.call(data.getCode(), data.getMessage(), null);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void updateIdToken(@NotNull AuthCallback<UserInfo> callback) {
        try {
            String endpoint = "/api/v2/users/refresh-token";
            JSONObject body = new JSONObject();
            Guardian.post(endpoint, body, (data)-> {
                if (data.getCode() == 200) {
                    createUserInfoFromResponse(Authing.getCurrentUser(), data, callback);
                } else {
                    callback.call(data.getCode(), data.getMessage(), null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void mfaCheck(String phone, String email, @NotNull AuthCallback<Boolean> callback) {
        try {
            JSONObject body = new JSONObject();
            if (phone != null)
                body.put("phone", phone);
            if (email != null)
                body.put("email", email);
            String endpoint = "/api/v2/applications/mfa/check";
            Guardian.post(endpoint, body, (data)-> {
                try {
                    if (data.getCode() == 200) {
                        boolean ok = data.getData().getBoolean("result");
                        callback.call(data.getCode(), data.getMessage(), ok);
                    } else {
                        callback.call(data.getCode(), data.getMessage(), false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.call(500, "Exception", null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void mfaVerifyByPhone(String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        mfaVerifyByPhone(null, phone, code, callback);
    }

    public static void mfaVerifyByPhone(String phoneCountryCode, String phone, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            if (!Util.isNull(phoneCountryCode)){
                body.put("phoneCountryCode", phoneCountryCode);
            }
            body.put("phone", phone);
            body.put("code", code);
            String endpoint = "/api/v2/applications/mfa/sms/verify";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void mfaVerifyByEmail(String email, String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("code", code);
            String endpoint = "/api/v2/applications/mfa/email/verify";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void mfaVerifyByOTP(String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("authenticatorType", "totp");
            body.put("totp", code);
            String endpoint = "/api/v2/mfa/totp/verify";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void mfaVerifyByRecoveryCode(String code, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("authenticatorType", "totp");
            body.put("recoveryCode", code);
            String endpoint = "/api/v2/mfa/totp/recovery";
            Guardian.post(endpoint, body, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void updatePassword(String newPassword, String oldPassword, @NotNull AuthCallback<JSONObject> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("newPassword", Util.encryptPassword(newPassword));
            if (oldPassword != null) {
                body.put("oldPassword", Util.encryptPassword(oldPassword));
            }
            String endpoint = "/api/v2/password/update";
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void getCurrentUser(@NotNull AuthCallback<UserInfo> callback) {
        getCurrentUser(new UserInfo(), callback);
    }

    public static void getCurrentUser(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        try {
            String endpoint = "/api/v2/users/me";
            Guardian.get(endpoint, (data)-> createUserInfoFromResponse(userInfo, data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    @Deprecated
    public static void updateUser(JSONObject object, @NotNull AuthCallback<UserInfo> callback) {
        updateProfile(object, callback);
    }

    public static void updateProfile(JSONObject object, @NotNull AuthCallback<UserInfo> callback) {
        try {
            String endpoint = "/api/v2/users/profile/update";
            Guardian.post(endpoint, object, (data)-> createUserInfoFromResponse(data, callback));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    @Deprecated
    public static void updateCustomUserInfo(JSONObject customData, @NotNull AuthCallback<JSONObject> callback) {
        setCustomUserData(customData, callback);
    }

    public static void setCustomUserData(JSONObject customData, @NotNull AuthCallback<JSONObject> callback) {
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            callback.call(500, "no user logged in", null);
            return;
        }

        try {
            JSONArray array = new JSONArray();
            for (Iterator<String> it = customData.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONObject obj = new JSONObject();
                obj.put("definition", key);
                obj.put("value", customData.get(key));
                array.put(obj);
            }
            JSONObject body = new JSONObject();
            body.put("udfs", array);
            String endpoint = "/api/v2/udfs/values";
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void getCustomUserData(UserInfo userInfo, @NotNull AuthCallback<UserInfo> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("targetType", "USER");
            body.put("targetId", userInfo.getId());
            Guardian.post("/api/v2/udvs/get", body, (data)-> {
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
    }

    public static void logout(@NotNull AuthCallback<?> callback) {
        try {
            String endpoint = "/api/v2/logout?app_id=" + Authing.getAppId();
            Guardian.get(endpoint, (data)-> {
                Safe.logoutUser(Authing.getCurrentUser());
                Authing.setCurrentUser(null);
                CookieManager.removeAllCookies();
                callback.call(data.getCode(), data.getMessage(), null);
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void deleteAccount(AuthCallback<JSONObject> callback) {
        try {
            String endpoint = "/api/v2/users/delete";
            Guardian.delete(endpoint, (data)-> {
                if (data.getCode() == 200) {
                    Safe.logoutUser(Authing.getCurrentUser());
                    Authing.setCurrentUser(null);
                    CookieManager.removeAllCookies();
                }
                callback.call(data.getCode(), data.getMessage(), data.getData());
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void markQRCodeScanned(String ticket, @NotNull AuthCallback<JSONObject> callback) {
        try {
            String endpoint = "/api/v2/qrcode/scanned";
            JSONObject body = new JSONObject();
            body.put("random", ticket);
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void loginByScannedTicket(boolean autoMarkScanned, String ticket, @NotNull AuthCallback<JSONObject> callback) {
        if (autoMarkScanned) {
            markQRCodeScanned(ticket, ((code, message, data) -> {
                if (code == 200) {
                    loginByScannedTicket(ticket, callback);
                } else {
                    callback.call(code, message, data);
                }
            }));
        } else {
            loginByScannedTicket(ticket, callback);
        }
    }

    public static void loginByScannedTicket(String ticket, @NotNull AuthCallback<JSONObject> callback) {
        try {
            String endpoint = "/api/v2/qrcode/confirm";
            JSONObject body = new JSONObject();
            body.put("random", ticket);
            Guardian.post(endpoint, body, (data)-> callback.call(data.getCode(), data.getMessage(), data.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(500, "Exception", null);
        }
    }

    public static void createUserInfoFromResponse(Response data, @NotNull AuthCallback<UserInfo> callback) {
        createUserInfoFromResponse(new UserInfo(), data, callback);
    }

    public static void createUserInfoFromResponse(UserInfo userInfo, Response data, @NotNull AuthCallback<UserInfo> callback) {
        int code = data.getCode();
        try {
            if (code == 200) {
                userInfo = UserInfo.createUserInfo(userInfo, data.getData());
                Authing.saveUser(userInfo);
                String token = userInfo.getIdToken();
                if (Util.isNull(token)) {
                    callback.call(code, data.getMessage(), userInfo);
                } else {
                    getCustomUserData(userInfo, callback);
                }
            } else if (code == EC_MFA_REQUIRED) {
                MFAData mfaData = MFAData.create(data.getData());
                userInfo.setMfaData(mfaData);
                callback.call(code, data.getMessage(), userInfo);
            } else if (code == EC_FIRST_TIME_LOGIN) {
                JSONObject o = data.getData();
                if (o.has("token")) {
                    String token = o.getString("token");
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

    private static void startOidcInteraction(AuthRequest authData, Response data, @NotNull AuthCallback<UserInfo> callback){
        if (authData == null) {
            createUserInfoFromResponse(data, callback);
        } else if (data.getCode() == 200) {
            try {
                UserInfo userInfo = UserInfo.createUserInfo(data.getData());
                String token = userInfo.getIdToken();
                authData.setToken(token);
                new OIDCClient(authData).authByToken(token, callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            callback.call(data.getCode(), data.getMessage(), null);
        }
    }
}
