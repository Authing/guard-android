package cn.authing.guard.data;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public interface ConfigCallback {
        void call(Config config);
    }

    private String userPoolId;
    private String identifier; // tenant domain
    private String name;
    private String logo;
    private String userpoolLogo;
    private List<String> enabledLoginMethods;
    private List<String> loginTabList;
    private String defaultLoginMethod;
    private List<String> registerTabList;
    private String defaultRegisterMethod;
    private int verifyCodeLength = 6;
    private List<SocialConfig> socialConfigs;
    private List<Agreement> agreements;
    private int passwordStrength;
    private List<String> completeFieldsPlace;
    private List<ExtendedField> extendedFields;

    public static Config parse(JSONObject data) throws JSONException {
        Config config = new Config();

        if (data.has("userPoolId"))
            config.setUserPoolId(data.getString("userPoolId"));
        if (data.has("identifier"))
            config.setIdentifier(data.getString("identifier"));
        if (data.has("name"))
            config.setName(data.getString("name"));
        if (data.has("logo"))
            config.setLogo(data.getString("logo"));
        if (data.has("userpoolLogo"))
            config.setUserpoolLogo(data.getString("userpoolLogo"));
        if (data.has("verifyCodeLength"))
            config.setVerifyCodeLength(data.getInt("verifyCodeLength"));
        if (data.has("passwordStrength"))
            config.setPasswordStrength(data.getInt("passwordStrength"));

        if (data.has("loginTabs")) {
            JSONObject loginTabs = data.getJSONObject("loginTabs");
            JSONArray loginTabList = loginTabs.getJSONArray("list");
            config.setLoginTabList(toStringList(loginTabList));
            config.setDefaultLoginMethod(loginTabs.getString("default"));
        }

        if (data.has("registerTabs")) {
            JSONObject registerTabs = data.getJSONObject("registerTabs");
            JSONArray registerTabList = registerTabs.getJSONArray("list");
            config.setRegisterTabList(toStringList(registerTabList));
            config.setDefaultRegisterMethod(registerTabs.getString("default"));
        }

        if (data.has("passwordTabConfig")) {
            JSONObject passwordTabConfig = data.getJSONObject("passwordTabConfig");
            JSONArray enabledLoginMethods = passwordTabConfig.getJSONArray("enabledLoginMethods");
            config.setEnabledLoginMethods(toStringList(enabledLoginMethods));
        }

        if (data.has("ecConnections")) {
            JSONArray socialConnections = data.getJSONArray("ecConnections");
            config.socialConfigs = toSocialList(socialConnections);
        }

        if (data.has("agreements")) {
            JSONArray agreements = data.getJSONArray("agreements");
            config.agreements = toAgreementList(agreements);
        }

        if (data.has("complateFiledsPlace")) {
            config.setCompleteFieldsPlace(toStringList(data.getJSONArray("complateFiledsPlace")));
        }

        if (data.has("extendsFields")) {
            config.setExtendedFields(toExtendedFields(data.getJSONArray("extendsFields")));
        }
        return config;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return (TextUtils.isEmpty(logo)) ? userpoolLogo : logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getUserpoolLogo() {
        return userpoolLogo;
    }

    public void setUserpoolLogo(String userpoolLogo) {
        this.userpoolLogo = userpoolLogo;
    }

    public List<String> getEnabledLoginMethods() {
        return enabledLoginMethods;
    }

    public void setEnabledLoginMethods(List<String> enabledLoginMethods) {
        this.enabledLoginMethods = enabledLoginMethods;
    }

    public List<String> getLoginTabList() {
        return loginTabList;
    }

    public void setLoginTabList(List<String> loginTabList) {
        this.loginTabList = loginTabList;
    }

    public String getDefaultLoginMethod() {
        return defaultLoginMethod;
    }

    public void setDefaultLoginMethod(String defaultLoginMethod) {
        this.defaultLoginMethod = defaultLoginMethod;
    }

    public List<String> getRegisterTabList() {
        return registerTabList;
    }

    public void setRegisterTabList(List<String> registerTabList) {
        this.registerTabList = registerTabList;
    }

    public String getDefaultRegisterMethod() {
        return defaultRegisterMethod;
    }

    public void setDefaultRegisterMethod(String defaultRegisterMethod) {
        this.defaultRegisterMethod = defaultRegisterMethod;
    }

    public int getVerifyCodeLength() {
        return verifyCodeLength;
    }

    public void setVerifyCodeLength(int verifyCodeLength) {
        this.verifyCodeLength = verifyCodeLength;
    }

    public List<SocialConfig> getSocialConfigs() {
        return socialConfigs;
    }

    public List<Agreement> getAgreements() {
        return agreements;
    }

    public int getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }

    public List<String> getCompleteFieldsPlace() {
        return completeFieldsPlace;
    }

    public void setCompleteFieldsPlace(List<String> completeFieldsPlace) {
        this.completeFieldsPlace = completeFieldsPlace;
    }

    public List<ExtendedField> getExtendedFields() {
        return extendedFields;
    }

    public void setExtendedFields(List<ExtendedField> extendedFields) {
        this.extendedFields = extendedFields;
    }

    public String getSocialConnectionId(String type) {
        String connId = "";
        List<SocialConfig> configs = getSocialConfigs();
        for (SocialConfig c : configs) {
            String provider = c.getType();
            if (type.equalsIgnoreCase(provider)) {
                connId = c.getId();
                break;
            }
        }
        return connId;
    }

    public String getSocialAppId(String type) {
        String appId = "";
        List<SocialConfig> configs = getSocialConfigs();
        for (SocialConfig c : configs) {
            String provider = c.getType();
            if (type.equalsIgnoreCase(provider)) {
                appId = c.getAppId();
                break;
            }
        }
        return appId;
    }

    private static List<String> toStringList(JSONArray array) throws JSONException {
        List<String> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add((array.getString(i)));
        }
        return list;
    }

    private static List<SocialConfig> toSocialList(JSONArray array) throws JSONException {
        List<SocialConfig> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            SocialConfig config = new SocialConfig();
            if (obj.has("id")) {
                String id = obj.getString("id");
                config.setId(id);
            }
            if (obj.has("provider")) {
                String provider = obj.getString("provider");
                config.setType(provider);
            }
            if (obj.has("type")) {
                String provider = obj.getString("type");
                config.setType(provider);
            }
            if (obj.has("fields")) {
                JSONObject fields = obj.getJSONObject("fields");
                if (fields.has("appId")) {
                    config.setAppId(fields.getString("appId"));
                }
            }
            list.add(config);
        }
        return list;
    }

    private static List<Agreement> toAgreementList(JSONArray array) throws JSONException {
        List<Agreement> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            String title = obj.getString("title");
            String lang = obj.getString("lang");
            boolean isRequired = obj.getBoolean("required");
            Agreement agreement = new Agreement();
            agreement.setTitle(title);
            agreement.setLang(lang);
            agreement.setRequired(isRequired);
            list.add(agreement);
        }
        return list;
    }

    private static List<ExtendedField> toExtendedFields(JSONArray array) throws JSONException {
        List<ExtendedField> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            ExtendedField extendedField = new ExtendedField();
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("type")) {
                extendedField.setType(obj.getString("type"));
            }
            if (obj.has("inputType")) {
                extendedField.setInputType(obj.getString("inputType"));
            }
            if (obj.has("name")) {
                extendedField.setName(obj.getString("name"));
            }
            if (obj.has("label")) {
                extendedField.setLabel(obj.getString("label"));
            }
            if (obj.has("required")) {
                extendedField.setRequired(obj.getBoolean("required"));
            }
            if (obj.has("validateRules")) {
                extendedField.setValidateRule(toValidateRules(obj.getJSONArray("validateRules")));
            }

            list.add(extendedField);
        }
        return list;
    }

    private static List<ExtendedField.ValidateRule> toValidateRules(JSONArray array) throws JSONException {
        List<ExtendedField.ValidateRule> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            ExtendedField.ValidateRule rule = new ExtendedField.ValidateRule();
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("type")) {
                rule.setType(obj.getString("type"));
            }
            if (obj.has("content")) {
                rule.setContent(obj.getString("content"));
            }
            if (obj.has("error")) {
                rule.setError(obj.getString("error"));
            }
            list.add(rule);
        }
        return list;
    }
}
