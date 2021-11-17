package cn.authing.guard.analyze;

import android.provider.Settings;
import android.util.Log;

import cn.authing.guard.Authing;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SDKUsageTask implements Runnable {

    private final static String TAG = "SDKUsageTask";
    private static final String SDK_VERSION = "1.0.4";

    @Override
    public void run() {
        String ssaid = Settings.Secure.getString(Authing.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "https://developer-beta.authing.cn/stats/sdk-trace/?appid=" + Authing.getAppId()
                + "&sdk=android&version=" + SDK_VERSION
                + "&ip=" + ssaid;

        Request.Builder builder = new Request.Builder();
        builder.url(url);

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            if (response.code() != 200) {
                Log.e(TAG, "Trace failed:" + response.code());
            }
        } catch (Exception e){
            Log.e(TAG, "Trace exception:" + e.toString());
        }
    }
}
