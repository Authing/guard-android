package cn.authing.guard.network;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Uploader {

    private static final String TAG = "Uploader";

    public static void uploadImage(File file, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> _uploadImage(config, file, callback));
    }

    public static void _uploadImage(Config config, File file, @NotNull AuthCallback<UserInfo> callback) {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .build();
        String url = Authing.getSchema() + "://" + Util.getHost(config) + "/api/v2/upload?folder=photos";
        Request request = new Request.Builder().url(url).post(formBody).build();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            Log.i(TAG, "uploadFile result. " + response.code() + " message:" + s);
            callback.call(response.code(), s,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
