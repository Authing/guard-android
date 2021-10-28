package cn.authing.guard.data;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;

import java.io.InputStream;

import cn.authing.guard.R;

public class ImageLoader extends AsyncTask<String, Void, Drawable> {
    private final Context context;

    private String url;
    private ImageView imageView;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public static ImageLoader with(Context context) {
        return new ImageLoader(context);
    }

    public ImageLoader load(String url) {
        this.url = url;
        return this;
    }

    public void into(ImageView imageView) {
        this.imageView=imageView;
        execute(url);
    }

    @Override
    protected Drawable doInBackground(String... urls) {
        try {
            String imageURL=urls[0];
            if (imageURL.endsWith(".svg")) {
                return AppCompatResources.getDrawable(context, R.drawable.ic_default_logo);
            } else {
                InputStream in = new java.net.URL(imageURL).openStream();
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(in));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Drawable result) {
        imageView.setImageDrawable(result);
    }
}
