package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import cn.authing.guard.R;


public class CircleImageView extends AppCompatImageView {

    private int mRadius;
    private Path mPath;
    private RectF mRectF;
    private ViewOutlineProvider viewOutlineProvider;

    public CircleImageView(@NonNull Context context) {
        this(context, null);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView);
        mRadius = (int) typedArray.getDimension(R.styleable.CircleImageView_circleImageRadius, 0f);
        typedArray.recycle();
        setRadius(mRadius);
    }

    public void setRadius(int radius) {
        boolean isChange = radius != mRadius;
        mRadius = radius;
        if (mPath == null) {
            mPath = new Path();
        }
        if (mRectF == null) {
            mRectF = new RectF();
        }
        if (mRadius != 0) {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                if (viewOutlineProvider == null) {
                    viewOutlineProvider = new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            int width = getWidth();
                            int height = getHeight();
                            outline.setRoundRect(0, 0, width, height, mRadius);
                        }
                    };
                }
                setOutlineProvider(viewOutlineProvider);
                setClipToOutline(true);
            }
            mRectF.set(0, 0, getWidth(), getHeight());
            mPath.reset();
            mPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
        } else {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                setClipToOutline(false);
            }
        }

        if (isChange) {
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                invalidateOutline();
            }
        }

    }

    @Override
    public void draw(Canvas canvas) {
        boolean clip = false;
        if (Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT && mRadius > 0) {
            clip = true;
            canvas.save();
            canvas.clipPath(mPath);
        }
        super.draw(canvas);
        if (clip) {
            canvas.restore();
        }
    }
}
