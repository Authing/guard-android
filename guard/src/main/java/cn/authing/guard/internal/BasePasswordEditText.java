package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class BasePasswordEditText extends EditTextLayout implements TextWatcher {

    private Drawable eyeOnDrawable;
    private Drawable eyeOffDrawable;
    private Drawable eyeDrawable;
    private ImageView eyeButton;

    public BasePasswordEditText(Context context) {
        this(context, null);
    }

    public BasePasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasePasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected int getDefaultHintResId() {
        return R.string.authing_password_edit_text_hint;
    }

    private void init(Context context, AttributeSet attrs) {
        CharSequence s = getEditText().getHint();
        if (s == null) {
            getEditText().setHint(getDefaultHintResId());
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BasePasswordEditText);
        boolean toggleEnabled = array.getBoolean(R.styleable.BasePasswordEditText_toggleEnabled, true);
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
        if (!errorEnabled) {
            return;
        }

        showError(null);

        if (TextUtils.isEmpty(s)) {
            return;
        }

        Authing.getPublicConfig((config -> {
            if (config == null){
                return;
            }
            int strength = config.getPasswordStrength();
            if (strength != 0) {
                String str = getText().toString();
                int length = str.length();
                String err = "";
                if (length < 6) {
                    if (strength == 2) {
                        err += getResources().getString(R.string.authing_password_strength2);
                    } else if (strength == 3) {
                        err = getResources().getString(R.string.authing_password_strength3);
                    } else {
                        err = getResources().getString(R.string.authing_password_strength1);
                    }
                } else if (strength == 2 || strength == 3) {
                    int count = 0;
                    if (Validator.hasEnglish(str)) {
                        count++;
                    }
                    if (Validator.hasNumber(str)) {
                        count++;
                    }
                    if (Validator.hasSpecialCharacter(str)) {
                        count++;
                    }
                    if (strength == 2) {
                        if (count < 2) {
                            err = getResources().getString(R.string.authing_password_strength2);
                        }
                    } else {
                        if (count < 3) {
                            err = getResources().getString(R.string.authing_password_strength3);
                        }
                    }
                }

                showError(err);
            }
        }));
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
