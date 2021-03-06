package cn.authing.guard.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.authing.guard.Authing;
import cn.authing.guard.Callback;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class ImageLoader {
    private final Context context;

    private String url;
    private ImageView imageView;

    private ImageLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ImageLoader with(Context context) {
        return new ImageLoader(context);
    }

    public ImageLoader load(String url) {
        this.url = url;
        return this;
    }

    public void into(ImageView imageView) {
        if (imageView == null || Util.isNull(url)) {
            return;
        }

        this.imageView = imageView;

        execute(url, null);
    }

    public void execute(String url, Callback<Drawable> callback) {
        new Thread(() -> {
            try {
                Drawable drawable = _execute(url);
                if (callback != null) {
                    callback.call(true, drawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.call(false, null);
                }
            }
        }).start();
    }

    private Drawable _execute(String url) throws IOException {
        if (url.endsWith(".svg")) {
            Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.ic_authing_default_logo);
            updateImage(drawable);
            return drawable;
        }

        String fileName = "" + Authing.getAppId();
        File file = new File(context.getExternalCacheDir(), fileName);
        if (file.exists()) {
            Drawable drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(new FileInputStream(file)));
            updateImage(drawable);
        }

        InputStream in = new java.net.URL(url).openStream();
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(in));
        updateImage(drawable);

        Bitmap bitmap = drawable.getBitmap();
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.close();

        return drawable;
    }

    private void updateImage(Drawable drawable) {
        if (imageView != null) {
            imageView.post(() -> imageView.setImageDrawable(drawable));
        }
    }
}
