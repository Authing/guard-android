package cn.authing.guard;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClearableEditText extends androidx.appcompat.widget.AppCompatEditText {

    protected Drawable clearDrawable;
    protected boolean clearAllEnabled = true;

    public ClearableEditText(@NonNull Context context) {
        this(context, null);
    }

    public ClearableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ClearableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        clearDrawable = context.getDrawable(R.drawable.ic_authing_clear_all);
        setMaxLines(1);
        setSingleLine(true);

        setOnFocusChangeListener((v, hasFocus)-> {
            ((ViewGroup)getParent()).setPressed(hasFocus);
        });
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (!clearAllEnabled) {
            return;
        }
        Drawable[] drawables = this.getCompoundDrawablesRelative();
        if (text.toString().length() > 0) {
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], clearDrawable, drawables[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (clearDrawable != null) {
                Rect bounds = clearDrawable.getBounds();
                int actionX = (int)event.getX();
                int actionY = (int) event.getY();
                int x = getWidth() - getPaddingRight() - actionX;
                int y = getHeight() - getPaddingBottom() - actionY;

                if (bounds.contains(x, y)) {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    setText("");
                    return false;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean isClearAllEnabled() {
        return clearAllEnabled;
    }

    public void setClearAllEnabled(boolean clearAllEnabled) {
        this.clearAllEnabled = clearAllEnabled;
    }
}
