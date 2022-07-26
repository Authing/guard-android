package cn.authing.guard.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class ContinueWithTextView extends LinearLayout {
    public ContinueWithTextView(Context context) {
        this(context, null);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Authing.getPublicConfig(config -> {
            if (config == null || config.getSocialConfigs().size() == 0) {
                setVisibility(View.GONE);
            } else {
                setOrientation(LinearLayout.HORIZONTAL);
                setGravity(Gravity.CENTER);

                ImageView leftView = new ImageView(getContext());
                leftView.setBackgroundResource(R.drawable.authing_social_line_left);
                addView(leftView);

                TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContinueWithTextView);
                String text = array.getString(R.styleable.ContinueWithTextView_middleText);
                float textSize = array.getDimension(R.styleable.ContinueWithTextView_middleTextSize, Util.sp2px(context, 12));
                int textColor = array.getColor(R.styleable.ContinueWithTextView_middleTextColor, context.getColor(R.color.authing_text_gray));
                array.recycle();

                TextView textView = new TextView(getContext());
                textView.setText(text);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                textView.setTextColor(textColor);
                textView.setPadding((int) Util.dp2px(context, 8), 0, (int) Util.dp2px(context, 8), 0);
                addView(textView);

                ImageView rightView = new ImageView(getContext());
                rightView.setBackgroundResource(R.drawable.authing_social_line_right);
                addView(rightView);
            }
        });
    }
}
