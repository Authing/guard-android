package cn.authing.guard.activity;

import android.os.Bundle;
import android.view.View;

import cn.authing.guard.Authing;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.util.DarkModeManager;
import cn.authing.guard.util.Util;

public class BindPhoneActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_bind_phone);
        DarkModeManager.getInstance().setDarkMode(this);
        PhoneNumberEditText editText = findViewById(R.id.et_phone);
        if (Authing.getCurrentUser() != null && !Util.isNull(Authing.getCurrentUser().getPhone_number())) {
            editText.getEditText().setText(Authing.getCurrentUser().getPhone_number());
            editText.disable();
            editText.showCountryCodePicker(false);
            findViewById(R.id.ll_get_code).setVisibility(View.GONE);
        }
    }
}