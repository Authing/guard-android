package cn.authing.guard.data;

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
    private String identifier; // host
    private String name;
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

    public static Config parse(JSONObject data) throws JSONException {
        Config config = new Config();

        config.setUserPoolId(data.getString("userPoolId"));
        config.setIdentifier(data.getString("identifier"));
        config.setName(data.getString("name"));
        config.setUserpoolLogo(data.getString("userpoolLogo"));
        config.setVerifyCodeLength(data.getInt("verifyCodeLength"));
        config.setPasswordStrength(data.getInt("passwordStrength"));

        JSONObject loginTabs = data.getJSONObject("loginTabs");
        JSONArray loginTabList = loginTabs.getJSONArray("list");
        config.setLoginTabList(toStringList(loginTabList));
        config.setDefaultLoginMethod(loginTabs.getString("default"));

        JSONObject registerTabs = data.getJSONObject("registerTabs");
        JSONArray registerTabList = registerTabs.getJSONArray("list");
        config.setRegisterTabList(toStringList(registerTabList));
        config.setDefaultRegisterMethod(registerTabs.getString("default"));

        JSONObject passwordTabConfig = data.getJSONObject("passwordTabConfig");
        JSONArray enabledLoginMethods = passwordTabConfig.getJSONArray("enabledLoginMethods");
        config.setEnabledLoginMethods(toStringList(enabledLoginMethods));

        JSONArray socialConnections = data.getJSONArray("socialConnections");
        config.socialConfigs = toSocialList(socialConnections);

        JSONArray agreements = data.getJSONArray("agreements");
        config.agreements = toAgreementList(agreements);
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
            String id = obj.getString("id");
            String provider = obj.getString("provider");
            SocialConfig config = new SocialConfig();
            config.setId(id);
            config.setProvider(provider);
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
}
