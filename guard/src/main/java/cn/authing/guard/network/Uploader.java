package cn.authing.guard.network;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.data.Config;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class Uploader {

    private static final String TAG = "Uploader";

    public static void uploadImage(Activity activity, Uri uri, @NotNull Callback<String> callback) {
        InputStream in;
        try {
            in = activity.getContentResolver().openInputStream(uri);
            uploadImage(in, callback);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.call(false, "exception when uploading image");
        }
    }

    public static void uploadImage(InputStream in, @NotNull Callback<String> callback) {
        Authing.getPublicConfig(config -> new Thread() {
            public void run() {
                _uploadImage(config, in, callback);
            }
        }.start());
    }

    public static void uploadFaceImage(InputStream in, @NotNull Callback<String> callback) {
        Authing.getPublicConfig(config -> new Thread() {
            public void run() {
                _uploadFaceImage(config, in, callback);
            }
        }.start());
    }

    private static void _uploadImage(Config config, InputStream inputStream, @NotNull Callback<String> callback) {
        RequestBody requestBody = create(MediaType.parse("image/png"), inputStream);
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "aPhoto", requestBody)
                .build();

        String url = Authing.getScheme() + "://" + Util.getHost(config) + "/api/v2/upload?folder=photos";
        Request request = new Request.Builder().url(url).post(formBody).build();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            Log.i(TAG, "uploadFile result. " + response.code() + " message:" + s);
            if (response.code() == 200) {
                JSONObject json;
                try {
                    json = new JSONObject(s);
                    if (json.has("data")) {
                        JSONObject data = json.getJSONObject("data");
                        if (data.has("url")) {
                            String uploadedUrl = data.getString("url");
                            callback.call(true, uploadedUrl);
                        } else {
                            callback.call(false, s);
                        }
                    } else {
                        callback.call(false, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.call(false, s);
                }
            } else {
                callback.call(false, s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(false, "exception when uploading image");
        }
    }

    private static void _uploadFaceImage(Config config, InputStream inputStream, @NotNull Callback<String> callback) {
        RequestBody requestBody = create(MediaType.parse("image/png"), inputStream);
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "aPhoto", requestBody)
                .build();

        String url = Authing.getScheme() + "://" + Util.getHost(config) + "/api/v2/upload?folder=photos&private=" + true;
        Request request = new Request.Builder().url(url).post(formBody).build();
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Call call = client.newCall(request);
        okhttp3.Response response;
        try {
            response = call.execute();
            String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            Log.i(TAG, "uploadFile result. " + response.code() + " message:" + s);
            if (response.code() == 200) {
                JSONObject json;
                try {
                    json = new JSONObject(s);
                    if (json.has("data")) {
                        JSONObject data = json.getJSONObject("data");
                        if (data.has("key")) {
                            String uploadedUrl = data.getString("key");
                            callback.call(true, uploadedUrl);
                        } else {
                            callback.call(false, s);
                        }
                    } else {
                        callback.call(false, s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.call(false, s);
                }
            } else {
                callback.call(false, s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.call(false, "exception when uploading image");
        }
    }

    public static RequestBody create(final MediaType mediaType, final InputStream inputStream) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    return inputStream.available();
                } catch (IOException e) {
                    return 0;
                }
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                try (Source source = Okio.source(inputStream)) {
                    sink.writeAll(source);
                }
            }
        };
    }
}
