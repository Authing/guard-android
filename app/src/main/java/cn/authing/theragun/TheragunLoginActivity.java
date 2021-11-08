package cn.authing.theragun;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.R;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.activity.BaseLoginActivity;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.network.Response;
import cn.authing.guard.util.Util;

public class TheragunLoginActivity extends BaseLoginActivity {

    PhoneNumberEditText editText;
    LoadingButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.theragun_login);

        editText = findViewById(R.id.theragun_pet);
        btn = findViewById(R.id.btn_login);
        btn.setOnClickListener((v)->{
            if (!editText.isContentValid()) {
                Util.setErrorText(editText, getString(cn.authing.guard.R.string.authing_invalid_phone_number));
                return;
            }

            btn.startLoadingVisualEffect();

            String phoneNumber = editText.getText().toString();
            JSONObject body = new JSONObject();
            try {
                body.put("phone", phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Util.setErrorText(editText, null);
            Guardian.post("https://core.authing.cn/api/v2/sms/send", body, this::handleSMSResult);
        });
    }

    private void handleSMSResult(Response data) {
        runOnUiThread(()->{
            btn.stopLoadingVisualEffect();
            if (data != null && data.getCode() == 200) {
                next();
            } else {
                Util.setErrorText(editText, getString(cn.authing.guard.R.string.authing_get_verify_code_failed));
            }
        });
    }

    private void next() {
        Intent intent = new Intent(this, TheragunVerifyCodeActivity.class);
        intent.putExtra("phone", editText.getText().toString());
        startActivity(intent);
    }
}