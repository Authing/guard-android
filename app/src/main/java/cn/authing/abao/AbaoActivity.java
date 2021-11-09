package cn.authing.abao;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.activity.BaseLoginActivity;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.network.Response;
import cn.authing.guard.social.SocialLoginListView;
import cn.authing.guard.util.Util;

public class AbaoActivity extends BaseLoginActivity {

    PhoneNumberEditText editText;
    ImageView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.abao_login);
        editText = findViewById(R.id.pet);
        btn = findViewById(R.id.btn_login);

        if (btn != null) {
            btn.setOnClickListener((v) -> {
//                next();

                if (!editText.isContentValid()) {
                    Util.setErrorText(editText, getString(cn.authing.guard.R.string.authing_invalid_phone_number));
                    return;
                }

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

        SocialLoginListView lv = findViewById(R.id.lv_social);
        if (lv != null) {
            lv.setOnLoginListener((ok, data) -> {
                if (ok) {
                    Intent intent = new Intent(AbaoActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void handleSMSResult(Response data) {
        runOnUiThread(() -> {
            if (data != null && data.getCode() == 200) {
                next();
            } else {
                Util.setErrorText(editText, getString(cn.authing.guard.R.string.authing_get_verify_code_failed));
            }
        });
    }

    private void next() {
        Intent intent = new Intent(this, AbaoVerifyCodeActivity.class);
        intent.putExtra("phone", editText.getText().toString());
        startActivity(intent);
    }
}