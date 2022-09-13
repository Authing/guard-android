package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;

public class MandatoryField extends TextView {

    // 0 none; 1. right; 2. left
    private int asteriskPosition;

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

        Analyzer.report("MandatoryField");

        String text = getText().toString();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MandatoryField);
        asteriskPosition = array.getInt(R.styleable.MandatoryField_asteriskPosition, 1);
        array.recycle();

        setMandatoryText(text);
    }

    public void setMandatoryText(CharSequence text) {
        Spannable span;
        if (asteriskPosition == 2) {
            span = new SpannableString("* " + text);
            span.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.authing_error)),
                    0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else if (asteriskPosition == 1) {
            span = new SpannableString(text + " *");
            int length = span.length();
            span.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.authing_error)),
                    length - 1, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            span = new SpannableString(text != null ? text : "");
        }
        setText(span);
    }

    public int getAsteriskPosition() {
        return asteriskPosition;
    }

    public void setAsteriskPosition(int asteriskPosition) {
        this.asteriskPosition = asteriskPosition;
    }
}
