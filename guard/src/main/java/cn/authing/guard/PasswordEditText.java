package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.util.Util;

public class PasswordEditText extends EditTextLayout implements TextWatcher {

    private Drawable eyeOnDrawable;
    private Drawable eyeOffDrawable;
    private Drawable eyeDrawable;
    private ImageView eyeButton;

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        CharSequence s = getEditText().getHint();
        if (s == null) {
            getEditText().setHint(R.string.password_edit_text_hint);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText);
        boolean toggleEnabled = array.getBoolean(R.styleable.PasswordEditText_toggleEnabled, true);
        array.recycle();

        getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        getEditText().setTypeface(Typeface.DEFAULT);

        if (toggleEnabled) {
            eyeOnDrawable = context.getDrawable(R.drawable.ic_authing_eye);
            eyeOffDrawable = context.getDrawable(R.drawable.ic_authing_eye_off);
            eyeDrawable = eyeOnDrawable;

            LinearLayout eyeTouchArea = new LinearLayout(context);
            eyeTouchArea.setOrientation(HORIZONTAL);
            eyeTouchArea.setGravity(Gravity.CENTER_VERTICAL);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            eyeTouchArea.setLayoutParams(lp);

            int length = (int) Util.dp2px(context, 24);
            eyeButton = new ImageView(context);
            LayoutParams iconParam = new LayoutParams(length, length);
            int p = (int) Util.dp2px(context, 6);
            iconParam.setMargins(p, 0, p, 0);
            eyeButton.setLayoutParams(iconParam);
            eyeButton.setVisibility(View.GONE);
            eyeButton.setBackground(eyeOnDrawable);
            eyeTouchArea.setOnClickListener((v -> toggle()));
            eyeTouchArea.addView(eyeButton);
            root.addView(eyeTouchArea);

            getEditText().addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (eyeButton != null) {
            if (text.toString().length() > 0) {
                eyeButton.setVisibility(View.VISIBLE);
            } else {
                eyeButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void toggle() {
        int start = getEditText().getSelectionStart();
        int end = getEditText().getSelectionEnd();
        if (eyeDrawable == eyeOnDrawable) {
            getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeDrawable = eyeOffDrawable;
        } else {
            getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeDrawable = eyeOnDrawable;
        }
        eyeButton.setBackground(eyeDrawable);
        getEditText().setSelection(start, end);
    }
}
