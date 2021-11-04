package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.internal.CustomEventButton;

public class WechatLoginButton extends CustomEventButton {

    public WechatLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public WechatLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WechatLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.ic_authing_wechat);
        setOnClickListener((v -> login()));
    }

    private void login() {
        Wechat.login(getContext(), (ok, data)->{
            if (callback != null) {
                callback.call(ok, data);
            }
        });
    }
}
