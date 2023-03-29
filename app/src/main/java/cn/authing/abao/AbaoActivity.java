package cn.authing.abao;

import android.content.Intent;
import android.os.Bundle;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.GetVerifyCodeButton;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.activity.BaseAuthActivity;
import cn.authing.guard.social.SocialLoginListView;

public class AbaoActivity extends BaseAuthActivity {

    PhoneNumberEditText editText;
    GetVerifyCodeButton btn;

    boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.abao_login);

        editText = findViewById(R.id.pet);
        btn = findViewById(R.id.btn_login);

        if (btn != null) {
            btn.setOnGetVerifyCodeListener(() -> runOnUiThread(() -> {
                next();
            }));
        }


        SocialLoginListView lv = findViewById(R.id.lv_social);
        if (lv != null) {
            lv.setOnLoginListener((code, message, data) -> {
                if (code == 200) {
                    Intent intent = new Intent(AbaoActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void next() {
        Intent intent = new Intent(this, AbaoVerifyCodeActivity.class);
        intent.putExtra("phone", editText.getText().toString());
        //intent.putExtra("country", countryCodePicker.getCountry());
        startActivity(intent);
    }
}