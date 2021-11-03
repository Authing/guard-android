package cn.authing.guard;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.util.Util;

public class VerifyCodeEditText extends EditTextLayout implements TextWatcher {

    private final Button getCodeButton;
    private int countDown;
    private final String countDownTip;
    private int maxLength = 6;

    public VerifyCodeEditText(@NonNull Context context) {
        this(context, null);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        countDownTip = context.getString(R.string.authing_resend_after);

        editText.setHint(R.string.verify_code_edit_text_hint);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (Authing.getPublicConfig() != null) {
            maxLength = Authing.getPublicConfig().getVerifyCodeLength();
        }
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        editText.addTextChangedListener(this);

        getCodeButton = new Button(context);
        getCodeButton.setText(R.string.authing_get_verify_code);
        getCodeButton.setBackgroundResource(R.drawable.authing_get_code_button_background);
        getCodeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        getCodeButton.setTextColor(context.getColor(R.color.authing_main));
        LayoutParams btnlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int m = (int)Util.dp2px(context, 4);
        btnlp.setMargins(m, 0, m, 0);
        getCodeButton.setLayoutParams(btnlp);
        getCodeButton.setOnClickListener((v -> getSMSCode()));
        addView(getCodeButton);
    }

    public Editable getText() {
        return editText.getText();
    }

    private void getSMSCode() {
        PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)Util.findViewByClass(this, PhoneNumberEditText.class);
        if (phoneNumberEditText == null) {
            return;
        }

        String phoneNumber = phoneNumberEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("phone", phoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Guardian.post("https://core.authing.cn/api/v2/sms/send", body, (data)->{
            if (data.getCode() == 200 || data.getCode() == 500) {
                countDown = 60;
                countDown();
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
            getCodeButton.setText(R.string.authing_get_verify_code);
            getCodeButton.setEnabled(true);
            getCodeButton.setTextColor(getContext().getColor(R.color.authing_main));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == maxLength) {
            LoginButton button = (LoginButton)Util.findViewByClass(this, LoginButton.class);
            if (button != null) {
                button.login();
            }
        }
    }
}
