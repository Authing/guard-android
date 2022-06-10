package cn.authing.guard.analyze;

import android.provider.Settings;

import cn.authing.guard.Authing;
import cn.authing.guard.util.Const;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SDKUsageTask implements Runnable {

    @Override
    public void run() {
        String ssaid = Settings.Secure.getString(Authing.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "https://developer-beta.authing.cn/stats/sdk-trace/?appid=" + Authing.getAppId()
                + "&sdk=android&version=" + Const.SDK_VERSION
                + "&host=" + Authing.getHost()
                + "&ip=" + ssaid;

        Request.Builder builder = new Request.Builder();
        builder.url(url);

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        try {
            call.execute();
        } catch (Exception ignored) {
        }
    }
}
