package cn.authing.ut;

import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class TestCaseUtil {

    public static TestCase createRegisterByEmailCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("注册");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        testCase.setCaseName("邮箱密码");
        testCase.setApiName("registerByEmail");
        testCase.setUrl("/api/v2/register/email");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱号+密码");
            testCase.setParams("389000577@qq.com,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传邮箱号");
            testCase.setParams("389000577@qq.com,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传密码");
            testCase.setParams(",123456");
        }
        return testCase;
    }

    public static TestCase createRegisterByPhoneCodeCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("注册");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        testCase.setCaseName("短信验证码");
        testCase.setApiName("registerByPhoneCode");
        testCase.setUrl("/api/v2/register/phone-code");
        if (type == 0) {
            testCase.setCaseSubName("传手机号码+验证码");
            testCase.setParams("19129910165,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传电话号码");
            testCase.setParams("19129910165,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传密码");
            testCase.setParams(",123456");
        }
        return testCase;
    }

    public static TestCase createSocialLoginCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("社会化登录");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        if (type == 0) {
            testCase.setCaseName("微信");
            testCase.setApiName("loginByWechat");
            testCase.setUrl("/api/v2/ecConn/wechatMobile/authByCode");
            testCase.setCaseSubName("");
            testCase.setParams("19129910165");
        } else if (type == 1) {
            testCase.setCaseName("企业微信");
            testCase.setApiName("loginByWecom");
            testCase.setUrl("/api/v2/ecConn/wechat-work/authByCode");
            testCase.setCaseSubName("");
            testCase.setParams("19129910165");
        } else if (type == 2) {
            testCase.setCaseName("企业微信-代理模式");
            testCase.setApiName("loginByWecomAgency");
            testCase.setUrl("/api/v2/ecConn/wechat-work-agency/authByCode");
            testCase.setCaseSubName("");
            testCase.setParams("19129910165");
        } else if (type == 3) {
            testCase.setCaseName("自支付宝");
            testCase.setApiName("loginByAlipay");
            testCase.setUrl("/api/v2/ecConn/alipay/authByCode");
            testCase.setCaseSubName("");
            testCase.setParams("19129910165");
        } else if (type == 4) {
            testCase.setCaseName("飞书");
            testCase.setApiName("loginByLark");
            testCase.setUrl("/api/v2/ecConn/lark/authByCode");
            testCase.setCaseSubName("");
            testCase.setParams("19129910165");
        } else if (type == 5) {
            testCase.setCaseName("易盾一键登录");
            testCase.setApiName("loginByOneAuth");
            testCase.setUrl("/api/v2/ecConn/oneAuth/login");
            testCase.setCaseSubName("");
            testCase.setParams("19129910165,19129910165");
        }
        return testCase;
    }

    public static TestCase createRegisterByEmailCodeCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("注册");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        testCase.setCaseName("邮箱验证码");
        testCase.setApiName("registerByEmailCode");
        testCase.setUrl("/api/v2/register/email-code");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱号+验证码");
            testCase.setParams("389000577@qq.com,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传邮箱号");
            testCase.setParams("89000577@qq.com,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",123456");
        }
        return testCase;
    }


    public static TestCase createLoginByAccountCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("登录");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        testCase.setCaseName("账号密码");
        testCase.setApiName("loginByAccount");
        testCase.setUrl("/api/v2/login/account");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱号+密码");
            testCase.setParams("389000577@qq.com,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("传手机号+密码");
            testCase.setParams("19129910165,123456");
        } else if (type == 2) {
            testCase.setCaseSubName("只传邮箱号");
            testCase.setParams("389000577@qq.com,");
        } else if (type == 3) {
            testCase.setCaseSubName("只传手机号");
            testCase.setParams("19129910165,");
        } else if (type == 4) {
            testCase.setCaseSubName("只传密码");
            testCase.setParams(",123456");
        }
        return testCase;
    }

    public static TestCase createLoginByPhoneCodeCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("登录");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        testCase.setCaseName("短信验证码");
        testCase.setApiName("loginByPhoneCode");
        testCase.setUrl("api/v2/login/phone-code");
        if (type == 0) {
            testCase.setCaseSubName("传手机号+验证码");
            testCase.setParams("389000577@qq.com,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传手机号");
            testCase.setParams("19129910165,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",123456");
        }
        return testCase;
    }

    public static TestCase createLoginByEmailCodeCase(int type, boolean isOidc) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("登录");
        testCase.setMethod("post");
        testCase.setOidc(isOidc);
        testCase.setCaseName("邮箱验证码");
        testCase.setApiName("loginByEmailCode");
        testCase.setUrl("/api/v2/login/email-code");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱号+验证码");
            testCase.setParams("389000577@qq.com,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传邮箱号");
            testCase.setParams("389000577@qq.com,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",123456");
        }
        return testCase;
    }

    public static TestCase createSendSmsCodeCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("发送验证码");
        testCase.setMethod("post");
        testCase.setCaseName("短信");
        testCase.setApiName("sendSms");
        testCase.setUrl("/api/v2/sms/send");
        if (type == 0) {
            testCase.setCaseSubName("传手机号");
            testCase.setParams("19129910165");
        } else if (type == 1) {
            testCase.setCaseSubName("传区号+手机号");
            testCase.setParams("+86,19129910165");
        } else if (type == 2) {
            testCase.setCaseSubName("传错误区号+手机号");
            testCase.setParams("+1,19129910165");
        } else if (type == 4) {
            testCase.setCaseSubName("传空手机号");
            testCase.setParams("");
        }
        return testCase;
    }

    public static TestCase createSendEmailCodeCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("发送验证码");
        testCase.setMethod("post");
        testCase.setCaseName("邮箱");
        testCase.setApiName("sendEmail");
        testCase.setUrl("/api/v2/email/send");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱，发送重置密码邮件，邮件中包含验证码");
            testCase.setParams("389000577@qq.com,RESET_PASSWORD");
        } else if (type == 1) {
            testCase.setCaseSubName("传邮箱，发送验证邮箱的邮件");
            testCase.setParams("389000577@qq.com,VERIFY_EMAIL");
        } else if (type == 2) {
            testCase.setCaseSubName("传邮箱，发送修改邮箱邮件，邮件中包含验证码");
            testCase.setParams("389000577@qq.com,CHANGE_EMAIL");
        } else if (type == 3) {
            testCase.setCaseSubName("传邮箱，发送 MFA 验证邮件");
            testCase.setParams("389000577@qq.com,MFA_VERIFY");
        } else if (type == 4) {
            testCase.setCaseSubName("传邮箱,发送验证码");
            testCase.setParams("389000577@qq.com,VERIFY_CODE");
        } else if (type == 5) {
            testCase.setCaseSubName("传空邮箱");
            testCase.setParams("");
        }
        return testCase;
    }

    public static TestCase createGetUerInfoCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("获取用户信息");
        testCase.setMethod("get");
        testCase.setCaseName("获取用户信息");
        testCase.setApiName("getCurrentUser");
        testCase.setUrl("/api/v2/users/me");
        return testCase;
    }

    public static TestCase createGetCustomUserDataCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("获取用户信息");
        testCase.setMethod("get");
        testCase.setCaseName("获取用户扩展信息");
        testCase.setApiName("getCustomUserData");
        testCase.setUrl("/api/v2/udvs/get");
        return testCase;
    }

    public static TestCase createGetListRolesCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("获取用户信息");
        testCase.setMethod("get");
        testCase.setCaseName("获取用户角色信息");
        testCase.setApiName("listRoles");
        testCase.setUrl("/api/v2/users/me/roles");
        return testCase;
    }

    public static TestCase createGetListApplicationsCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("获取用户信息");
        testCase.setMethod("get");
        testCase.setCaseName("获取用户能够访问的应用信息");
        testCase.setApiName("listApplications");
        testCase.setUrl("/api/v2/users/me/applications/allowed?");
        return testCase;
    }

    public static TestCase createGetListAuthorizedResourcesCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("获取用户信息");
        testCase.setMethod("get");
        testCase.setCaseName("获取用户授权资源信息");
        testCase.setApiName("listAuthorizedResources");
        testCase.setUrl("/api/v2/users/resource/authorized");
        return testCase;
    }

    public static TestCase createGetListOrgsCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("获取用户信息");
        testCase.setMethod("get");
        testCase.setCaseName("获取用户所在组织机构信息");
        testCase.setApiName("listOrgs");
        testCase.setUrl("/api/v2/users/ + userInfo.getId() + /orgs");
        return testCase;
    }

    public static TestCase createBindPhoneCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("绑定手机");
        testCase.setApiName("bindPhone");
        testCase.setUrl("/api/v2/users/phone/bind");
        if (type == 0) {
            testCase.setCaseSubName("传手机号+验证码");
            testCase.setParams("19129910165,1234");
        } else if (type == 1) {
            testCase.setCaseSubName("只传手机号");
            testCase.setParams("19129910165,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",1234");
        } else if (type == 3) {
            testCase.setCaseSubName("传区号+手机号+验证码");
            testCase.setParams("+86,19129910165,1234");
        }
        return testCase;
    }

    public static TestCase createUnbindPhoneCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("解除手机绑定");
        testCase.setApiName("unbindPhone");
        testCase.setUrl("/api/v2/users/phone/unbind");
        return testCase;
    }

    public static TestCase createBindEmailCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("绑定邮箱");
        testCase.setApiName("bindEmail");
        testCase.setUrl("/api/v2/users/email/bind");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱+验证码");
            testCase.setParams("389000577@qq.com,1234");
        } else if (type == 1) {
            testCase.setCaseSubName("只传邮箱");
            testCase.setParams("389000577@qq.com,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",1234");
        } else if (type == 3) {
            testCase.setCaseSubName("传空");
            testCase.setParams("");
        }
        return testCase;
    }

    public static TestCase createUnbindEmailCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("解除邮箱绑定");
        testCase.setApiName("unbindEmail");
        testCase.setUrl("/api/v2/users/email/unbind");
        return testCase;
    }

    public static TestCase createResetPasswordByPhoneCodeCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("手机号重置密码");
        testCase.setApiName("resetPasswordByPhoneCode");
        testCase.setUrl("/api/v2/password/reset/sms");
        if (type == 0) {
            testCase.setCaseSubName("传手机号+验证码+密码");
            testCase.setParams("19129910165,1234,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传手机号+验证码");
            testCase.setParams("19129910165,1234,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传手机号");
            testCase.setParams("19129910165,,");
        } else if (type == 3) {
            testCase.setCaseSubName("只传验证码+密码");
            testCase.setParams(",1234,123456");
        } else if (type == 4) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",1234,");
        }  else if (type == 5) {
            testCase.setCaseSubName("只传密码");
            testCase.setParams(",,123456");
        } else if (type == 6) {
            testCase.setCaseSubName("传区号+手机号+验证码+密码");
            testCase.setParams("+86,19129910165,1234,123456");
        }
        return testCase;
    }

    public static TestCase createResetPasswordByEmailCodeCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("邮箱重置密码");
        testCase.setApiName("resetPasswordByEmailCode");
        testCase.setUrl("/api/v2/password/reset/email");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱+验证码");
            testCase.setParams("389000577@qq.com,1234,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传邮箱");
            testCase.setParams("389000577@qq.com,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",1234");
        } else if (type == 3) {
            testCase.setCaseSubName("传空");
            testCase.setParams("");
        }
        return testCase;
    }

    public static TestCase createUpdatePassword() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setCaseName("更新密码");
        testCase.setCaseSubName(getUserName());
        testCase.setApiName("updatePassword");
        testCase.setMethod("post");
        testCase.setUrl("/api/v2/password/update");
        testCase.setParams("123456,12345678");
        return testCase;
    }

    public static TestCase createUpdateProfileCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("更新用户信息");
        testCase.setMethod("post");
        testCase.setCaseName("更新");
        testCase.setApiName("updateProfile");
        testCase.setUrl("/api/v2/users/profile/update");
        if (type == 0) {
            testCase.setCaseSubName("用户名");
            testCase.setParams("username,test1");
        } else if (type == 1) {
            testCase.setCaseSubName("昵称");
            testCase.setParams("nickname,昵称");
        } else if (type == 2) {
            testCase.setCaseSubName("姓名");
            testCase.setParams("name,z");
        } else if (type == 3) {
            testCase.setCaseSubName("公司");
            testCase.setParams("company,authing");
        } else if (type == 4) {
            testCase.setCaseSubName("Given Name");
            testCase.setParams("givenName,Z");
        } else if (type == 5) {
            testCase.setCaseSubName("Family Name");
            testCase.setParams("familyName,Z");
        } else if (type == 6) {
            testCase.setCaseSubName("Middle Name");
            testCase.setParams("middleName,Z");
        } else if (type == 7) {
            testCase.setCaseSubName("地址");
            testCase.setParams("address,深圳");
        } else if (type == 8) {
            testCase.setCaseSubName("城市");
            testCase.setParams("city,深圳");
        }
        return testCase;
    }

    public static TestCase createMfaCheckCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("MFA");
        testCase.setCaseName("检测手机号或邮箱是否已被绑定");
        testCase.setApiName("mfaCheck");
        testCase.setMethod("post");
        testCase.setUrl("/api/v2/applications/mfa/check");
        if (type == 0) {
            testCase.setCaseSubName("手机号");
            testCase.setParams("19129910165,");
        } else if (type == 1) {
            testCase.setCaseSubName("邮箱");
            testCase.setParams(",389000577@qq.com");
        } else if (type == 2) {
            testCase.setCaseSubName("传空");
            testCase.setParams("");
        }
        return testCase;
    }

    public static TestCase createMfaVerifyByPhoneCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("MFA");
        testCase.setCaseName("检验二次验证 MFA 短信验证码");
        testCase.setApiName("mfaVerifyByPhone");
        testCase.setMethod("post");
        testCase.setUrl("/api/v2/applications/mfa/sms/verify");
        if (type == 0) {
            testCase.setCaseSubName("传手机号+验证码");
            testCase.setParams("19129910165,1234");
        } else if (type == 1) {
            testCase.setCaseSubName("只传手机号");
            testCase.setParams("19129910165,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",1234");
        } else if (type == 3) {
            testCase.setCaseSubName("传区号+手机号+验证码");
            testCase.setParams("+86,19129910165,1234");
        }
        return testCase;
    }

    public static TestCase createMfaVerifyByEmailCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("MFA");
        testCase.setCaseName("检验二次验证 MFA 邮箱验证码");
        testCase.setApiName("mfaVerifyByEmail");
        testCase.setMethod("post");
        testCase.setUrl("/api/v2/applications/mfa/email/verify");
        if (type == 0) {
            testCase.setCaseSubName("传邮箱号+验证码");
            testCase.setParams("389000577@qq.com,123456");
        } else if (type == 1) {
            testCase.setCaseSubName("只传邮箱号");
            testCase.setParams("389000577@qq.com,");
        } else if (type == 2) {
            testCase.setCaseSubName("只传验证码");
            testCase.setParams(",123456");
        }
        return testCase;
    }

    public static TestCase createMfaVerifyByOTPCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("MFA");
        testCase.setCaseName("检验二次验证 MFA 口令");
        testCase.setApiName("mfaVerifyByOTP");
        testCase.setMethod("post");
        testCase.setUrl("/api/v2/mfa/totp/verify");
        testCase.setCaseSubName("");
        testCase.setParams("123456");
        return testCase;
    }

    public static TestCase createMfaVerifyByRecoveryCodeCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("MFA");
        testCase.setCaseName("检验二次验证 MFA 恢复代码");
        testCase.setApiName("mfaVerifyByRecoveryCode");
        testCase.setMethod("post");
        testCase.setUrl("/api/v2/mfa/totp/recovery");
        testCase.setCaseSubName("");
        testCase.setParams("123456");
        return testCase;
    }

    public static TestCase createLogoutCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("账号密码");
        testCase.setCaseName("退出登录");
        testCase.setCaseSubName(getUserName());
        testCase.setApiName("logout");
        testCase.setMethod("get");
        testCase.setUrl("/api/v2/logout");
        return testCase;
    }

    public static TestCase createDeleteAccountCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("账号密码");
        testCase.setCaseName("注销账号");
        testCase.setCaseSubName(getUserName());
        testCase.setApiName("delete");
        testCase.setMethod("get");
        testCase.setUrl("/api/v2/users/delete");
        return testCase;
    }

    public static TestCase createCheckAccountCase(int type) {
        TestCase testCase = new TestCase();
        testCase.setModuleName("账号密码");
        testCase.setCaseName("检测用户是否存在");
        testCase.setCaseSubName(getUserName());
        testCase.setApiName("checkAccount");
        testCase.setMethod("get");
        testCase.setUrl("/api/v2/users/is-user-exists?");
        if (type == 0) {
            testCase.setCaseSubName("手机号");
            testCase.setParams("phone,19129910165");
        } else if (type == 1) {
            testCase.setCaseSubName("邮箱");
            testCase.setParams("email,389000577@qq.com");
        } else if (type == 2) {
            testCase.setCaseSubName("用户名");
            testCase.setParams("username,fin");
        }
        return testCase;
    }

    public static TestCase createCheckPasswordCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("账号密码");
        testCase.setCaseName("检查密码是否合法");
        testCase.setApiName("checkPassword");
        testCase.setMethod("get");
        testCase.setUrl("/api/v2/users/password/check?");
        testCase.setParams("123456");
        return testCase;
    }

    public static TestCase createGetSecurityLevelCase() {
        TestCase testCase = new TestCase();
        testCase.setModuleName("账号密码");
        testCase.setCaseName("获取用户的安全等级评分");
        testCase.setApiName("getSecurityLevel");
        testCase.setMethod("get");
        testCase.setUrl("/api/v2/users/me/security-level");
        return testCase;
    }

    public static String getUserName(){
        UserInfo userInfo = Authing.getCurrentUser();
        String userName = "-";
        if (userInfo == null){
            return userName;
        }
        if (!Util.isNull(userInfo.getNickname())){
            userName = userInfo.getUsername();
        }
        if (!Util.isNull(userInfo.getName())){
            userName = userInfo.getUsername();
        }
        if (!Util.isNull(userInfo.getEmail())){
            userName = userInfo.getEmail();
        }
        if (!Util.isNull(userInfo.getPhone_number())){
            userName = userInfo.getPhone_number();
        }
        if (!Util.isNull(userInfo.getUsername())){
            userName = userInfo.getUsername();
        }
        return userName;
    }

}
