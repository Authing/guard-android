package cn.authing.guard.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class BaseTabItem extends LinearLayout {

    protected final TextView button;
    protected final View underLine;
    protected boolean isFocused;

    public BaseTabItem(Context context) {
        this(context, null);
    }

    public BaseTabItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseTabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        button = new TextView(context);
        button.setBackground(null);
        button.setClickable(false);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        int color = getResources().getColor(R.color.authing_text_gray, null);
        button.setTextColor(color);
        addView(button);

        underLine = new View(context);
        int height = (int) Util.dp2px(context, 1);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        lp.topMargin = (int) Util.dp2px(context, 7);
        underLine.setLayoutParams(lp);
        addView(underLine);

        loseFocus();
    }

    public void setText(String text) {
        button.setText(text);
    }

    public void gainFocus(BaseTabItem lastFocused) {
        isFocused = true;
        int color = getResources().getColor(R.color.authing_main, null);
        button.setTextColor(color);
        underLine.setBackgroundColor(color);

        if (lastFocused != null) {
            float lastFocusedX = lastFocused.getX();
            float x = getX();
            Animation animation = new TranslateAnimation(lastFocusedX - x, 0, 0, 0);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setDuration(300);
            animation.setFillAfter(true);
            underLine.startAnimation(animation);
        }
    }

    public void loseFocus() {
        isFocused = false;
        int color = getResources().getColor(R.color.authing_text_gray, null);
        button.setTextColor(color);
        underLine.setBackgroundColor(0);
    }

    public boolean isFocused() {
        return isFocused;
    }
}
