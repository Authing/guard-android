package cn.authing;

import android.content.Context;
import android.content.SharedPreferences;

import cn.authing.guard.Authing;


public class App extends android.app.Application {

    public static final String SP_NAME = "SP_AUTHING_GUARD";

    public static final String SP_KEY_SCHEMA = "SP_SCHEMA";
    public static final String SP_KEY_HOST = "SP_HOST";
    public static final String SP_KEY_APPID = "SP_APPID";

    public static void saveScheme(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_SCHEMA, s).commit();
    }

    public static String loadScheme(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_SCHEMA, "https");
    }

    public static void saveHost(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_HOST, s).commit();
    }

    public static String loadHost(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_HOST, "console.authing.cn");
    }

    public static void saveAppId(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_APPID, s).commit();
    }

    public static String loadAppId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_APPID, "60caaf41df670b771fd08937");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String schema = loadScheme(this);
        String host = loadHost(this);
        String appid = loadAppId(this);
        Authing.setScheme(schema);
        Authing.setHost(host);
        Authing.init(getApplicationContext(), appid);
    }
}
