package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class GetEmailCodeButton extends LoadingButton {

    private String scene = "RESET_PASSWORD";

    public GetEmailCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetEmailCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GetEmailCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GetEmailCodeButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            String text = getContext().getString(R.string.authing_get_verify_code);
            setText(text);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_verify_code_background);
        }

        setOnClickListener((v -> getEmailCode()));
    }

    private void getEmailCode() {
        String email;
        View v = Util.findViewByClass(this, AccountEditText.class);
        if (v != null) {
            AccountEditText editText = (AccountEditText)v;
            email = editText.getText().toString();
        } else {
            email = (String) AuthFlow.get(getContext(), AuthFlow.KEY_ACCOUNT);
        }

        if (!Validator.isValidEmail(email)) {
            Util.setErrorText(this, getContext().getString(R.string.authing_invalid_email));
            return;
        }

        startLoadingVisualEffect();
        Util.setErrorText(this, null);

        AuthClient.sendEmail(email, scene, this::handleResult);
    }

    private void handleResult(int code, String message, Object ignore) {
        post(()->{
            stopLoadingVisualEffect();
            if (code != 200) {
                Util.setErrorText(this, getContext().getString(R.string.authing_get_email_code_failed));
            }
        });
    }

    public void setScene(String scene) {
        this.scene = scene;
    }
}
