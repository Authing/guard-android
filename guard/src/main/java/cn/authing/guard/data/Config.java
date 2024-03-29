package cn.authing.guard.data;

import static cn.authing.guard.util.Util.toStringList;

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
    private String requestHostname;
    private String name;
    private String logo;
    private String userpoolLogo;
    private List<String> enabledLoginMethods;
    private List<String> loginTabList;
    private String defaultLoginMethod;
    private List<String> registerTabList;
    private String defaultRegisterMethod;
    private boolean registerDisabled;
    private boolean autoRegisterThenLoginHintInfo;
    private List<String> passwordTabValidRegisterMethods;
    private List<String> verifyCodeTabValidRegisterMethods;
    private List<TabMethodsField> tabMethodsFields;
    private int verifyCodeLength = 6;
    private List<SocialConfig> socialConfigs;
    private List<Agreement> agreements;
    private int passwordStrength;
    private List<String> completeFieldsPlace;
    private List<ExtendedField> extendedFields;
    private JSONObject extendedFieldsI18n;
    private List<String> redirectUris = new ArrayList<>();
    private boolean internationalSmsEnable;
    private String userAgent;
    private List<RegexRules> regexRules;

    public static Config parse(JSONObject data) throws JSONException {
        Config config = new Config();

        if (data.has("userPoolId"))
            config.setUserPoolId(data.getString("userPoolId"));
        if (data.has("identifier"))
            config.setIdentifier(data.getString("identifier"));
        if (data.has("requestHostname"))
            config.setRequestHostname(data.getString("requestHostname"));
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
            List<String> loginTab = toStringList(loginTabList);
            if (loginTab.contains("phone-code")){
                if (data.has("verifyCodeTabConfig")) {
                    JSONObject verifyCodeTabConfig = data.getJSONObject("verifyCodeTabConfig");
                    JSONArray validLoginMethods = new JSONArray();
                    if (verifyCodeTabConfig.has("validLoginMethods")){
                        validLoginMethods = verifyCodeTabConfig.getJSONArray("validLoginMethods");
                    }else{
                        if (verifyCodeTabConfig.has("enabledLoginMethods")){
                            validLoginMethods = verifyCodeTabConfig.getJSONArray("enabledLoginMethods");
                        }
                    }
                    List<String> validLoginMethodLis = toStringList(validLoginMethods);
                    if (!validLoginMethodLis.isEmpty()){
                        int index = loginTab.indexOf("phone-code");
                        loginTab.remove("phone-code");
                        loginTab.addAll(index, validLoginMethodLis);
                    }
                    if (verifyCodeTabConfig.has("validRegisterMethods")){
                        JSONArray validRegisterMethods = verifyCodeTabConfig.getJSONArray("validRegisterMethods");
                        config.setVerifyCodeTabValidRegisterMethods(toStringList(validRegisterMethods));
                    }
                }
            }
            config.setLoginTabList(loginTab);
            config.setDefaultLoginMethod(loginTabs.getString("default"));
        }

        if (data.has("registerDisabled")){
            config.setRegisterDisabled(data.getBoolean("registerDisabled"));
        }

        if (data.has("registerTabs")) {
            JSONObject registerTabs = data.getJSONObject("registerTabs");
            JSONArray registerTabList = registerTabs.getJSONArray("list");
            config.setRegisterTabList(toStringList(registerTabList));
            config.setDefaultRegisterMethod(registerTabs.getString("default"));
        }

        if (data.has("passwordTabConfig")) {
            JSONObject passwordTabConfig = data.getJSONObject("passwordTabConfig");
            if (passwordTabConfig.has("validLoginMethods")){
                JSONArray validLoginMethods = passwordTabConfig.getJSONArray("validLoginMethods");
                config.setEnabledLoginMethods(toStringList(validLoginMethods));
            } else {
                if (passwordTabConfig.has("enabledLoginMethods")){
                    JSONArray enabledLoginMethods = passwordTabConfig.getJSONArray("enabledLoginMethods");
                    config.setEnabledLoginMethods(toStringList(enabledLoginMethods));
                }
            }
            if (passwordTabConfig.has("validRegisterMethods")){
                JSONArray validRegisterMethods = passwordTabConfig.getJSONArray("validRegisterMethods");
                config.setPasswordTabValidRegisterMethods(toStringList(validRegisterMethods));
            }
        }

        if (data.has("tabMethodsFields")) {
            config.setTabMethodsFields(toTabMethodFieldsList(data.getJSONArray("tabMethodsFields")));
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

        if (data.has("extendsFieldsI18n")) {
            config.setExtendedFieldsI18n(data.getJSONObject("extendsFieldsI18n"));
        }

        if (data.has("redirectUris")) {
            config.setRedirectUris(toStringList(data.getJSONArray("redirectUris")));
        }

        if (data.has("internationalSmsConfig") && !data.isNull("internationalSmsConfig")) {
            JSONObject internationalSmsConfig = data.getJSONObject("internationalSmsConfig");
            if (internationalSmsConfig.has("enabled")){
                config.setInternationalSmsEnable(internationalSmsConfig.getBoolean("enabled"));
            }
        }

        if (data.has("ssoPageComponentDisplay") && !data.isNull("ssoPageComponentDisplay")) {
            JSONObject internationalSmsConfig = data.getJSONObject("ssoPageComponentDisplay");
            if (internationalSmsConfig.has("autoRegisterThenLoginHintInfo")){
                config.setAutoRegisterThenLoginHintInfo(internationalSmsConfig.getBoolean("autoRegisterThenLoginHintInfo"));
            }
        }

        if (data.has("regexRules")){
            JSONArray regexRules = data.getJSONArray("regexRules");
            config.setRegexRules(toRegexRules(regexRules));

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

    public String getRequestHostname() {
        return requestHostname;
    }

    public void setRequestHostname(String requestHostname) {
        this.requestHostname = requestHostname;
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

    private static List<RegexRules> toRegexRules(JSONArray array) throws JSONException {
        List<RegexRules> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            RegexRules regexRules = new RegexRules();
            JSONObject obj = array.getJSONObject(i);
            if (obj.has("key")) {
                regexRules.setKey(obj.getString("key"));
            }
            if (obj.has("appLevel")) {
                regexRules.setAppLevel(obj.getString("appLevel"));
            }
            if (obj.has("userpoolLevel")) {
                regexRules.setUserPoolLevel(obj.getString("userpoolLevel"));
            }
            list.add(regexRules);
        }
        return list;
    }

    public List<RegexRules> getRegexRules() {
        return regexRules;
    }

    private static List<TabMethodsField> toTabMethodFieldsList(JSONArray array) throws JSONException {
        List<TabMethodsField> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            TabMethodsField tabMethodsField = new TabMethodsField();
            JSONObject obj = array.getJSONObject(i);

            String key = obj.getString("key");
            tabMethodsField.setKey(key);
            String label = obj.getString("label");
            tabMethodsField.setLabel(label);
            String labelEn = obj.getString("labelEn");
            tabMethodsField.setLabelEn(labelEn);
            JSONObject i18n = obj.getJSONObject("i18n");
            tabMethodsField.setI18n(i18n);

            list.add(tabMethodsField);
        }
        return list;
    }

    public boolean isRegisterDisabled() {
        return registerDisabled;
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

    public JSONObject getExtendedFieldsI18n() {
        return extendedFieldsI18n;
    }

    public void setExtendedFieldsI18n(JSONObject extendedFieldsI18n) {
        this.extendedFieldsI18n = extendedFieldsI18n;
    }

    public List<String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public boolean isInternationalSmsEnable() {
        return internationalSmsEnable;
    }

    public void setInternationalSmsEnable(boolean internationalSmsEnable) {
        this.internationalSmsEnable = internationalSmsEnable;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isAutoRegisterThenLoginHintInfo() {
        return autoRegisterThenLoginHintInfo;
    }

    public void setAutoRegisterThenLoginHintInfo(boolean autoRegisterThenLoginHintInfo) {
        this.autoRegisterThenLoginHintInfo = autoRegisterThenLoginHintInfo;
    }

    public void setRegisterDisabled(boolean registerDisabled) {
        this.registerDisabled = registerDisabled;
    }

    public List<String> getPasswordTabValidRegisterMethods() {
        return passwordTabValidRegisterMethods;
    }

    public void setPasswordTabValidRegisterMethods(List<String> passwordTabValidRegisterMethods) {
        this.passwordTabValidRegisterMethods = passwordTabValidRegisterMethods;
    }

    public List<String> getVerifyCodeTabValidRegisterMethods() {
        return verifyCodeTabValidRegisterMethods;
    }

    public void setVerifyCodeTabValidRegisterMethods(List<String> verifyCodeTabValidRegisterMethods) {
        this.verifyCodeTabValidRegisterMethods = verifyCodeTabValidRegisterMethods;
    }

    public List<TabMethodsField> getTabMethodsFields() {
        return tabMethodsFields;
    }

    public String getSocialConnectionId(String type) {
        return getSocialValue(type, "connectionId");
    }

    public String getSocialAppId(String type) {
        return getSocialValue(type, "appId");
    }

    public String getSocialAgentId(String type) {
        return getSocialValue(type, "agentId");
    }

    public String getSocialSchema(String type) {
        return getSocialValue(type, "schema");
    }

    public String getSocialBusinessId(String type) {
        return getSocialValue(type, "businessId");
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
                if (fields.has("corpId")) {
                    config.setAppId(fields.getString("corpId"));
                }
                if (fields.has("agentId")) {
                    config.setAgentId(fields.getString("agentId"));
                }
                if (fields.has("schema")) {
                    config.setSchema(fields.getString("schema"));
                }
                if (fields.has("businessId")) {
                    config.setBusinessId(fields.getString("businessId"));
                }
                if (fields.has("clientID")) {
                    config.setClientId(fields.getString("clientID"));
                }
                if (fields.has("mobileAppID")) {
                    config.setMobileAppID(fields.getString("mobileAppID"));
                }
                if (fields.has("originalID")) {
                    config.setOriginalID(fields.getString("originalID"));
                }
                if (fields.has("appKey")) {
                    config.setAppKey(fields.getString("appKey"));
                }
                if (fields.has("redirectURI")) {
                    config.setRedirectUrl(fields.getString("redirectURI"));
                }
            }
            list.add(config);
        }
        return list;
    }

    public String getSocialClientId(String type) {
        return getSocialValue(type, "clientId");
    }

    public String getSocialMobileAppID(String type) {
        return getSocialValue(type, "mobileAppID");
    }

    public String getSocialOriginalID(String type) {
        return getSocialValue(type, "originalID");
    }

    public String getSocialAppKey(String type) {
        return getSocialValue(type, "appKey");
    }

    public String getSocialRedirectUrl(String type) {
        return getSocialValue(type, "redirectURI");
    }

    public String getSocialValue(String type, String fieldName) {
        String value = "";
        List<SocialConfig> configs = getSocialConfigs();
        for (SocialConfig c : configs) {
            String provider = c.getType();
            if (type.equalsIgnoreCase(provider)) {
                switch (fieldName){
                    case "connectionId":
                        value = c.getId();
                        break;
                    case "appId":
                        value = c.getAppId();
                        break;
                    case "agentId":
                        value = c.getAgentId();
                        break;
                    case "schema":
                        value = c.getSchema();
                        break;
                    case "businessId":
                        value = c.getBusinessId();
                        break;
                    case "clientId":
                        value = c.getClientId();
                        break;
                    case "mobileAppID":
                        value = c.getMobileAppID();
                        break;
                    case "originalID":
                        value = c.getOriginalID();
                        break;
                    case "appKey":
                        value = c.getAppKey();
                        break;
                    case "redirectURI":
                        value = c.getRedirectUrl();
                        break;
                }
                break;
            }
        }
        return value;
    }

    private static List<Agreement> toAgreementList(JSONArray array) throws JSONException {
        List<Agreement> list = new ArrayList<>();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            Agreement agreement = new Agreement();
            JSONObject obj = array.getJSONObject(i);
            String title = obj.getString("title");
            agreement.setTitle(title);

            String lang = obj.getString("lang");
            agreement.setLang(lang);

            boolean isRequired = obj.getBoolean("required");
            agreement.setRequired(isRequired);

            try {
                int availableAt = obj.getInt("availableAt");
                agreement.setAvailableAt(availableAt);
            } catch (JSONException e) {
                // for historical reason availableAt can be null
                e.printStackTrace();
            }
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

    public void setRegexRules(List<RegexRules> regexRules) {
        this.regexRules = regexRules;
    }

    public void setTabMethodsFields(List<TabMethodsField> tabMethodsFields) {
        this.tabMethodsFields = tabMethodsFields;
    }
}
