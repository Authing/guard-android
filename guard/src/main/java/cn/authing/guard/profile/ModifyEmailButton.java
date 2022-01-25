package cn.authing.guard.profile;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class ModifyEmailButton extends PrimaryButton {

    private boolean hasEmail;

    public ModifyEmailButton(@NonNull Context context) {
        this(context, null);
    }

    public ModifyEmailButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public ModifyEmailButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (Authing.getCurrentUser() != null && !Util.isNull(Authing.getCurrentUser().getEmail()))
            hasEmail = true;

        Authing.getPublicConfig(config -> {
            if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
                if (hasEmail) {
                    setText(R.string.authing_unbind);
                } else {
                    setText(R.string.authing_bind);
                }
            }

            setOnClickListener((v -> clicked()));
        });
    }

    public void clicked() {
        String email = Util.getAccount(this);
        String vCode = Util.getVerifyCode(this);
        startLoadingVisualEffect();
        if (hasEmail) {
            AuthClient.unbindEmail((code, message, data)-> {
                handleResult(code, message);
            });
        } else {
            AuthClient.bindEmail(email, vCode, (code, message, data)-> {
                handleResult(code, message);
            });
        }
    }

    private void handleResult(int code, String message) {
        stopLoadingVisualEffect();
        if (code == 200) {
            ((Activity)getContext()).finish();
        } else {
            Util.setErrorText(this, message);
        }
    }
}
