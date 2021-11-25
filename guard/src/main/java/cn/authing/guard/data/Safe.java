package cn.authing.guard.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import cn.authing.guard.Authing;

public class Safe {

    public static final String SP_NAME = "SP_AUTHING_GUARD";

    public static final String SP_KEY_ACCOUNT = "SP_ACCOUNT";
    public static final String SP_KEY_PASSWORD = "SP_PASSWORD";

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
        SharedPreferences sp = Authing.getAppContext().getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_PASSWORD, "");
    }
}
