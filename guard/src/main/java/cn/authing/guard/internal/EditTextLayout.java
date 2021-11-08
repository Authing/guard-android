package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class EditTextLayout extends LinearLayout implements TextWatcher {

    protected LinearLayout root;
    protected ImageView leftIcon;
    protected AppCompatEditText editText;
    protected ImageView clearAllButton;

    public EditTextLayout(@NonNull Context context) {
        this(context, null);
    }

    public EditTextLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(HORIZONTAL);

        root = this;
        root.setOrientation(HORIZONTAL);
        LayoutParams rootParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setLayoutParams(rootParam);
//        addView(root);

        if (GlobalStyle.isIsEditTextLayoutBackgroundSet()) {
            int background = GlobalStyle.getEditTextLayoutBackground();
            root.setBackgroundResource(background);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EditTextLayout);
        Drawable leftDrawable = array.getDrawable(R.styleable.EditTextLayout_leftIconDrawable);
        boolean clearAllEnabled = array.getBoolean(R.styleable.EditTextLayout_clearAllEnabled, true);
//        Drawable bgDrawable = array.getDrawable(R.styleable.EditTextLayout_background);
        float textSize = array.getDimension(R.styleable.EditTextLayout_textSize, Util.dp2px(context, 16));
        array.recycle();

        leftIcon = new ImageView(context);
        int length = (int) Util.dp2px(context, 24);
        LayoutParams iconParam = new LayoutParams(length, length);
        leftIcon.setLayoutParams(iconParam);
        int m = (int) Util.dp2px(context, 8);
        iconParam.setMargins(m, 0, 0, 0);
        leftIcon.setImageDrawable(leftDrawable);
        root.addView(leftIcon);
        if (leftDrawable == null) {
            leftIcon.setVisibility(View.GONE);
        }

        editText = new AppCompatEditText(context);
//        editText.setBackground(bgDrawable);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        editText.setOnFocusChangeListener((v, hasFocus)-> {
            root.setPressed(hasFocus);
        });
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        editText.setLayoutParams(lp);
        if (GlobalStyle.isIsEditTextBackgroundSet()) {
            int background = GlobalStyle.getEditTextBackground();
            editText.setBackgroundResource(background);
        }
        root.addView(editText);

        if (clearAllEnabled) {
            addClearAllButton();
        }
    }

    private void addClearAllButton() {
        Context context = getContext();
        LinearLayout clearAllTouchArea = new LinearLayout(context);
        clearAllTouchArea.setOrientation(HORIZONTAL);
        clearAllTouchArea.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        clearAllTouchArea.setLayoutParams(lp);

        Drawable clearDrawable = context.getDrawable(R.drawable.ic_authing_clear_all);
        int length = (int) Util.dp2px(context, 24);
        clearAllButton = new ImageView(context);
        LayoutParams iconParam = new LayoutParams(length, length);
        int p = (int) Util.dp2px(context, 6);
        iconParam.setMargins(p, 0, p, 0);
        clearAllButton.setLayoutParams(iconParam);
        clearAllButton.setVisibility(View.GONE);
        clearAllButton.setBackground(clearDrawable);
        clearAllTouchArea.setOnClickListener((v -> editText.setText("")));
        clearAllTouchArea.addView(clearAllButton);
        root.addView(clearAllTouchArea);

        editText.addTextChangedListener(this);
    }

    public EditText getEditText() {
        return editText;
    }

    public Editable getText() {
        return editText.getText();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (clearAllButton != null) {
            if (text.toString().length() > 0) {
                clearAllButton.setVisibility(View.VISIBLE);
            } else {
                clearAllButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
