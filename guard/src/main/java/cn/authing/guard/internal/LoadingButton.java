package cn.authing.guard.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class LoadingButton extends Button {

    protected AnimatedVectorDrawable loading;

    protected boolean showLoading;
    protected int loadingLocation; // 0 left; 1 over;

    public LoadingButton(@NonNull Context context) {
        this(context, null);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loading = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        loading.setVisible(false, true);
    }

    public void startLoadingVisualEffect() {
        setEnabled(false);
        showLoading = true;
        loading.setVisible(true, true);
        loading.start();

        Util.setErrorText(this, null);
    }

    public void stopLoadingVisualEffect() {
        post(()->{
            showLoading = false;
            loading.stop();
            loading.setVisible(false, true);
            setEnabled(true);
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (loadingLocation == 0) {
            drawLoading(canvas);
            super.onDraw(canvas);
        } else {
            super.onDraw(canvas);
            drawLoading(canvas);
        }
    }

    private void drawLoading(Canvas canvas) {
        if (!showLoading) {
            return;
        }

        int cw = getWidth();
        int ch = getHeight();
        int p = (int) Util.dp2px(getContext(), 4);
        int length = ch - 2 * p;
        loading.setBounds(0, 0, length, length);

        float x;
        float deltaX = 0;
        if (loadingLocation == 0) {
            float textWidth = getPaint().measureText(getText().toString());
            x = (cw - (length + p + textWidth)) / 2;
            float tx = x + length + p;
            float originalTextX = (cw - textWidth) / 2;
            deltaX = tx - originalTextX;
        } else {
            x = (cw - length) / 2;
        }

        canvas.save();
        canvas.translate(x, (ch - length) / 2);
        loading.draw(canvas);
        canvas.restore();

        if (loadingLocation == 0) {
            canvas.translate(deltaX, 0);
        }

        // continue onDraw while loading
        invalidate();
    }
}
