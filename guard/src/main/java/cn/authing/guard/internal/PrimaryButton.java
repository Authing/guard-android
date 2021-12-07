package cn.authing.guard.internal;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;

public class PrimaryButton extends LoadingButton {

    public PrimaryButton(@NonNull Context context) {
        this(context, null);
    }

    public PrimaryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public PrimaryButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(0xffffffff);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_button_background);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP);
        loading.setColorFilter(porterDuffColorFilter);
    }
}
