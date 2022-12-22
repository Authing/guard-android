package cn.authing.guard.data;

import static cn.authing.guard.util.Util.toStringList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SocialBindData implements Serializable {

    private List<String> methods;
    private String key;
    private List<UserInfo> accounts;

    public static SocialBindData create(JSONObject data) {
        SocialBindData socialBindData = new SocialBindData();

        try {
            if (data.has("methods")) {
                JSONArray methods = data.getJSONArray("methods");
                List<String> methodList = toStringList(methods);
                socialBindData.setMethods(methodList);
            }

            if (data.has("accounts")) {
                JSONArray accounts = data.getJSONArray("accounts");
                List<UserInfo> accountList = new ArrayList<>();
                for (int i = 0; i < accounts.length(); i++) {
                    JSONObject account = accounts.getJSONObject(i);
                    UserInfo userInfo = UserInfo.createUserInfo(account);
                    accountList.add(userInfo);
                }
                socialBindData.setAccounts(accountList);
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

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<UserInfo> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<UserInfo> accounts) {
        this.accounts = accounts;
    }
}
