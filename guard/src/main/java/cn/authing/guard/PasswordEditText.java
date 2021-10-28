package cn.authing.guard;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class PasswordEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Drawable eyeDrawable;
    private Drawable eyeOffDrawable;
    private Drawable rightDrawable;

    public PasswordEditText(Context context) {
        super(context);
        init(context);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
//        String s1 = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_marginStart");
//        if (s1 == null) {
//            ViewGroup.LayoutParams lp = getLayoutParams();
//            if (lp instanceof ViewGroup.MarginLayoutParams) {
//                ((ViewGroup.MarginLayoutParams) lp).setMarginStart(64);
//            }
//        }
//
//        String s2 = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_marginEnd");
//        TypedArray ta = context.obtainStyledAttributes(attrs, new int[]{R.styleable.Layout_android_layout_marginStart});
//        float ml = ta.getInt(0, 0);
        init(context);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        CharSequence s = getHint();
        if (s == null) {
            setHint(R.string.password_edit_text_hint);
        }

        eyeDrawable = context.getDrawable(R.drawable.ic_authing_eye);
        eyeOffDrawable = context.getDrawable(R.drawable.ic_authing_eye_off);
        rightDrawable = eyeDrawable;
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        setMaxLines(1);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        Drawable[] drawables = this.getCompoundDrawablesRelative();
        if (text.toString().length() > 0) {
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], rightDrawable, drawables[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (rightDrawable != null) {
                Rect bounds = rightDrawable.getBounds();
                int actionX = (int)event.getX();
                int actionY = (int) event.getY();
                int x = getWidth() - getPaddingRight() - actionX;
                int y = getHeight() - getPaddingBottom() - actionY;

                if (bounds.contains(x, y)) {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    toggle();
                    return false;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public EditText getEditText() {
        return this;
    }

    private void toggle() {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (rightDrawable == eyeDrawable) {
            setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            rightDrawable = eyeOffDrawable;
        } else {
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            rightDrawable = eyeDrawable;
        }
        Drawable[] drawables = this.getCompoundDrawablesRelative();
        setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], rightDrawable, drawables[3]);
        setSelection(start, end);
    }
}
