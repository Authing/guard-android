package cn.authing.guard;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.network.Guardian;
import cn.authing.guard.util.Util;

public class VerifyCodeEditText extends LinearLayout {

    private ClearableEditText editText;
    private Button getCodeButton;
    private int countDown;
    private String countDownTip;

    public VerifyCodeEditText(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public String getText() {
        return editText.getText().toString();
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        countDownTip = context.getString(R.string.authing_resend_after);

        editText = new ClearableEditText(context);
        editText.setHint(R.string.verify_code_edit_text_hint);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        editText.setLayoutParams(lp);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        addView(editText);

        getCodeButton = new Button(context);
        getCodeButton.setText(R.string.authing_get_verify_code);
        getCodeButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        getCodeButton.setTextColor(context.getColor(R.color.authing_main));
        int height = (int)Util.dp2px(context, 40);
        LayoutParams btnlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        int m = (int)Util.dp2px(context, 4);
        btnlp.setMargins(m, 0, m, 0);
        getCodeButton.setLayoutParams(btnlp);
        getCodeButton.setOnClickListener((v -> {
            getSMSCode();
        }));
        addView(getCodeButton);
    }

    private void getSMSCode() {
        String phoneNumber = null;
        ViewGroup vg = (ViewGroup) getParent();
        for (int i = 0;i < vg.getChildCount();++i) {
            View v = vg.getChildAt(i);
            if (v instanceof PhoneNumberEditText) {
                PhoneNumberEditText et = (PhoneNumberEditText)v;
                phoneNumber = et.getText().toString();
                break;
            }
        }

        if (phoneNumber == null) {
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
            } else {

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
}
