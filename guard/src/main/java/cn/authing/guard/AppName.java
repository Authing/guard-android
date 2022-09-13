package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import cn.authing.guard.analyze.Analyzer;

public class AppName extends AppCompatTextView {
    public AppName(@NonNull Context context) {
        this(context, null);
    }

    public AppName(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppName(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AppName");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(getResources().getColor(R.color.authing_app_name, null));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textStyle") == null) {
            setTypeface(Typeface.DEFAULT_BOLD);
        }

        Authing.getPublicConfig((config -> {
            if (config != null) {
                String s = getText().toString();
                if (TextUtils.isEmpty(s)) {
                    setText(config.getName());
                } else {
                    String formatText = String.format(s, config.getName());
                    setText(formatText);
                }
            }
        }));
    }
}
