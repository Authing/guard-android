package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.network.Response;
import cn.authing.guard.util.Util;

public class GetVerifyCodeButton extends LoadingButton {

    private final Button getCodeButton;
    private int countDown;
    private final String countDownTip;

    private String text;
    private int textColor;

    public GetVerifyCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        loadingView.setImageResource(R.drawable.ic_authing_animated_loading_blue);

        countDownTip = context.getString(R.string.authing_resend_after);

        getCodeButton = new Button(context);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GetVerifyCodeButton);
        Drawable background = array.getDrawable(R.styleable.GetVerifyCodeButton_background);

        text = array.getString(R.styleable.GetVerifyCodeButton_text);
        textColor = array.getColor(R.styleable.GetVerifyCodeButton_textColor, -1);
        float textSize = array.getDimension(R.styleable.GetVerifyCodeButton_textSize, Util.dp2px(context, 12));
        float paddingLeft = array.getDimension(R.styleable.GetVerifyCodeButton_paddingLeft, -1);
        float paddingTop = array.getDimension(R.styleable.GetVerifyCodeButton_paddingTop, -1);
        float paddingRight = array.getDimension(R.styleable.GetVerifyCodeButton_paddingRight, -1);
        float paddingBottom = array.getDimension(R.styleable.GetVerifyCodeButton_paddingBottom, -1);
        array.recycle();

        if (background == null) {
            getCodeButton.setBackgroundResource(R.drawable.authing_verify_code_background);
        } else {
            getCodeButton.setBackground(background);
        }

        if (TextUtils.isEmpty(text)) {
            text = getContext().getString(R.string.authing_get_verify_code);
        }
        getCodeButton.setText(text);

        getCodeButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        setTextColor();

        int left = getCodeButton.getPaddingLeft();
        int right = getCodeButton.getPaddingRight();
        int top = getCodeButton.getPaddingTop();
        int bottom = getCodeButton.getPaddingBottom();
        if (paddingLeft != -1) {
            left = (int)paddingLeft;
        }
        if (paddingTop != -1) {
            top = (int)paddingTop;
        }
        if (paddingRight != -1) {
            right = (int)paddingRight;
        }
        if (paddingBottom != -1) {
            bottom = (int)paddingBottom;
        }
        getCodeButton.setPadding(left, top, right, bottom);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        int m = (int) Util.dp2px(context, 4);
        lp.setMargins(m, 0, m, 0);
        getCodeButton.setLayoutParams(lp);
        getCodeButton.setOnClickListener((v -> getSMSCode()));
        addView(getCodeButton, 0);
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

        startLoginVisualEffect();
        Util.setErrorText(this, null);
        Guardian.post("https://core.authing.cn/api/v2/sms/send", body, (data)->{
            handleSMSResult(data);
        });
    }

    private void handleSMSResult(Response data) {
        post(()->{
            stopLoginVisualEffect();
            if (data.getCode() == 200) {
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
                getCodeButton.setEnabled(false);
                getCodeButton.setText(String.format(countDownTip, countDown--));
                getCodeButton.setTextColor(0xff888888);
                countDown();
            }, 1000);
        } else {
            getCodeButton.setText(text);
            getCodeButton.setEnabled(true);
            setTextColor();
        }
    }

    @Override
    public void startLoginVisualEffect() {
        super.startLoginVisualEffect();
        getCodeButton.setEnabled(false);
    }

    public void stopLoginVisualEffect() {
        super.stopLoginVisualEffect();
        post(()->{
            getCodeButton.setEnabled(true);
        });
    }

    private void setTextColor() {
        if (textColor == -1) {
            getCodeButton.setTextColor(getContext().getColorStateList(R.color.button_text));
        } else {
            getCodeButton.setTextColor(textColor);
        }
    }
}
