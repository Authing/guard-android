package cn.authing.guard.internal;

import static cn.authing.guard.util.Util.findAllViewByClass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.LoginContainer;

public class LoginMethodTabItem extends BaseTabItem {

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
    }

    @Override
    public void gainFocus(BaseTabItem lastFocused) {
        super.gainFocus(lastFocused);

        post(()->{
            List<View> containers = findAllViewByClass(this, LoginContainer.class);
            for (View v : containers) {
                LoginContainer container = (LoginContainer)v;
                if (container.getType() == type) {
                    container.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.GONE);
                }
            }
        });
    }

    public LoginContainer.LoginType getType() {
        return type;
    }

    public void setType(LoginContainer.LoginType type) {
        this.type = type;
    }
}
