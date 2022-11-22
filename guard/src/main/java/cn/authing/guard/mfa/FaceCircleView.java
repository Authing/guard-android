package cn.authing.guard.mfa;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.authing.guard.util.Util;


/**
 * 圆形透明遮挡层
 */
public class FaceCircleView extends View {

    private Paint mPaint;
    private float mRadius;
    private float marginTop;

    public FaceCircleView(Context context) {
        super(context);
        init(context);
    }

    public FaceCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FaceCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        // android.graphics.PorterDuff.Mode.CLEAR 显示挖空canvas为透明
        mPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));

        mRadius = Util.dp2px(context, 130);
        marginTop = Util.dp2px(context, 40) + (int) Util.dp2px(context, 8);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 255, 255);
        int mXCenter = getWidth() / 2;
        canvas.drawCircle(mXCenter, mRadius + marginTop, mRadius, mPaint);
    }

}
