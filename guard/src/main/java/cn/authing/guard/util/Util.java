package cn.authing.guard.util;

import android.Manifest;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.igexin.sdk.PushManager;
import com.netease.nis.quicklogin.QuickLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Iterator;
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
import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.EmailEditText;
import cn.authing.guard.ErrorTextView;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.Country;
import cn.authing.guard.data.DeviceInfo;
import cn.authing.guard.data.Safe;
import cn.authing.guard.data.TabMethodsField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.device.DeviceUtils;
import cn.authing.guard.util.device.PhoneUtils;
import cn.authing.guard.webauthn.WebAuthNRegister;

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
        do {
            array.add(0, temp % 10);
            temp /= 10;
        } while (temp > 0);
        return array;
    }

    public static List<View> findAllViewByClass(View current, Class<?> T) {
        View view = current.getRootView();
        List<View> result = new ArrayList<>();
        _findAllViewByClass((ViewGroup) view, T, result);
        return result;
    }

    private static void _findAllViewByClass(ViewGroup parent, Class<?> T, List<View> result) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                _findAllViewByClass((ViewGroup) child, T, result);
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
        if (view instanceof ViewGroup) {
            return findChildViewByClass((ViewGroup) view, T, onlyVisible);
        }
        return view;
    }

    public static View findChildViewByClass(ViewGroup parent, Class<?> T, boolean onlyVisible) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup && (!onlyVisible || child.isShown())) {
                View result = findChildViewByClass((ViewGroup) child, T, onlyVisible);
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
        if (v instanceof AccountEditText) {
            AccountEditText editText = (AccountEditText) v;
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
        if (v instanceof PhoneNumberEditText) {
            PhoneNumberEditText editText = (PhoneNumberEditText) v;
            phone = editText.getText().toString();
        }
        if (TextUtils.isEmpty(phone)) {
            phone = (String) AuthFlow.get(current.getContext(), AuthFlow.KEY_MFA_PHONE);
        }
        if (TextUtils.isEmpty(phone)) {
            UserInfo userInfo = Authing.getCurrentUser();
            if (userInfo != null) {
                phone = userInfo.getPhone_number();
            }
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
        return phone;
    }

    public static String getPhoneCountryCode(View current) {
        String phoneCountryCode = null;
        View v = findViewByClass(current, CountryCodePicker.class);
        if (v instanceof CountryCodePicker) {
            phoneCountryCode = ((CountryCodePicker) v).getCountryCode();
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

    public static String getEmail(View current) {
        String email = "";
        View v = findViewByClass(current, EmailEditText.class);
        if (v instanceof EmailEditText) {
            EmailEditText editText = (EmailEditText) v;
            email = editText.getText().toString();
        }
        if (TextUtils.isEmpty(email)) {
            email = (String) AuthFlow.get(current.getContext(), AuthFlow.KEY_ACCOUNT);
        }
        if (TextUtils.isEmpty(email)) {
            email = (String) AuthFlow.get(current.getContext(), AuthFlow.KEY_MFA_EMAIL);
        }
        if (TextUtils.isEmpty(email)) {
            String account = AuthFlow.getAccount(current.getContext());
            if (Validator.isValidEmail(account)) {
                email = account;
            }
        }
        if (TextUtils.isEmpty(email)) {
            email = Safe.loadAccount();
        }
        return email;
    }


    public static String getPassword(View current) {
        String password = null;
        View v = findViewByClass(current, PasswordEditText.class);
        if (v instanceof PasswordEditText) {
            PasswordEditText editText = (PasswordEditText) v;
            password = editText.getText().toString();
        }
        if (TextUtils.isEmpty(password)) {
            password = Safe.loadPassword();
        }
        return password;
    }

    public static String getVerifyCode(View current) {
        View v = findViewByClass(current, VerifyCodeEditText.class);
        if (v instanceof VerifyCodeEditText) {
            VerifyCodeEditText editText = (VerifyCodeEditText) v;
            return editText.getText().toString();
        }
        return null;
    }

    public static void setErrorText(View view, String text) {
        view.post(() -> {
            View v = Util.findViewByClass(view, ErrorTextView.class);
            if (v instanceof ErrorTextView) {
                ErrorTextView errorView = (ErrorTextView) v;
                errorView.setText(text);
                if (TextUtils.isEmpty(text)) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
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

    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
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
        for (int i = 0; i < length; ++i) {
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

    public static boolean isCn() {
        String lang = Locale.getDefault().getLanguage();
        return !Util.isNull(lang) && lang.contains("zh");
    }

    public static boolean isEn() {
        String lang = Locale.getDefault().getLanguage();
        return !Util.isNull(lang) && lang.contains("en");
    }

    public static String getAppLanguage() {
        String country = Locale.getDefault().getCountry();
        if ("TW".equals(country)) {
            return "zh-TW";
        } else if ("US".equals(country)) {
            return "en-US";
        } else if ("JP".equals(country)) {
            return "ja-JP";
        }
        return "zh-CN";
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
        Matcher match = ip_pattern.matcher(name);
        return match.matches();
    }

    public static String getHost(Config config) {
        if (isIp(Authing.getHost())) {
            return Authing.getHost();
        } else if (config != null) {
            String appHost = Authing.getHost().contains(config.getIdentifier())
                    ? Authing.getHost() : config.getIdentifier() + "." + Authing.getHost();
            String ssoHost = config.getRequestHostname();
            return Util.isNull(ssoHost) ? appHost : ssoHost;
        } else {
            ALog.e("Guard", "invalid host");
            return "core." + Authing.getHost();
        }
    }

    public static String getIdentifierHost(Config config){
        if (config != null) {
            String identifier = config.getIdentifier();
            String ssoHost = config.getRequestHostname();
            if (Util.isNull(ssoHost)){
                return Authing.getHost().contains(identifier)
                        ? Authing.getHost() : identifier + "." + Authing.getHost();
            }
            if (ssoHost.contains(identifier)){
                return ssoHost;
            }
            int firstIndex = ssoHost.indexOf(".");
            return identifier + ssoHost.substring(firstIndex, ssoHost.length());
        }
        return Authing.getHost();
    }

    public static List<String> toStringList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        if (array != null){
            int size = array.length();
            for (int i = 0; i < size; i++) {
                list.add((array.getString(i)));
            }
        }
        return list;
    }

    public static void setStatusBarColor(Activity activity, int colorResId) {
        if (null == activity) {
            return;
        }
        Window window = activity.getWindow();
        UiModeManager uiModeManager = (UiModeManager) activity.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() != UiModeManager.MODE_NIGHT_YES) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getColor(colorResId));
    }

    public static void openSettingUI(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivity(intent);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static JSONObject pareUnderLine(JSONObject object) {
        if (object == null) {
            return object;
        }
        try {
            JSONObject parsedObject = new JSONObject();
            Iterator<String> sIterator = object.keys();
            while (sIterator.hasNext()) {
                String key = sIterator.next();
                String value = object.getString(key);
                String newKey = underlineToHump(key);
                parsedObject.put(newKey, value);
            }
            return parsedObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static String underlineToHump(String str) {
        //正则匹配下划线及后一个字符，删除下划线并将匹配的字符转成大写
        Matcher matcher = Pattern.compile("_([a-z])").matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配的子串替换成指定字符串，并且将替换后的子串及之前到上次匹配的子串之后的字符串添加到StringBuffer对象中
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            //把之后的字符串也添加到StringBuffer对象中
            matcher.appendTail(sb);
        } else {
            //去除除字母之外的前面带的下划线
            return sb.toString().replaceAll("_", "");
        }
        return underlineToHump(sb.toString());
    }

    public static boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }

    /**
     *  获取字段名称
     */
    public static String getLabel(Config config, String name){
        String label = "";
        List<TabMethodsField> tabMethodsFields = ( config != null ? config.getTabMethodsFields() : null);
        if (TextUtils.isEmpty(name) || tabMethodsFields == null || tabMethodsFields.isEmpty()){
            return label;
        }
        for (TabMethodsField tabMethodsField : tabMethodsFields){
            if (tabMethodsField == null
                    || TextUtils.isEmpty(tabMethodsField.getKey())
                    || !name.equals(tabMethodsField.getKey())){
                continue;
            }

            String language = Util.getAppLanguage();
            if (language.equals("en-US")) {
                label = tabMethodsField.getLabelEn();
            } else {
                label = tabMethodsField.getLabel();
            }
            JSONObject i18n = tabMethodsField.getI18n();
            if (i18n == null){
                break;
            }
            try {
                if (i18n.has(language)){
                    label = i18n.getString(language);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return label;
    }

    public static String getUserName(UserInfo userInfo){
        String userName = "-";
        if (userInfo == null){
            return userName;
        }
        if (!Util.isNull(userInfo.getUsername())){
            return userInfo.getUsername();
        }
        if (!Util.isNull(userInfo.getName())){
            return userInfo.getName();
        }
        if (!Util.isNull(userInfo.getNickname())){
            return userInfo.getNickname();
        }
        if (!Util.isNull(userInfo.getPhone_number())){
            return userInfo.getPhone_number();
        }
        if (!Util.isNull(userInfo.getEmail())){
            return userInfo.getEmail();
        }
        return userName;
    }


    /**
     * 登录成功逻辑统一处理
     */
    public static void loginSuccess(Activity activity, UserInfo userInfo){
        pushDeviceInfo(activity);
        Intent intent = new Intent();
        intent.putExtra("user", userInfo);
        activity.setResult(AuthActivity.OK, intent);
        activity.finish();
        quitActivity();
    }


    public static void pushDeviceInfo(Context context) {
        String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION};
        //验证是否许可权限
        boolean hasPermission = true;
        for (String str : permissions) {
            if (context.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
            }
        }

        if (!hasPermission) {
            return;
        }
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setUniqueId(DeviceUtils.getUniqueDeviceId(context));
        deviceInfo.setName(PhoneUtils.getDeviceName());
        deviceInfo.setVersion("Android " + PhoneUtils.getSDKVersionName());
        deviceInfo.setHks("");
        deviceInfo.setFde("");
        deviceInfo.setHor(DeviceUtils.isDeviceRooted());
        deviceInfo.setSn(PhoneUtils.getSerial());
        deviceInfo.setType("Mobile");
        deviceInfo.setProducer(PhoneUtils.getManufacturer());
        deviceInfo.setMod(PhoneUtils.getModel());
        deviceInfo.setOs("Android");
        deviceInfo.setImei(PhoneUtils.getIMEI(context));
        deviceInfo.setMeid(PhoneUtils.getMEID(context));
        deviceInfo.setDescription("");
        AuthClient.createDevice(deviceInfo, new AuthCallback<JSONObject>() {
            @Override
            public void call(int code, String message, JSONObject data) {
                Log.d("Util", "createDevice : code = " +code + " message = " + message);
            }
        });
    }

    public static void pushCid(Context context){
        String cid = PushManager.getInstance().getClientid(context);
        AuthClient.bindPushCid(cid, new AuthCallback<JSONObject>() {
            @Override
            public void call(int code, String message, JSONObject data) {
                if (code == 200){
                    Log.e("Util", "pushCid : code = " +code + " message = " + message);
                }
            }
        });
    }

    public static String encodeBase64URL(byte[] bytes){
        return Base64.encodeToString(bytes, Base64.URL_SAFE|Base64.NO_PADDING|Base64.NO_WRAP);
    }

    public static void biometricBind(AuthActivity activity){
        WebAuthNRegister webAuthNRegister = new WebAuthNRegister(activity, new WebAuthNRegister.WebAuthNRegisterCallBack() {
            @Override
            public void onSuccess() {
                AuthFlow flow = activity.getFlow();
                Intent intent = new Intent(activity, AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getBiometricAccountBindSuccessLayoutId());
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onFailed(int code, String message) {
                AuthClient.logout(new AuthCallback<Object>() {
                    @Override
                    public void call(int code, String message, Object data) {

                    }
                });
                if (!TextUtils.isEmpty(message) && (message.contains("CancelledException")
                        || message.contains("TimeoutException") || message.contains("UnknownException"))){
                    return;
                }
                activity.runOnUiThread(() -> ToastUtil.showCenter(activity, message));
            }
        });
        webAuthNRegister.startRegister();
    }

    public static void quitActivity(){
        try {
            QuickLogin.getInstance().quitActivity();
        } catch (NoClassDefFoundError e){
            Log.e("Util", "quitActivity : e = " + e);
        }
    }

}
