package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.R;

public class MaskLabel extends TextView {

    protected int maskLength;

    public MaskLabel(Context context) {
        this(context, null);
    }

    public MaskLabel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaskLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaskLabel);
        maskLength = array.getInt(R.styleable.MaskLabel_maskLength,4);
        array.recycle();

        if (!TextUtils.isEmpty(getText())) {
            setTextWithMask(getText());
        }
    }

    public void setTextWithMask(CharSequence text) {
        String s;
        int length = text.length();
        if (length < maskLength) {
            s = asterisk(length);
        } else if (length < maskLength + 3) {
            s = text.subSequence(0, length-maskLength).toString() + asterisk(maskLength);
        } else {
            s = text.subSequence(0, 3).toString() + asterisk(maskLength) + text.subSequence(3+maskLength, length);
        }
        setText(s);
    }

    private String asterisk(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < count;++i) {
            sb.append("*");
        }
        return sb.toString();
    }
}
