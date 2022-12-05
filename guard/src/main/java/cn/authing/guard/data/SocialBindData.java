package cn.authing.guard.data;

import static cn.authing.guard.util.Util.toStringList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SocialBindData implements Serializable {

    private List<String> tabMethods;
    private List<String> passwordMethods;
    private String key;
    private List<UserInfo> accountList;

    public static SocialBindData create(JSONObject data) {
        SocialBindData socialBindData = new SocialBindData();

        try {
            if (data.has("methods")) {
                JSONArray methods = data.getJSONArray("methods");
                List<String> methodList = toStringList(methods);
                List<String> tabMethodList = new ArrayList<>();
                List<String> passwordMethods = new ArrayList<>();
                for (String method : methodList) {
                    if ("email-password".equals(method)
                            || "username-password".equals(method)
                            || "phone-password".equals(method)) {
                        if (!tabMethodList.contains("password")) {
                            tabMethodList.add("password");
                        }
                        passwordMethods.add(method);
                    } else {
                        tabMethodList.add(method);
                    }
                }
                socialBindData.setTabMethods(tabMethodList);
                socialBindData.setPasswordMethods(passwordMethods);
            }

            if (data.has("accounts")) {
                JSONArray accounts = data.getJSONArray("accounts");
                List<UserInfo> accountList = new ArrayList<>();
                for (int i = 0; i < accounts.length(); i++) {
                    JSONObject account = accounts.getJSONObject(i);
                    UserInfo userInfo = UserInfo.createUserInfo(account);
                    accountList.add(userInfo);
                }
                socialBindData.setAccountList(accountList);
            }

            if (data.has("key")) {
                String key = data.getString("key");
                socialBindData.setKey(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return socialBindData;
    }

    public List<String> getTabMethods() {
        return tabMethods;
    }

    public void setTabMethods(List<String> tabMethods) {
        this.tabMethods = tabMethods;
    }

    public List<String> getPasswordMethods() {
        return passwordMethods;
    }

    public void setPasswordMethods(List<String> passwordMethods) {
        this.passwordMethods = passwordMethods;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<UserInfo> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<UserInfo> accountList) {
        this.accountList = accountList;
    }
}
