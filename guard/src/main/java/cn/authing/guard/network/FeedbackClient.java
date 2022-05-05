package cn.authing.guard.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.util.Util;

public class FeedbackClient {
    public static void feedback(String contact, int type, String description, List<String> images, @NotNull Callback<String> callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("appId", Authing.getAppId());
            body.put("phone", contact);
            body.put("type", type);
            body.put("description", description);
            if (images != null && images.size() > 0) {
                body.put("images", new JSONArray(images));
            }
            Guardian.post("/api/v2/feedback", body, (data)-> {
                if (data.getCode() == 200) {
                    callback.call(true, null);
                } else {
                    callback.call(false, data.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(false, "Feedback exception");
        }
    }
}
