package cn.authing.guard.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.authing.guard.R;

public class ImageUtil {

    public static final int THUMBNAIL_SIZE = 128;
    public static final int SCREENSHOT_SIZE = 1280;

    public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
        return getScaledImage(context, uri, THUMBNAIL_SIZE);
    }

    public static Bitmap getScreenshot(Context context, Uri uri) throws IOException {
        return getScaledImage(context, uri, SCREENSHOT_SIZE);
    }

    public static Bitmap getScaledImage(Context context, Uri uri, int maxSize) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = Math.max(onlyBoundsOptions.outHeight, onlyBoundsOptions.outWidth);

        double ratio = (originalSize > maxSize) ? (originalSize / maxSize) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        return k == 0 ? 1 : k;
    }


    /**
     * base 64 图片转码
     */
    public static Bitmap stringToBitmap(String url) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(url.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存图片到相册的方法
     */
    public static void saveImage(Context context, Bitmap toBitmap) {
        if (Build.VERSION.SDK_INT <= 29) {
            String insertImage = MediaStore.Images.Media.insertImage(context.getContentResolver(), toBitmap, "otp", "otp_qr_code");
            if (TextUtils.isEmpty(insertImage)) {
                ToastUtil.showCenter(context, context.getString(R.string.authing_bind_otp_save_qr_code_success), R.drawable.ic_authing_fail);
            } else {
                ToastUtil.showCenter(context, context.getString(R.string.authing_bind_otp_save_qr_code_success), R.drawable.ic_authing_success);
            }
        } else {
            //开始一个新的进程执行保存图片的操作
            Uri insertUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(insertUri, "rw");
                if (toBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
                    ToastUtil.showCenter(context, context.getString(R.string.authing_bind_otp_save_qr_code_success), R.drawable.ic_authing_success);
                } else {
                    ToastUtil.showCenter(context, context.getString(R.string.authing_bind_otp_save_qr_code_success), R.drawable.ic_authing_fail);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                ALog.e("ImageUtils", e.toString());
            }
        }

    }


}
