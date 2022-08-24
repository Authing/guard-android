package cn.authing.ut;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.Application;
import cn.authing.guard.data.Organization;
import cn.authing.guard.data.Resource;
import cn.authing.guard.data.Role;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;

public class HttpUtil {

    public static void sync(TestCase testCase, IHttpCallBack callBack) {
        if (testCase == null) {
            return;
        }
        String apiName = testCase.getApiName();
        if (TextUtils.isEmpty(apiName)) {
            return;
        }
        String params = testCase.getParams();
        if ("registerByEmail".equals(apiName)) {
            registerByEmail(params, callBack);
        } else if ("registerByPhoneCode".equals(apiName)) {
            registerByPhoneCode(params, callBack);
        } else if ("registerByEmailCode".equals(apiName)) {
            registerByEmailCode(params, callBack);
        } else if ("loginByAccount".equals(apiName)) {
            loginByAccount(params, callBack);
        } else if ("loginByPhoneCode".equals(apiName)) {
            loginByPhoneCode(params, callBack);
        } else if ("loginByEmailCode".equals(apiName)) {
            loginByEmailCode(params, callBack);
        } else if ("loginByWechat".equals(apiName)) {
            loginByWechat(params, callBack);
        } else if ("loginByWecom".equals(apiName)) {
            loginByWecom(params, callBack);
        } else if ("loginByWecomAgency".equals(apiName)) {
            loginByWecomAgency(params, callBack);
        } else if ("loginByAlipay".equals(apiName)) {
            loginByAlipay(params, callBack);
        } else if ("loginByLark".equals(apiName)) {
            loginByLark(params, callBack);
        } else if ("loginByOneAuth".equals(apiName)) {
            loginByOneAuth(params, callBack);
        } else if ("sendSms".equals(apiName)) {
            sendSms(params, callBack);
        } else if ("sendEmail".equals(apiName)) {
            sendEmail(params, callBack);
        } else if ("getCurrentUser".equals(apiName)) {
            getCurrentUser(callBack);
        } else if ("getCustomUserData".equals(apiName)) {
            getCustomUserData(callBack);
        } else if ("listRoles".equals(apiName)) {
            listRoles(callBack);
        } else if ("listApplications".equals(apiName)) {
            listApplications(callBack);
        } else if ("listAuthorizedResources".equals(apiName)) {
            listAuthorizedResources(callBack);
        } else if ("listOrgs".equals(apiName)) {
            listOrgs(callBack);
        } else if ("bindPhone".equals(apiName)) {
            bindPhone(params, callBack);
        } else if ("unbindPhone".equals(apiName)) {
            unBindPhone(callBack);
        } else if ("bindEmail".equals(apiName)) {
            bindEmail(params, callBack);
        } else if ("unbindEmail".equals(apiName)) {
            unbindEmail(callBack);
        } else if ("resetPasswordByPhoneCode".equals(apiName)) {
            resetPasswordByPhoneCode(params, callBack);
        } else if ("resetPasswordByEmailCode".equals(apiName)) {
            resetPasswordByEmailCode(params, callBack);
        } else if ("updatePassword".equals(apiName)) {
            updatePassword(params, callBack);
        } else if ("updateProfile".equals(apiName)) {
            updateProfile(params, callBack);
        } else if ("mfaCheck".equals(apiName)) {
            mfaCheck(params, callBack);
        } else if ("mfaVerifyByPhone".equals(apiName)) {
            mfaVerifyByPhone(params, callBack);
        } else if ("mfaVerifyByEmail".equals(apiName)) {
            mfaVerifyByEmail(params, callBack);
        } else if ("mfaVerifyByOTP".equals(apiName)) {
            mfaVerifyByOTP(params, callBack);
        } else if ("mfaVerifyByRecoveryCode".equals(apiName)) {
            mfaVerifyByRecoveryCode(params, callBack);
        } else if ("logout".equals(apiName)) {
            logout(callBack);
        } else if ("delete".equals(apiName)) {
            deleteAccount(callBack);
        } else if ("checkAccount".equals(apiName)) {
            checkAccount(params, callBack);
        } else if ("checkPassword".equals(apiName)) {
            checkPassword(params, callBack);
        } else if ("getSecurityLevel".equals(apiName)) {
            getSecurityLevel(callBack);
        }
    }


    private static void registerByEmail(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.registerByEmail(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("registerByEmail", code, message, data);
            }
        });
    }

    private static void registerByPhoneCode(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.registerByPhoneCode(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("registerByPhoneCode", code, message, data);
            }
        });
    }

    private static void registerByEmailCode(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.registerByEmailCode(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("registerByEmailCode", code, message, data);
            }
        });
    }


    private static void loginByAccount(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.loginByAccount(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByAccount", code, message, data);
            }
        });
    }

    private static void loginByPhoneCode(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.loginByPhoneCode(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByPhoneCode", code, message, data);
            }
        });
    }

    private static void loginByEmailCode(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.loginByEmailCode(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByEmailCode", code, message, data);
            }
        });
    }

    private static void loginByWechat(String params, IHttpCallBack callBack) {
        AuthClient.loginByWechat(params, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByWechat", code, message, data);
            }
        });
    }

    private static void loginByWecom(String params, IHttpCallBack callBack) {
        AuthClient.loginByWecom(params, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByWecom", code, message, data);
            }
        });
    }

    private static void loginByWecomAgency(String params, IHttpCallBack callBack) {
        AuthClient.loginByWecomAgency(params, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByWecomAgency", code, message, data);
            }
        });
    }

    private static void loginByAlipay(String params, IHttpCallBack callBack) {
        AuthClient.loginByAlipay(params, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByAlipay", code, message, data);
            }
        });
    }

    private static void loginByLark(String params, IHttpCallBack callBack) {
        AuthClient.loginByLark(params, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByLark", code, message, data);
            }
        });
    }

    private static void loginByOneAuth(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.loginByOneAuth(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("loginByOneAuth", code, message, data);
            }
        });
    }

    private static void sendSms(String params, IHttpCallBack callBack) {
        if (TextUtils.isEmpty(params)) {
            AuthClient.sendSms("", new AuthCallback<UserInfo>() {
                @Override
                public void call(int code, String message, UserInfo data) {
                    callBack.showResult("sendSms", code, message, data);
                }
            });
            return;
        }

        String[] paramsArr = params.split(",");
        if (paramsArr.length == 1) {
            AuthClient.sendSms(paramsArr[0], new AuthCallback<UserInfo>() {
                @Override
                public void call(int code, String message, UserInfo data) {
                    callBack.showResult("sendSms", code, message, data);
                }
            });
            return;
        }

        AuthClient.sendSms(paramsArr[0], paramsArr[1], new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("sendSms", code, message, data);
            }
        });
    }

    private static void sendEmail(String params, IHttpCallBack callBack) {
        if (TextUtils.isEmpty(params)) {
            AuthClient.sendEmail("", "", new AuthCallback<JSONObject>() {
                @Override
                public void call(int code, String message, JSONObject data) {
                    callBack.showResult("sendEmail", code, message, null);
                }
            });
            return;
        }

        String[] paramsArr = params.split(",");
        AuthClient.sendEmail(paramsArr[0], paramsArr[1], new AuthCallback<JSONObject>() {
            @Override
            public void call(int code, String message, JSONObject data) {
                callBack.showResult("sendEmail", code, message, null);
            }
        });
    }

    private static void getCurrentUser(IHttpCallBack callBack) {
        AuthClient.getCurrentUser(new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("getCurrentUser", code, message, data);
            }
        });
    }

    private static void getCustomUserData(IHttpCallBack callBack) {
        AuthClient.getCustomUserData(Authing.getCurrentUser(), new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("getCustomUserData", code, message, data);
            }
        });
    }

    private static void listRoles(IHttpCallBack callBack) {
        AuthClient.listRoles(new AuthCallback<List<Role>>() {
            @Override
            public void call(int code, String message, List<Role> data) {
                callBack.showResult("listRoles", code, message, null);
            }
        });
    }

    private static void listApplications(IHttpCallBack callBack) {
        AuthClient.listApplications(new AuthCallback<List<Application>>() {
            @Override
            public void call(int code, String message, List<Application> data) {
                callBack.showResult("listApplications", code, message, null);
            }
        });
    }

    private static void listAuthorizedResources(IHttpCallBack callBack) {
        AuthClient.listAuthorizedResources("default", new AuthCallback<List<Resource>>() {
            @Override
            public void call(int code, String message, List<Resource> data) {
                callBack.showResult("listAuthorizedResources", code, message, null);
            }
        });
    }

    private static void listOrgs(IHttpCallBack callBack) {
        AuthClient.listOrgs(new AuthCallback<List<Organization[]>>() {
            @Override
            public void call(int code, String message, List<Organization[]> data) {
                callBack.showResult("listOrgs", code, message, null);
            }
        });
    }

    private static void bindPhone(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        if (paramsArr.length == 1 || paramsArr.length == 2) {
            AuthClient.bindPhone(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
                @Override
                public void call(int code, String message, UserInfo data) {
                    callBack.showResult("bindPhone", code, message, data);
                }
            });
            return;
        }

        AuthClient.bindPhone(paramsArr[0], paramsArr[1], paramsArr[2], new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("bindPhone", code, message, data);
            }
        });
    }

    private static void unBindPhone(IHttpCallBack callBack) {
        AuthClient.unbindPhone(new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("unBindPhone", code, message, data);
            }
        });
    }

    private static void bindEmail(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.bindEmail(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("bindEmail", code, message, data);
            }
        });
    }

    private static void unbindEmail(IHttpCallBack callBack) {
        AuthClient.unbindEmail(new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("unbindEmail", code, message, data);
            }
        });
    }


    private static void resetPasswordByPhoneCode(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        if (paramsArr.length == 3) {
            AuthClient.resetPasswordByPhoneCode(paramsArr[0], paramsArr[1], paramsArr[1], new AuthCallback<JSONObject>() {
                @Override
                public void call(int code, String message, JSONObject data) {
                    callBack.showResult("resetPasswordByPhoneCode", code, message, null);
                }
            });
            return;
        }

        AuthClient.resetPasswordByPhoneCode(paramsArr.length > 0 ? paramsArr[0] : "",
                paramsArr.length > 1 ? paramsArr[1] : "",
                paramsArr.length > 2 ? paramsArr[2] : "",
                paramsArr.length > 3 ? paramsArr[3] : "", new AuthCallback<JSONObject>() {
                    @Override
                    public void call(int code, String message, JSONObject data) {
                        callBack.showResult("resetPasswordByPhoneCode", code, message, null);
                    }
                });
    }

    private static void resetPasswordByEmailCode(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.resetPasswordByEmailCode(paramsArr.length > 0 ? paramsArr[0] : "",
                paramsArr.length > 1 ? paramsArr[1] : "",
                paramsArr.length > 2 ? paramsArr[2] : "", new AuthCallback<JSONObject>() {
                    @Override
                    public void call(int code, String message, JSONObject data) {
                        callBack.showResult("resetPasswordByEmailCode", code, message, null);
                    }
                });
    }

    private static void updatePassword(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.updatePassword(paramsArr.length > 0 ? paramsArr[0] : "",
                paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<JSONObject>() {
                    @Override
                    public void call(int code, String message, JSONObject data) {
                        callBack.showResult("updatePassword", code, message, null);
                    }
                });
    }

    private static void updateProfile(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        JSONObject object = new JSONObject();
        try {
            object.put(paramsArr[0], paramsArr[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AuthClient.updateProfile(object, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("updateProfile", code, message, data);
            }
        });
    }

    private static void mfaCheck(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.mfaCheck(paramsArr.length > 0 ? paramsArr[0] : "",
                paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<Boolean>() {
                    @Override
                    public void call(int code, String message, Boolean data) {
                        callBack.showResult("updatePassword", code, message, null);
                    }
                });
    }

    private static void mfaVerifyByPhone(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        if (paramsArr.length == 1 || paramsArr.length == 2) {
            AuthClient.mfaVerifyByPhone(paramsArr[0], paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
                @Override
                public void call(int code, String message, UserInfo data) {
                    callBack.showResult("bindPhone", code, message, data);
                }
            });
            return;
        }

        AuthClient.mfaVerifyByPhone(paramsArr[0], paramsArr[1], paramsArr[2], new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("bindPhone", code, message, data);
            }
        });
    }

    private static void mfaVerifyByEmail(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.mfaVerifyByEmail(paramsArr.length > 0 ? paramsArr[0] : "",
                paramsArr.length > 1 ? paramsArr[1] : "", new AuthCallback<UserInfo>() {
                    @Override
                    public void call(int code, String message, UserInfo data) {
                        callBack.showResult("updatePassword", code, message, null);
                    }
                });
    }

    private static void mfaVerifyByOTP(String params, IHttpCallBack callBack) {
        AuthClient.mfaVerifyByOTP(params, new AuthCallback<UserInfo>() {
                    @Override
                    public void call(int code, String message, UserInfo data) {
                        callBack.showResult("updatePassword", code, message, null);
                    }
                });
    }

    private static void mfaVerifyByRecoveryCode(String params, IHttpCallBack callBack) {
        AuthClient.mfaVerifyByRecoveryCode(params, new AuthCallback<UserInfo>() {
            @Override
            public void call(int code, String message, UserInfo data) {
                callBack.showResult("updatePassword", code, message, null);
            }
        });
    }

    private static void logout(IHttpCallBack callBack) {
        AuthClient.logout(new AuthCallback<Object>() {
            @Override
            public void call(int code, String message, Object data) {
                callBack.showResult("logout", code, message, null);
            }
        });
    }

    private static void deleteAccount(IHttpCallBack callBack) {
        AuthClient.deleteAccount((AuthCallback<JSONObject>) (code, message, data) -> callBack.showResult("deleteAccount", code, message, null));
    }

    private static void checkAccount(String params, IHttpCallBack callBack) {
        String[] paramsArr = params.split(",");
        AuthClient.checkAccount(paramsArr[0], paramsArr[1], new AuthCallback<JSONObject>() {
            @Override
            public void call(int code, String message, JSONObject data) {
                callBack.showResult("checkAccount", code, message, null);
            }
        });
    }

    private static void checkPassword(String params, IHttpCallBack callBack) {
        AuthClient.checkPassword(params, (AuthCallback<JSONObject>) (code, message, data) -> callBack.showResult("checkPassword", code, message, null));
    }

    private static void getSecurityLevel(IHttpCallBack callBack) {
        AuthClient.getSecurityLevel((AuthCallback<JSONObject>) (code, message, data) -> callBack.showResult("getSecurityLevel", code, message, null));
    }
}
