package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.GlobalCountDown;
import cn.authing.guard.util.Util;

public class GetVerifyCodeButton extends LoadingButton {

    private String countDownTip;

    private String text;

    public GetVerifyCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loadingLocation = OVER; // over on top since this button is usually small

        countDownTip = context.getString(R.string.authing_resend_after);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            text = getContext().getString(R.string.authing_get_verify_code);
            setText(text);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_verify_code_background);
        }

        if (GlobalCountDown.countDown > 0) {
            countDown();
        }
        setOnClickListener((v -> getSMSCode()));
    }

    private void getSMSCode() {
        String phoneNumber = Util.getPhoneNumber(this);
        if (!TextUtils.isEmpty(phoneNumber)) {
            startLoadingVisualEffect();
            Util.setErrorText(this, null);
            AuthClient.sendSms(phoneNumber, this::handleSMSResult);
        }
    }

    private void handleSMSResult(int code, String message, Object ignore) {
        post(()->{
            stopLoadingVisualEffect();
            if (code == 200) {
                countDown();
            } else {
                Util.setErrorText(this, getContext().getString(R.string.authing_get_verify_code_failed));
            }
        });
    }

    private void countDown() {
        if (GlobalCountDown.countDown > 0) {
            updateCountDown();
            postDelayed(this::countDown, 1000);
        } else {
            setText(text);
            setEnabled(true);
        }
    }

    private void updateCountDown() {
        setEnabled(false);
        setText(String.format(countDownTip, GlobalCountDown.countDown));
    }

    public void setCountDownTip(String format) {
        countDownTip = format;
    }
}
