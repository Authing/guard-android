package cn.authing.nissan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import cn.authing.R;
import cn.authing.guard.AccountEditText;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.activity.BaseAuthActivity;
import cn.authing.guard.util.Validator;

public class NissanVirtualKeySignupOneActivity extends BaseAuthActivity implements TextWatcher {
    EditText etFN;
    EditText etLN;
    AccountEditText etEmail;
    PhoneNumberEditText etPhone;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.activity_nissan_virtual_key_signup1);

        etFN = findViewById(R.id.nvk_et_fn);
        etLN = findViewById(R.id.nvk_et_ln);
        etEmail = findViewById(R.id.nvk_et_email);
        etPhone = findViewById(R.id.nvk_et_phone);

        etFN.addTextChangedListener(this);
        etLN.addTextChangedListener(this);
        etEmail.getEditText().addTextChangedListener(this);
        etPhone.getEditText().addTextChangedListener(this);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener((v)->{
            Intent intent = new Intent(NissanVirtualKeySignupOneActivity.this, NissanVirtualKeySignupTwoActivity.class);
            intent.putExtra("email", etEmail.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(etFN.getText())
                && !TextUtils.isEmpty(etLN.getText())
                && !TextUtils.isEmpty(etEmail.getEditText().getText())
                && !TextUtils.isEmpty(etPhone.getEditText().getText())
                && Validator.isValidEmail(etEmail.getText())) {
            btnNext.setEnabled(true);
        }
    }
}