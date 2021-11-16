package cn.authing.guard;

import static cn.authing.guard.util.Util.getThemeAccentColor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MandatoryField extends TextView {
    public MandatoryField(Context context) {
        this(context, null);
    }

    public MandatoryField(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MandatoryField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MandatoryField(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        String text = getText().toString();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MandatoryField);
        int pos = array.getInt(R.styleable.MandatoryField_asteriskPosition, 1);
        array.recycle();

        Spannable span;
        if (pos == 0) {
            span = new SpannableString("* " + text);
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#ff831827")), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            span = new SpannableString(text + " *");
            int length = span.length();
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#ff831827")), length - 1, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        setText(span);
    }
}
