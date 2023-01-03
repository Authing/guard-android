package cn.authing.guard.internal;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class AccountTipsText extends AppCompatTextView {

    public AccountTipsText(@NonNull Context context) {
        this(context, null);
    }

    public AccountTipsText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountTipsText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_text_gray));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        post(() -> {
            UserInfo userInfo = Authing.getCurrentUser();
            if (null == userInfo) {
                return;
            }
            String text = (String) getText();
            setText(String.format(text, Util.getUserName(userInfo)));
        });
    }
}
