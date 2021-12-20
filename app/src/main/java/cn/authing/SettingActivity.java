package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import cn.authing.guard.Authing;
import cn.authing.guard.internal.EditTextLayout;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        EditTextLayout etAppId = findViewById(R.id.et_appid);
        etAppId.getEditText().setText(Authing.getAppId());

        EditTextLayout etHost = findViewById(R.id.et_host);
        etHost.getEditText().setText(Authing.getHost());

        Button btn = findViewById(R.id.btn_save);
        btn.setOnClickListener((v)->{
            String appid = etAppId.getText().toString();
            String host = etHost.getText().toString();
            Authing.init(this, appid);
            Authing.setHost(host);

            App.saveAppId(this, appid);
            App.saveHost(this, host);

            Toast.makeText(SettingActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        });

        Button restore = findViewById(R.id.btn_restore);
        restore.setOnClickListener((v)->{
            String appid = "60caaf41df670b771fd08937";
            String host = "authing.cn";
            Authing.init(this, appid);
            Authing.setHost(host);

            etAppId.getEditText().setText(appid);
            etHost.getEditText().setText(host);


            App.saveAppId(this, appid);
            App.saveHost(this, host);

            Toast.makeText(SettingActivity.this, "Restored", Toast.LENGTH_SHORT).show();
        });
    }
}