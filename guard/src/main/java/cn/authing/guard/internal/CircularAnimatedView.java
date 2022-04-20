package cn.authing.guard.internal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.authing.guard.R;

public class CircularAnimatedView extends View {

    private final Bitmap background;
    private final Rect backgroundBound;
    private final Bitmap mask;
    private final Rect maskBound;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private float angle;

    public CircularAnimatedView(Context context) {
        this(context, null);
    }

    public CircularAnimatedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularAnimatedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.authing_loading);
        backgroundBound = new Rect(0, 0, background.getWidth(), background.getHeight());
        mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.authing_loading_mask);
        maskBound = new Rect(0, 0, mask.getWidth(), mask.getHeight());
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = getWidth();
        paint.setXfermode(null);
        canvas.drawBitmap(background, backgroundBound, new Rect(0, 0, getWidth(), getHeight()), paint);
        paint.setColor(0xff0080ff);
        canvas.drawArc(0, 0, size, size, angle, 90, true, paint);

        paint.setXfermode(mode);
        canvas.drawBitmap(mask, maskBound, new Rect(0, 0, getWidth(), getHeight()), paint);

        angle += 5;
        invalidate();
    }
}
