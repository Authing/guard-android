package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.network.Response;
import cn.authing.guard.util.Util;

public class GetVerifyCodeButton extends LoadingButton {

    private int countDown;
    private String countDownTip;

    private final String text;

    public GetVerifyCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loading = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);

        countDownTip = context.getString(R.string.authing_resend_after);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            text = getContext().getString(R.string.authing_get_verify_code);
        } else {
            text = attrs.getAttributeValue(NS_ANDROID, "text");
        }
        setText(text);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_verify_code_background);
        }

        setOnClickListener((v -> getSMSCode()));
    }

    private void getSMSCode() {
        View v = Util.findViewByClass(this, PhoneNumberEditText.class);
        if (v == null) {
            return;
        }

        PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)v;
        if (!phoneNumberEditText.isContentValid()) {
            Util.setErrorText(this, getContext().getString(R.string.authing_invalid_phone_number));
            return;
        }

        String phoneNumber = phoneNumberEditText.getText().toString();
        JSONObject body = new JSONObject();
        try {
            body.put("phone", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        startLoadingVisualEffect();
        Util.setErrorText(this, null);
        Guardian.post("https://core.authing.cn/api/v2/sms/send", body, this::handleSMSResult);
    }

    private void handleSMSResult(Response data) {
        post(()->{
            stopLoadingVisualEffect();
            if (data != null && data.getCode() == 200) {
                countDown = 60;
                countDown();
            } else {
                Util.setErrorText(this, getContext().getString(R.string.authing_get_verify_code_failed));
            }
        });
    }

    private void countDown() {
        if (countDown >= 0) {
            postDelayed(() -> {
                updateCountDown();
                countDown();
            }, 1000);
        } else {
            setText(text);
            setEnabled(true);
        }
    }

    private void updateCountDown() {
        setEnabled(false);
        setText(String.format(countDownTip, countDown--));
    }

    public void startCountDown() {
        startCountDown(60);
    }

    public void startCountDown(int cd) {
        countDown = cd;
        updateCountDown();
        countDown();
    }

    public void setCountDownTip(String format) {
        countDownTip = format;
    }
}
