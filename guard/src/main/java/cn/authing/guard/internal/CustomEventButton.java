package cn.authing.guard.internal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.Callback;
import cn.authing.guard.data.UserInfo;

public class CustomEventButton extends RelativeLayout {

    protected Callback<UserInfo> callback;

    public CustomEventButton(@NonNull Context context) {
        this(context, null);
    }

    public CustomEventButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEventButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnLoginListener(Callback<UserInfo> callback) {
        this.callback = callback;
    }
}
