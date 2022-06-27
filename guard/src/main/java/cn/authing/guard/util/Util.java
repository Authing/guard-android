package cn.authing.guard.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.Authing;
import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.ErrorTextView;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.Country;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;

public class Util {

    public static float dp2px(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float px2dp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int sp2px(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scaledDensity + 0.5f);
    }

    public static String encryptPassword(String password) {
        if (isNull(password)){
            return null;
        }
        try {
            byte[] keyBytes = Base64.decode(Authing.getPublicKey(), Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherMsg = cipher.doFinal(password.getBytes());
            return new String(Base64.encode(cipherMsg, Base64.NO_WRAP));
        } catch (Exception e) {
            return "{\"2020\":\"" + e + "\"}";
        }
    }

    public static List<Integer> intDigits(int i) {
        int temp = i;
        ArrayList<Integer> array = new ArrayList<>();
        do{
            array.add(0, temp % 10);
            temp /= 10;
        } while  (temp > 0);
        return array;
    }

    public static List<View> findAllViewByClass(View current, Class<?> T) {
        View view = current.getRootView();
        List<View> result = new ArrayList<>();
        _findAllViewByClass((ViewGroup)view, T, result);
        return result;
    }

    private static void _findAllViewByClass(ViewGroup parent, Class<?> T, List<View> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                _findAllViewByClass((ViewGroup)child, T, result);
            }

            if (child.getClass().equals(T)) {
                result.add(child);
            }
        }
    }

    public static View findViewByClass(View current, Class<?> T) {
        return findViewByClass(current, T, true);
    }

    public static View findViewByClass(View current, Class<?> T, boolean onlyVisible) {
        View view = current.getRootView();
        return findChildViewByClass((ViewGroup)view, T, onlyVisible);
    }

    public static View findChildViewByClass(ViewGroup parent, Class<?> T, boolean onlyVisible) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup && (!onlyVisible || child.isShown())) {
                View result = findChildViewByClass((ViewGroup)child, T, onlyVisible);
                if (result != null) {
                    return result;
                }
            }

            if (T.isInstance(child)) {
                return child;
            }
        }
        return null;
    }

    public static String getAccount(View current) {
        String account = null;
        View v = findViewByClass(current, AccountEditText.class);
        if (v != null) {
            AccountEditText editText = (AccountEditText)v;
            account = editText.getText().toString();
        }
        if (TextUtils.isEmpty(account)) {
            account = AuthFlow.getAccount(current.getContext());
        }
        if (TextUtils.isEmpty(account)) {
            account = Safe.loadAccount();
        }
        return account;
    }

    public static String getPhoneNumber(View current) {
        String phone = null;
        View v = findViewByClass(current, PhoneNumberEditText.class);
        if (v != null) {
            PhoneNumberEditText editText = (PhoneNumberEditText)v;
            phone = editText.getText().toString();
        }
        if (TextUtils.isEmpty(phone)) {
            phone = (String) AuthFlow.get(current.getContext(), AuthFlow.KEY_MFA_PHONE);
        }
        if (TextUtils.isEmpty(phone)) {
            String account = AuthFlow.getAccount(current.getContext());
            if (Validator.isValidPhoneNumber(account)) {
                phone = account;
            }
        }
        if (TextUtils.isEmpty(phone)) {
            phone = Safe.loadAccount();
        }
        if (TextUtils.isEmpty(phone)) {
            UserInfo userInfo = Authing.getCurrentUser();
            if (userInfo != null) {
                phone = userInfo.getPhone_number();
            }
        }
        return phone;
    }

    public static String getPhoneCountryCode(View current) {
        String phoneCountryCode = null;
        View v = findViewByClass(current, CountryCodePicker.class);
        if (v != null) {
            phoneCountryCode = ((CountryCodePicker)v).getCountryCode();
        }
        if (TextUtils.isEmpty(phoneCountryCode)) {
            return getPhoneCountryCodeByCache(current.getContext());
        }
        return phoneCountryCode;
    }

    public static String getPhoneCountryCodeByCache(Context context) {
        String phoneCountryCode = (String) AuthFlow.get(context, AuthFlow.KEY_MFA_PHONE_COUNTRY_CODE);
        if (TextUtils.isEmpty(phoneCountryCode)) {
            phoneCountryCode = Safe.loadPhoneCountryCode();
        }
        if (TextUtils.isEmpty(phoneCountryCode)) {
            UserInfo userInfo = Authing.getCurrentUser();
            if (userInfo != null) {
                phoneCountryCode = userInfo.getPhoneCountryCode();
            }
        }
        return phoneCountryCode;
    }


    public static String getPassword(View current) {
        String password = null;
        View v = findViewByClass(current, PasswordEditText.class);
        if (v != null) {
            PasswordEditText editText = (PasswordEditText)v;
            password = editText.getText().toString();
        }
        if (TextUtils.isEmpty(password)) {
            password = Safe.loadPassword();
        }
        return password;
    }

    public static String getVerifyCode(View current) {
        View v = findViewByClass(current, VerifyCodeEditText.class);
        if (v != null) {
            VerifyCodeEditText editText = (VerifyCodeEditText)v;
            return editText.getText().toString();
        }
        return null;
    }

    public static void setErrorText(View view, String text) {
        view.post(()->{
            View v = Util.findViewByClass(view, ErrorTextView.class);
            if (v == null) {
                return;
            }
            ErrorTextView errorView = (ErrorTextView)v;
            errorView.setText(text);
            if (TextUtils.isEmpty(text)) {
                v.setVisibility(View.INVISIBLE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        });
    }

    public static Map<String, List<String>> splitQuery(URI url) throws UnsupportedEncodingException {
        final Map<String, List<String>> queryPairs = new LinkedHashMap<>();
        final String query = url.getQuery();
        if (TextUtils.isEmpty(query)) {
            return null;
        }

        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!queryPairs.containsKey(key)) {
                queryPairs.put(key, new LinkedList<>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            Objects.requireNonNull(queryPairs.get(key)).add(value);
        }
        return queryPairs;
    }

    public static String getAuthCode(String url) {
        return getQueryParam(url, "code");
    }

    public static String getQueryParam(String url, String key) {
        try {
            URI u = new URI(url);
            Map<String, List<String>> map = Util.splitQuery(u);
            if (map != null && map.containsKey(key)) {
                List<String> list = map.get(key);
                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme().resolveAttribute (R.attr.colorAccent, value, true);
        return value.data;
    }

    public static String randomString(int length) {
        String seed;
        Random rand = new Random();
        int seedLength;
        String asciiUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String asciiLowerCase = asciiUpperCase.toLowerCase();
        String digits = "1234567890";
        seed = asciiUpperCase + asciiLowerCase + digits;
        seedLength = seed.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < length;++i) {
            sb.append(seed.charAt(rand.nextInt(seedLength)));
        }
        return sb.toString();
    }

    public static boolean isNull(String s) {
        return TextUtils.isEmpty(s) || "null".equals(s);
    }

    public static List<Country> loadCountryList(Context context) {
        List<Country> countries = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.country);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    String[] data = line.split(",");
                    Country country = new Country(data[0], data[3], data[5], data[4], data[2], data[1]);
                    String firstSpell = isCn() ? data[5].substring(0, 1) : data[4].substring(0, 1);
                    country.setFirstSpell(firstSpell);
                    countries.add(country);
                }
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countries;
    }

    public static boolean isCn(){
        String lang = Locale.getDefault().getLanguage();
        return !Util.isNull(lang) && lang.contains("zh");
    }

    public static String getLangHeader() {
        String lang = Locale.getDefault().getLanguage();
        return (!Util.isNull(lang) && lang.contains("zh")) ? "zh-CN" : "en-US";
    }

    public static boolean isIp(String name) {
        if (name == null || name.length() == 0) {
            return true;
        }

        // ip v6
        if (name.contains(":")) {
            return true;
        }

        // ip v4
        String numRange = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\."
                + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\."
                + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])" + "\\."
                + "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";

        Pattern ip_pattern = Pattern.compile(numRange);
        Matcher match= ip_pattern.matcher(name);
        return match.matches();
    }

    public static String getHost(Config config) {
        if (isIp(Authing.getHost())) {
            return Authing.getHost();
        } else if (config != null) {
            String appHost = config.getIdentifier() + "." + Authing.getHost();
            String ssoHost = config.getRequestHostname();
            return Util.isNull(ssoHost) ? appHost : ssoHost;
        } else {
            ALog.e("Guard", "invalid host");
            return "core." + Authing.getHost();
        }
    }

    public static List<String> toStringList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add((array.getString(i)));
        }
        return list;
    }

    public static void setStatusBarColor(Activity activity, int colorResId){
        if (null == activity){
            return;
        }
        Window window = activity.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getColor(colorResId));
    }
}
