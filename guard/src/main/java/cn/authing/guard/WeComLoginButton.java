package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.internal.CustomEventButton;
import cn.authing.guard.social.WeCom;

public class WeComLoginButton extends CustomEventButton {

    public WeComLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public WeComLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeComLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.ic_authing_wecom);
        setOnClickListener((v -> login()));
    }

    private void login() {
        WeCom.login(getContext(), (ok, data)->{
            if (callback != null) {
                callback.call(ok, data);
            }
        });
    }
}
