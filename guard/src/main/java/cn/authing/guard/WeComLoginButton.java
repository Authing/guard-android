package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.internal.CustomEventButton;
import cn.authing.guard.social.WeCom;

public class WeComLoginButton extends CustomEventButton {

    public WeComLoginButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WeComLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeComLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
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
