package cn.authing.push;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Push {

    private static final String TAG = "Push";

//    public static final String BASE_URL = "http://192.168.100.100:9005";
    public static final String BASE_URL = "https://developer-beta.authing.cn";

    public void registerDevice(Context context) {
        getHuaweiToken(context);
    }

    public void getHuaweiToken(Context context) {
        // 创建一个新线程
        new Thread() {
            @Override
            public void run() {
                try {
                    // 从agconnect-services.json文件中读取APP_ID
                    String appId = "106247535";

                    // 输入token标识"HCM"
                    String tokenScope = "HCM";
                    String token = HmsInstanceId.getInstance(context).getToken(appId, tokenScope);
                    ALog.i(TAG, "get token: " + token);

                    // 判断token是否为空
                    if(!TextUtils.isEmpty(token)) {
                        sendRegTokenToHMSServer(token);
                    }
                } catch (ApiException e) {
                    ALog.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }

    public static void sendRegTokenToHMSServer(String token) {
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo == null) {
            ALog.w(TAG, "push not registered. user not logged in");
            return;
        }

        Authing.getPublicConfig(config -> {
            if (config == null) {
                ALog.w(TAG, "push registered failed. uninitialized");
                return;
            }
            ALog.i(TAG, "sending token to server. token:" + token);
            Request.Builder builder = new Request.Builder();
            builder.url(BASE_URL + "/ams/push/register");
            builder.addHeader("authorization", userInfo.getIdToken());
            builder.addHeader("x-authing-app-id", Authing.getAppId());
            builder.addHeader("x-authing-userpool-id", config.getUserPoolId());
            String body = "{\"channel\":\"huawei\", \"token\":\"" + token + "\"}";
            builder.post(RequestBody.create(body, Const.JSON));

            Request request = builder.build();
            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            okhttp3.Response response;
            try {
                response = call.execute();
                if (response.code() == 201 || response.code() == 200) {
                    ALog.i(TAG, "register huawei token success");
                } else {
                    String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                    ALog.e(TAG, "register huawei token failed:" + s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ALog.e(TAG, "register huawei token failed:", e);
            }
        });
    }
}
