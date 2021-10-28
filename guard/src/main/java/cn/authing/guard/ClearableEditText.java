package cn.authing.guard;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClearableEditText extends androidx.appcompat.widget.AppCompatEditText {

    protected Drawable clearDrawable;

    public ClearableEditText(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ClearableEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClearableEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        clearDrawable = context.getDrawable(R.drawable.ic_authing_clear_all);
        setMaxLines(1);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
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

    public EditText getEditText() {
        return this;
    }
}
