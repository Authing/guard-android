package cn.authing.guard.internal;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import cn.authing.guard.Callback;
import cn.authing.guard.data.UserInfo;

public class CustomEventButton extends AppCompatButton {

    protected Callback<UserInfo> callback;

    public CustomEventButton(@NonNull Context context) {
        super(context);
    }

    public CustomEventButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEventButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnLoginListener(Callback<UserInfo> callback) {
        this.callback = callback;
    }
}
