package cn.authing.guard.internal;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class LoadingButton extends CustomEventButton {

    protected final ImageView loadingView;

    public LoadingButton(@NonNull Context context) {
        this(context, null);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loadingView = new ImageView(context);
        loadingView.setId(R.id.loading_button_image_view);
        loadingView.setImageResource(R.drawable.ic_authing_animated_loading);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        loadingView.setLayoutParams(lp);
        loadingView.setVisibility(View.GONE);
        addView(loadingView);
    }

    public void startLoginVisualEffect() {
        setEnabled(false);
        loadingView.setVisibility(View.VISIBLE);
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable)loadingView.getDrawable();
        drawable.start();

        Util.setErrorText(this, null);
    }

    public void stopLoginVisualEffect() {
        post(()->{
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable)loadingView.getDrawable();
            drawable.stop();
            loadingView.setVisibility(View.GONE);
            setEnabled(true);
        });
    }
}
