package cn.authing.guard.analyze;

import org.json.JSONObject;

import cn.authing.guard.Authing;
import cn.authing.guard.util.Const;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ComponentTask implements Runnable {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static String TAG = "ComponentTask";

    private final JSONObject body;

    ComponentTask(JSONObject body) {
        this.body = body;
    }

    @Override
    public void run() {
//        ALog.d(TAG, "reporting component:" + body);
        String url = "https://developer-beta.authing.cn/stats/component-trace/?appid=" + Authing.getAppId()
                + "&sdk=android&version=" + Const.SDK_VERSION;

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        RequestBody requestBody = RequestBody.create(body.toString(), JSON);
        builder.post(requestBody);

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            Analyzer.clearComponents();
        } catch (Exception e){
            e.printStackTrace();
            Analyzer.clearComponents();
        }
    }
}
