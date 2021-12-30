package cn.authing;

import android.content.Context;
import android.content.SharedPreferences;

import cn.authing.guard.Authing;
import cn.authing.guard.oneclick.OneClick;
import cn.authing.guard.social.Alipay;
import cn.authing.guard.social.WeCom;
import cn.authing.guard.social.Wechat;

public class App extends android.app.Application {

    public static final String SP_NAME = "SP_AUTHING_GUARD";

    public static final String SP_KEY_SCHEMA = "SP_SCHEMA";
    public static final String SP_KEY_HOST = "SP_HOST";
    public static final String SP_KEY_APPID = "SP_APPID";

    public static void saveSchema(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_SCHEMA, s).commit();
    }

    public static String loadSchema(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_SCHEMA, "https");
    }

    public static void saveHost(Context context, String s) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_HOST, s).commit();
    }

    public static String loadHost(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(SP_KEY_HOST, "authing.cn");
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

        // social public info has to be set manually
        Wechat.appId = "wx1cddb15e280c0f67";

        Alipay.appId = "2021002192647456";

        WeCom.corpId = "ww2fe68893d538b6c1";
        WeCom.agentId = "1000003";
        WeCom.schema = "wwauth2fe68893d538b6c1000003";

        // one click
        OneClick.bizId = "74ae90bd84f74b69a88b578bbbbcdcfd";

        String schema = loadSchema(this);
        String host = loadHost(this);
        String appid = loadAppId(this);
        Authing.setSchema(schema);
        Authing.setHost(host);
        Authing.init(getApplicationContext(), appid);
    }
}
