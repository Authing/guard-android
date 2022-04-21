package cn.authing.guard.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import cn.authing.guard.Authing;
import cn.authing.guard.util.Util;

public class Safe {

    private static final String SP_NAME = "SP_AUTHING_GUARD";

    private static final String SP_KEY_ACCOUNT = "SP_ACCOUNT";
    private static final String SP_KEY_PASSWORD = "SP_PASSWORD";
    private static final String SP_KEY_TOKEN = "SP_TOKEN";
    private static final String SP_KEY_PHONE_COUNTRY_CODE = "SP_PHONE_COUNTRY_CODE";

    public static void saveAccount(String account) {
        SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_ACCOUNT, account).commit();
    }

    public static String loadAccount() {
        SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_ACCOUNT, "");
    }

    public static void savePassword(String password) {
        // TODO encrypt
        SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_PASSWORD, password).commit();
    }

    public static String loadPassword() {
        if (Authing.getAppContext() != null) {
            SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, 0);
            return sp.getString(SP_KEY_PASSWORD, "");
        } else {
            return "";
        }
    }

    public static void savePhoneCountryCode(String code) {
        if (Authing.getAppContext() != null) {
            SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
            sp.edit().putString(SP_KEY_PHONE_COUNTRY_CODE, code).commit();
        }
    }

    public static String loadPhoneCountryCode() {
        if (Authing.getAppContext() != null) {
            SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, 0);
            return sp.getString(SP_KEY_PHONE_COUNTRY_CODE, "");
        } else {
            return "";
        }
    }

    public static void saveUser(UserInfo userInfo) {
        if (Authing.getAppContext() != null) {
            SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
            sp.edit().putString(SP_KEY_TOKEN, userInfo.getIdToken()).commit();
        }
    }

    public static UserInfo loadUser() {
        if (Authing.getAppContext() != null) {
            SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, 0);
            String token = sp.getString(SP_KEY_TOKEN, "");
            if (Util.isNull(token)) {
                return null;
            } else {
                UserInfo userInfo = new UserInfo();
                userInfo.setIdToken(token);
                return userInfo;
            }
        } else {
            return null;
        }
    }

    public static void logoutUser(UserInfo userInfo) {
        if (Authing.getAppContext() != null) {
            SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
            sp.edit().remove(SP_KEY_TOKEN).commit();
        }
    }
}
