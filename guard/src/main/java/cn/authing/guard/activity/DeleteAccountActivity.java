package cn.authing.guard.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import cn.authing.guard.Authing;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.TitleLayout;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class DeleteAccountActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setStatusBarColor(this, R.color.authing_status_bar_bg);

        setContentView(R.layout.authing_delete_account);

        TitleLayout titleLayout = findViewById(R.id.title_layout);
        titleLayout.setBackIconClickListener(v -> finish());

        LinearLayout codeSendTipLayout = findViewById(R.id.layout_code_send_tip);
        LinearLayout codeSendLayout = findViewById(R.id.layout_code_send);
        PasswordEditText passwordEditText = findViewById(R.id.et_password);

        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo != null) {
            String phoneNumber = userInfo.getPhone_number();
            if (!Util.isNull(phoneNumber)) {
                codeSendTipLayout.setVisibility(View.VISIBLE);
                codeSendLayout.setVisibility(View.VISIBLE);
                return;
            }

            String password = userInfo.getPassword();
            if (!Util.isNull(password)) {
                passwordEditText.setVisibility(View.VISIBLE);
            }
        }
    }

}