package cn.authing.guard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class RoundImageView extends AppCompatImageView {

    private final Paint paint;

    public RoundImageView(@NonNull Context context) {
        this(context, null);
    }

    public RoundImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null || getWidth() == 0 || getHeight() == 0) {
            super.onDraw(canvas);
            return;
        }
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null){
                super.onDraw(canvas);
                return;
            }
            int radius = Math.min(bitmap.getWidth(), bitmap.getHeight());
            Bitmap b = getCircleBitmap(bitmap, radius);
            Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            Rect rectDest = new Rect(0,0, getWidth(), getHeight());
            paint.reset();
            paint.setAntiAlias(true);
            canvas.drawBitmap(b, rectSrc, rectDest, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap, int radius) {
        Bitmap swapBitmap;
        if(bitmap.getWidth() != radius || bitmap.getHeight() != radius){
            swapBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        } else{
            swapBitmap = bitmap;
        }

        Bitmap output = Bitmap.createBitmap(swapBitmap.getWidth(),
                swapBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, swapBitmap.getWidth(), swapBitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle((float) swapBitmap.getWidth() / 2,
                (float) swapBitmap.getHeight() / 2,
                (float) swapBitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(swapBitmap, rect, rect, paint);
        return output;
    }

}
