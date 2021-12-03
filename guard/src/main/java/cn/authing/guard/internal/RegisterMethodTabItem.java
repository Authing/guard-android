package cn.authing.guard.internal;

import static cn.authing.guard.util.Util.findAllViewByClass;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.R;
import cn.authing.guard.RegisterContainer;
import cn.authing.guard.util.Util;

public class RegisterMethodTabItem extends LinearLayout {

    private final Button button;
    private final View underLine;
    private RegisterContainer.RegisterType type;

    public RegisterMethodTabItem(Context context) {
        this(context, null);
    }

    public RegisterMethodTabItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterMethodTabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RegisterMethodTabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        button = new Button(context);
        button.setBackground(null);
        button.setClickable(false);
        addView(button);

        underLine = new View(context);
        int height = (int) Util.dp2px(context, 2);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        underLine.setLayoutParams(lp);
        addView(underLine);

        loseFocus();
    }

    public void setText(String text) {
        button.setText(text);
    }

    public void gainFocus() {
        int color = getResources().getColor(R.color.authing_main, null);
        button.setTextColor(color);
        underLine.setBackgroundColor(color);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        post(()->{
            List<View> containers = findAllViewByClass(this, RegisterContainer.class);
            for (View v : containers) {
                RegisterContainer container = (RegisterContainer)v;
                if (container.getType() == type) {
                    container.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.GONE);
                }
            }
        });
    }

    public void loseFocus() {
        button.setTextColor(0xffaaaaaa);
        underLine.setBackgroundColor(0);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
    }

    public RegisterContainer.RegisterType getType() {
        return type;
    }

    public void setType(RegisterContainer.RegisterType type) {
        this.type = type;
    }
}
