package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class AppName extends AppCompatTextView {
    public AppName(@NonNull Context context) {
        this(context, null);
    }

    public AppName(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppName(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Authing.getPublicConfig((config -> {
            if (config != null) {
                CharSequence s = getText();
                if (s == null || s.length() == 0) {
                    String pre = "";
                    TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AppName);
                    int t = array.getInt(R.styleable.AppName_pageType,0);
                    if (t == 0) {
                        pre = getResources().getString(R.string.authing_login);
                    } else if (t == 1) {
                        pre = getResources().getString(R.string.authing_welcome_to);
                    }
                    array.recycle();
                    setText(pre + " " + config.getName());
                }
            }

            if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
                setTextColor(getResources().getColor(R.color.authing_app_name, null));
            }

            if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            }

            if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textStyle") == null) {
                setTypeface(Typeface.DEFAULT_BOLD);
            }
        }));
    }
}
