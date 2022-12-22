package cn.authing.guard.internal;

import static cn.authing.guard.util.Util.findAllViewByClass;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.social.bind.SocialBindContainer;

public class SocialBindMethodTabItem extends BaseTabItem {

    private SocialBindContainer.SocialBindType type;

    public SocialBindMethodTabItem(Context context) {
        this(context, null);
    }

    public SocialBindMethodTabItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialBindMethodTabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SocialBindMethodTabItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void gainFocus(BaseTabItem lastFocused) {
        super.gainFocus(lastFocused);

        post(() -> {
            List<View> containers = findAllViewByClass(this, SocialBindContainer.class);
            for (View v : containers) {
                SocialBindContainer container = (SocialBindContainer) v;
                if (container.getType() == type) {
                    container.setVisibility(View.VISIBLE);
                } else {
                    container.setVisibility(View.GONE);
                }
            }
        });
    }

    public SocialBindContainer.SocialBindType getType() {
        return type;
    }

    public void setType(SocialBindContainer.SocialBindType type) {
        this.type = type;
    }
}
