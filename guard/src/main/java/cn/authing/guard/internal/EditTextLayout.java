package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.ClearableEditText;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class EditTextLayout extends LinearLayout {

    protected LinearLayout root;
    protected ImageView leftIcon;
    protected ClearableEditText editText;

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

        root = new LinearLayout(context);
        root.setOrientation(HORIZONTAL);
        LayoutParams rootParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setLayoutParams(rootParam);
        addView(root);

        if (GlobalStyle.isIsEditTextLayoutBackgroundSet()) {
            int background = GlobalStyle.getsEditTextLayoutBackground();
            root.setBackgroundResource(background);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EditTextLayout);
        Drawable leftDrawable = array.getDrawable(R.styleable.EditTextLayout_leftIconDrawable);
        boolean clearAllEnabled = array.getBoolean(R.styleable.EditTextLayout_clearAllEnabled, true);
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

        editText = new ClearableEditText(context);
        editText.setClearAllEnabled(clearAllEnabled);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        editText.setLayoutParams(lp);
        if (GlobalStyle.isIsEditTextBackgroundSet()) {
            int background = GlobalStyle.getsEditTextBackground();
            editText.setBackgroundResource(background);
        }
        root.addView(editText);
    }

    public EditText getEditText() {
        return editText;
    }

    public Editable getText() {
        return editText.getText();
    }
}
