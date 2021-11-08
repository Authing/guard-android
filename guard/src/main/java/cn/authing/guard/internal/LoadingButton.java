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

    public LoadingButton(@NonNull Context context) {
        this(context, null);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loading = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading);
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
        super.onDraw(canvas);

        if (showLoading) {
            int ch = getHeight();
            int p = (int) Util.dp2px(getContext(), 4);
            int length = ch - 2 * p;
            loading.setBounds(0, 0, length, length);
            canvas.save();
            canvas.translate((getWidth() - length) / 2, (ch - length) / 2);
            loading.draw(canvas);
            canvas.restore();

            // continue onDraw while loading
            invalidate();
        }
    }
}
