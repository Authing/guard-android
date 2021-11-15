package cn.authing.guard.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class AuthClient {
    public static void loginByAccount(String account, String password, @NotNull AuthCallback callback) {
        Authing.getPublicConfig((config -> {
            try {
                String encryptPassword = Util.encryptPassword(password);
                JSONObject body = new JSONObject();
                body.put("account", account);
                body.put("password", encryptPassword);
                String url = "https://" + config.getIdentifier() + ".authing.cn/api/v2/login/account";
                Guardian.post(url, body, (data)->{
                    if (data.getCode() != 200) {
                        callback.call(data.getCode(), data.getMessage(), null);
                        return;
                    }

                    UserInfo userInfo;
                    try {
                        userInfo = UserInfo.createUserInfo(data.getData());
                        callback.call(data.getCode(), data.getMessage(), userInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.call(500, "Cannot parse data into UserInfo", null);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception loginByAccount", null);
            }
        }));
    }
}
