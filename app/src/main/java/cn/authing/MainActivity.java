package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("user");
        TextView tv = findViewById(R.id.tv_nick_name);
        setText(tv, userInfo.getNickname());

        tv = findViewById(R.id.tv_name);
        setText(tv, userInfo.getName());

        tv = findViewById(R.id.tv_username);
        setText(tv, userInfo.getUsername());

        tv = findViewById(R.id.tv_phone);
        setText(tv, userInfo.getPhone_number());

        tv = findViewById(R.id.tv_email);
        setText(tv, userInfo.getEmail());


        TextView tvChangePassword = findViewById(R.id.tv_change_password);
        tvChangePassword.setOnClickListener((v)->{
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        });

        Button btn = findViewById(R.id.btn_logout);
        btn.setOnClickListener(v -> logout());
    }

    private void logout() {
        Authing.logout((ok, data)->{
            if (ok) {
                Intent intent = new Intent(this, SampleListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setText(TextView tv, String s) {
        if (Util.isNull(s)) {
            tv.setText("Unspecified");
        } else {
            tv.setText(s);
        }
    }
}