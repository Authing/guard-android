package cn.authing.guard.internal;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;


public class GoFinishButton extends androidx.appcompat.widget.AppCompatButton {

    public GoFinishButton(@NonNull Context context) {
        this(context, null);
    }

    public GoFinishButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoFinishButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_white));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_confirm));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackground(getContext().getDrawable(R.drawable.authing_button_background));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minWidth") == null) {
            setMinWidth(0);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minHeight") == null) {
            setMinHeight(0);
        }

        setOnClickListener(v -> {
            ((Activity) getContext()).finish();
        });
    }

}
