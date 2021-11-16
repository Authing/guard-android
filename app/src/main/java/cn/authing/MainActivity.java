package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("user");
        TextView tv = findViewById(R.id.tv_name);
        String name = userInfo.getName();
        if (name == null || name.equals("null")) {
            name = userInfo.getPhone_number();
        }
        if (name == null || name.equals("null")) {
            name = userInfo.getEmail();
        }
        tv.setText("你好，" + name);

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
}