package cn.authing.guard.internal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class EditTextLayout extends LinearLayout implements TextWatcher, View.OnFocusChangeListener {

    protected static final int ENormal = 0;
    protected static final int EAnimated = 1;

    protected LinearLayout root;
    protected int pageType;
    protected int hintMode;
    protected CharSequence hintText; // manually handle it in animated/fixed mode
    protected ImageView leftIcon;
    protected int leftIconSize; // in pixel
    protected AppCompatEditText editText;
    protected ImageView clearAllButton;
    protected boolean errorEnabled;
    protected TextView errorTextView;
    protected String errorText = "";

    protected static final int ICON_LEFT_RIGHT_MARGIN = 8;
    protected static final int LEFT_PADDING = 4;
    protected static final int TOP_PADDING = 4; // in dp
    protected static final int DURATION = 167;
    protected float leftPaddingPx;
    protected float topPaddingPx;
    protected float hintUpY;
    protected float hintDownY;
    protected float hintUpSize;
    protected Paint hintPaint;
    protected ValueAnimator hintYAnimator;
    protected ValueAnimator hintSizeAnimator;
    protected boolean isUp;

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

        setOrientation(VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EditTextLayout);
        pageType = array.getInt(R.styleable.EditTextLayout_pageType,0);
        hintMode = array.getInt(R.styleable.EditTextLayout_hintMode, ENormal);
        Drawable leftDrawable = array.getDrawable(R.styleable.EditTextLayout_leftIconDrawable);
        boolean clearAllEnabled = array.getBoolean(R.styleable.EditTextLayout_clearAllEnabled, true);
        errorEnabled = array.getBoolean(R.styleable.EditTextLayout_errorEnabled, false);
        float textSize = array.getDimension(R.styleable.EditTextLayout_android_textSize, Util.dp2px(context, 16));
        hintText = array.getString(R.styleable.EditTextLayout_android_hint);
        boolean enabled = array.getBoolean(R.styleable.EditTextLayout_enabled, true);
        int maxLines = array.getInt(R.styleable.EditTextLayout_android_maxLines, 1);
        boolean singleLine = array.getBoolean(R.styleable.EditTextLayout_android_singleLine, true);
//        int inputType = array.getInt(R.styleable.EditTextLayout_android_inputType, 0x00000001);

        setWillNotDraw(false);

        root = new RootContainer(context);
        addView(root);
        root.setOrientation(HORIZONTAL);
        if (hintMode == ENormal) {
            LayoutParams rootParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            root.setGravity(Gravity.CENTER_VERTICAL);
            root.setLayoutParams(rootParam);
        } else if (hintMode == EAnimated) {
            LayoutParams rootParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            root.setGravity(Gravity.BOTTOM);
            root.setLayoutParams(rootParam);
        }

        // Intrinsically, setting background of *EditText should set our root's background
        // which includes left icon, input box, right icon, clear all button while excludes error text
        Drawable bgParent = getBackground();
        root.setBackground(bgParent);
        setBackground(null);

        if (GlobalStyle.isIsEditTextLayoutBackgroundSet()) {
            int background = GlobalStyle.getEditTextLayoutBackground();
            root.setBackgroundResource(background);
        }

        int paddingStart = getPaddingStart();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingEnd = getPaddingEnd();
        root.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
        setPadding(0, 0, 0, 0);

        leftIcon = new ImageView(context);
        leftIconSize = (int) Util.dp2px(context, 24);
        LayoutParams iconParam = new LayoutParams(leftIconSize, leftIconSize);
        leftIcon.setLayoutParams(iconParam);
        int m = (int) Util.dp2px(context, ICON_LEFT_RIGHT_MARGIN);
        iconParam.setMargins(m, 0, 0, 0);
        leftIcon.setImageDrawable(leftDrawable);
        root.addView(leftIcon);
        if (leftDrawable == null) {
            leftIcon.setVisibility(View.GONE);
        }

        editText = new AppCompatEditText(context);
        if (bgParent != null) {
            // use parent's background as root's. clear EditText's bg
            editText.setBackground(null);
        }
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        editText.setHint(hintText);
        editText.setHintTextColor(array.getColor(R.styleable.EditTextLayout_hintColor, 0xff808080));
        editText.setTextColor(array.getColor(R.styleable.EditTextLayout_android_textColor, 0xff000000));
        editText.setMaxLines(maxLines);
        editText.setSingleLine(singleLine);
        editText.setEnabled(enabled);
//        editText.setInputType(inputType);
        editText.setOnFocusChangeListener(this);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        editText.setLayoutParams(lp);
        if (GlobalStyle.isIsEditTextBackgroundSet()) {
            int background = GlobalStyle.getEditTextBackground();
            editText.setBackgroundResource(background);
        }
        root.addView(editText);

        if (clearAllEnabled) {
            addClearAllButton();
        }

        if (hintMode == EAnimated) {
            hintPaint = new Paint();
            int color = editText.getHintTextColors().getDefaultColor();
            hintPaint.setColor(color);
            editText.setHintTextColor(0);
            leftPaddingPx = Util.dp2px(context, LEFT_PADDING);
            topPaddingPx = Util.dp2px(context, TOP_PADDING);
        }

        errorTextView = new TextView(context);
        errorTextView.setTextColor(context.getColor(R.color.authing_error));
        if (errorEnabled) {
            errorTextView.setVisibility(View.INVISIBLE);
        } else {
            errorTextView.setVisibility(View.GONE);
        }
        addView(errorTextView);

        array.recycle();
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
        int p = (int) Util.dp2px(context, ICON_LEFT_RIGHT_MARGIN);
        iconParam.setMargins(p, 0, p, 0);
        clearAllButton.setLayoutParams(iconParam);
        clearAllButton.setVisibility(View.GONE);
        clearAllButton.setBackground(clearDrawable);
        clearAllTouchArea.setOnClickListener(this::clearAllText);
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

    public String getErrorText() {
        return errorText;
    }

    public void showError(String error) {
        if (errorEnabled) {
//            post(()->{
                errorText = error;
                if (TextUtils.isEmpty(error)) {
                    errorTextView.setVisibility(View.INVISIBLE);
                } else {
                    errorTextView.setText(errorText);
                    errorTextView.setVisibility(View.VISIBLE);
                }
//            });
        }
    }

    private void clearAllText(View v) {
        editText.setText("");
        if (hintMode == EAnimated && !editText.hasFocus()) {
            moveHintDown();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (clearAllButton != null) {
            if (editText.isEnabled() && text.toString().length() > 0) {
                clearAllButton.setVisibility(View.VISIBLE);
            } else {
                clearAllButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (hintMode == EAnimated) {
            // force left icon to be vertically centered
            int h = getHeight();
            int top = (h - leftIconSize) / 2;
            leftIcon.layout(leftIcon.getLeft(), top, leftIcon.getRight(), h-top);

            if (clearAllButton != null) {
                int eh = editText.getHeight();
                int ch = clearAllButton.getHeight();
                int cbb = editText.getBottom() - (eh - ch) / 2;
                clearAllButton.layout(clearAllButton.getLeft(), cbb - clearAllButton.getHeight(), clearAllButton.getRight(), cbb);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.setPressed(hasFocus);
        if (hintMode == EAnimated && TextUtils.isEmpty(getText())) {
            if (hasFocus) {
                moveHintUp();
            } else {
                moveHintDown();
            }
        }
    }

    private void moveHintUp() {
        isUp = true;

        float downTextSize = editText.getTextSize();
        float upTextHeight = getHeight() - editText.getHeight() - TOP_PADDING*2;
        // when there is a lot of empty space, up size still has to be smaller
        hintUpSize = Math.min(upTextHeight, downTextSize*4/5);
        hintPaint.setTextSize(hintUpSize);

        int h = getHeight();
        float a = hintPaint.ascent();
        float d = hintPaint.descent();
        float th = d - a;
        hintUpY = topPaddingPx + th - d;
        hintDownY = (h + th)/2 - d;
        hintYAnimator = ValueAnimator.ofFloat(hintDownY, hintUpY);
        hintYAnimator.setDuration(DURATION);
        hintYAnimator.start();

        hintSizeAnimator = ValueAnimator.ofFloat(editText.getTextSize(), hintUpSize);
        hintSizeAnimator.setDuration(DURATION);
        hintSizeAnimator.setInterpolator(new DecelerateInterpolator());
        hintSizeAnimator.start();
        invalidate();
    }

    private void moveHintDown() {
        isUp = false;

        int h = getHeight();
        float a = hintPaint.ascent();
        float d = hintPaint.descent();
        float th = d - a;
        hintUpY = topPaddingPx + th - d;
        hintDownY = (h + th)/2 - d;
        hintYAnimator = ValueAnimator.ofFloat(hintUpY, hintDownY);
        hintYAnimator.setDuration(DURATION);
        hintYAnimator.start();

        hintSizeAnimator = ValueAnimator.ofFloat(hintUpSize, editText.getTextSize());
        hintSizeAnimator.setDuration(DURATION);
        hintSizeAnimator.setInterpolator(new AccelerateInterpolator());
        hintSizeAnimator.start();
    }

    private class RootContainer extends LinearLayout {

        public RootContainer(Context context) {
            this(context, null);
        }

        public RootContainer(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public RootContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public RootContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (hintMode == EAnimated) {
                hintText = editText.getHint();
                if (TextUtils.isEmpty(hintText)) {
                    return;
                }

                hintPaint.setTextSize(editText.getTextSize());
                hintPaint.setTypeface(editText.getTypeface());

                canvas.save();

                canvas.translate(leftIcon.getRight(), 0);

                if (hintYAnimator != null && hintYAnimator.isRunning()) {
                    hintPaint.setTextSize((float)hintSizeAnimator.getAnimatedValue());
                    float y = (float) hintYAnimator.getAnimatedValue();
                    canvas.drawText(hintText.toString(), leftPaddingPx, y, hintPaint);
                    invalidate();
                } else {
                    int h = getHeight();
                    float a = hintPaint.ascent();
                    float d = hintPaint.descent();
                    float th = d - a;
                    if (isUp) {
                        hintPaint.setTextSize(hintUpSize);
                        canvas.drawText(hintText.toString(), leftPaddingPx, hintUpY, hintPaint);
                    } else if (TextUtils.isEmpty(getText())) {
                        float y = (h + th) / 2 - d;
                        canvas.drawText(hintText.toString(), leftPaddingPx, y, hintPaint);
                    }
                }

                canvas.restore();
            }
        }
    }
}
