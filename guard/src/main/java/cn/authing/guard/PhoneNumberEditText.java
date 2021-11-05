package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.util.SpaceOnTheLeftSpan;
import cn.authing.guard.util.Util;

public class PhoneNumberEditText extends EditTextLayout implements TextWatcher {

    private final List<Integer> dividerPattern;
    private final float padding;

    public PhoneNumberEditText(@NonNull Context context) {
        this(context, null);
    }

    public PhoneNumberEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneNumberEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);

        CharSequence s = editText.getHint();
        if (s == null) {
            editText.setHint(context.getString(R.string.account_edit_text_hint) + context.getString(R.string.authing_phone));
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PhoneNumberEditText);
        int dp = array.getInt(R.styleable.PhoneNumberEditText_dividerPattern, 344);
        dividerPattern = Util.intDigits(dp);
        array.recycle();

        padding = Util.dp2px(context, 6);

        getEditText().addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
        if (dividerPattern == null || (dividerPattern.size() == 1 && dividerPattern.get(0) == 0)) {
            return;
        }

        Spannable spannableString = (Spannable)editText.getText();
        if (spannableString == null) {
            return;
        }

        int totalDividableLength = 0;
        SpaceOnTheLeftSpan[] spans = spannableString.getSpans(0, spannableString.length(), SpaceOnTheLeftSpan.class);
        for (SpaceOnTheLeftSpan span : spans) {
            spannableString.removeSpan(span);
        }

        for (int i = 0, len = spannableString.length();i < dividerPattern.size();++i) {
            int l = dividerPattern.get(i);
            totalDividableLength += l;

            if (totalDividableLength >= len) {
                break;
            }

            spannableString.setSpan(new SpaceOnTheLeftSpan(padding), totalDividableLength, totalDividableLength+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean isContentValid() {
        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        // TODO validate number with country code
//        if (text.length() != 11) {
//            return false;
//        }

        return true;
    }
}
