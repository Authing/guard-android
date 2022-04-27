package cn.authing.guard.internal;

import static cn.authing.guard.util.Util.findAllViewByClass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.RegisterContainer;

public class RegisterMethodTabItem extends BaseTabItem {

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
    }

    @Override
    public void gainFocus(BaseTabItem lastFocused) {
        super.gainFocus(lastFocused);

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

    public RegisterContainer.RegisterType getType() {
        return type;
    }

    public void setType(RegisterContainer.RegisterType type) {
        this.type = type;
    }
}
