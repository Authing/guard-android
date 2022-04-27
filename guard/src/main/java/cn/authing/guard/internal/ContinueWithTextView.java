package cn.authing.guard.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;

public class ContinueWithTextView extends androidx.appcompat.widget.AppCompatTextView {
    public ContinueWithTextView(Context context) {
        this(context, null);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Authing.getPublicConfig(config -> {
            if (config == null || config.getSocialConfigs().size() == 0) {
                setVisibility(View.GONE);
            }
        });
    }
}
