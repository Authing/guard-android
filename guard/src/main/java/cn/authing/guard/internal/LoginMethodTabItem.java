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

import cn.authing.guard.LoginContainer;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class LoginMethodTabItem extends LinearLayout {

    private Button button;
    private View underLine;
    private LoginContainer.LoginType type;

    public LoginMethodTabItem(Context context) {
        this(context, null);
    }

    public LoginMethodTabItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginMethodTabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LoginMethodTabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
            List<View> containers = findAllViewByClass(this, LoginContainer.class);
            for (View v : containers) {
                LoginContainer container = (LoginContainer)v;
                if (container.getType() == type) {
                    container.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void loseFocus() {
        button.setTextColor(0xffaaaaaa);
        underLine.setBackgroundColor(0);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
    }

    public LoginContainer.LoginType getType() {
        return type;
    }

    public void setType(LoginContainer.LoginType type) {
        this.type = type;
    }
}
