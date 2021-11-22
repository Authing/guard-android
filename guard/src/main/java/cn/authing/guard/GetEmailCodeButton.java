package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class GetEmailCodeButton extends LoadingButton {

    public GetEmailCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetEmailCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GetEmailCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GetEmailCodeButton);
        String text = array.getString(R.styleable.GetEmailCodeButton_android_text);
        array.recycle();

        setText(text);

        setOnClickListener((v -> getEmailCode()));
    }

    private void getEmailCode() {
        String email;
        View v = Util.findViewByClass(this, AccountEditText.class);
        if (v != null) {
            AccountEditText editText = (AccountEditText)v;
            email = editText.getText().toString();
        } else {
            email = AuthFlow.get(getContext(), AuthFlow.KEY_ACCOUNT);
        }

        if (!Validator.isValidEmail(email)) {
            Util.setErrorText(this, getContext().getString(R.string.authing_invalid_email));
            return;
        }

        startLoadingVisualEffect();
        Util.setErrorText(this, null);

        AuthClient.sendResetPasswordEmail(email, this::handleResult);
    }

    private void handleResult(int code, String message, Object ignore) {
        post(()->{
            stopLoadingVisualEffect();
            if (code == 200) {
            } else {
                Util.setErrorText(this, getContext().getString(R.string.authing_get_email_code_failed));
            }
        });
    }

}
