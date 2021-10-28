package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import cn.authing.guard.data.Config;

public class AppName extends AppCompatTextView {
    public AppName(@NonNull Context context) {
        super(context);
        init(null);
    }

    public AppName(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AppName(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        Config config = Authing.getPublicConfig();
        if (config != null) {
            CharSequence s = getText();
            if (s == null || s.length() == 0) {
                setText(config.getName());
            }
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(0xFFABB9D7);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }
    }
}
