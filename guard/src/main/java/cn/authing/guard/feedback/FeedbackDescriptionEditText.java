package cn.authing.guard.feedback;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.Objects;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.util.Util;

public class FeedbackDescriptionEditText extends LinearLayout {

    private AppCompatEditText editText;
    private TextView textView;
    private int maxNumberOfWords;

    public FeedbackDescriptionEditText(Context context) {
        this(context, null);
    }

    public FeedbackDescriptionEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedbackDescriptionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);

        Analyzer.report("HelpDescriptionEditText");
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FeedbackDescriptionEditText);
        float textSize = array.getDimension(R.styleable.FeedbackDescriptionEditText_android_textSize, Util.sp2px(context, 16));
        int textColor = array.getColor(R.styleable.FeedbackDescriptionEditText_android_textColor, context.getColor(R.color.authing_text_black));
        int hintTextColor = array.getColor(R.styleable.FeedbackDescriptionEditText_hintTextColor, context.getColor(R.color.authing_text_gray));
        String hint = array.getString(R.styleable.FeedbackDescriptionEditText_android_hint);
        int lines = array.getInt(R.styleable.FeedbackDescriptionEditText_android_lines, 3);
        int maxLines = array.getInt(R.styleable.FeedbackDescriptionEditText_android_maxLines, 3);
        boolean singleLine = array.getBoolean(R.styleable.FeedbackDescriptionEditText_android_singleLine, false);
        maxNumberOfWords = array.getInt(R.styleable.FeedbackDescriptionEditText_maxNumberOfWords, 500);
        array.recycle();

        editText = new AppCompatEditText(context);
        LayoutParams editParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, 0, 1);
        editText.setBackground(null);
        editText.setGravity(Gravity.TOP);
        editText.setHint(hint);
        editText.setLines(lines);
        editText.setMaxLines(maxLines);
        editText.setSingleLine(singleLine);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        editText.setTextColor(textColor);
        editText.setHintTextColor(hintTextColor);
        editText.setPadding(0, 0, 0, 0);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.removeTextChangedListener(this);
                if (s.length() > maxNumberOfWords) {
                    editText.setText(s.toString().substring(0, maxNumberOfWords));
                    editText.setSelection(maxNumberOfWords);
                }
                String text = s.length() + "/" + maxNumberOfWords;
                textView.setText(text);
                editText.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText.setOnFocusChangeListener((v, hasFocus) -> setSelected(hasFocus));
        addView(editText, editParams);

        textView = new TextView(context);
        LayoutParams textParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(hintTextColor);
        textView.setGravity(Gravity.END);
        String text = "0/" + maxNumberOfWords;
        textView.setText(text);
        addView(textView, textParams);
    }

    public String getText() {
        return Objects.requireNonNull(editText.getText()).toString();
    }

}
