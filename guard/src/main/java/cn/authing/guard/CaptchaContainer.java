package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CaptchaContainer extends LinearLayout {

    public CaptchaContainer(Context context) {
        this(context, null);
    }

    public CaptchaContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CaptchaContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(HORIZONTAL);
    }
}
