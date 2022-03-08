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

        EditTextLayout etScheme = findViewById(R.id.et_scheme);
        etScheme.getEditText().setText(App.loadScheme(this));

        EditTextLayout etHost = findViewById(R.id.et_host);
        etHost.getEditText().setText(Authing.getHost());

        EditTextLayout etAppId = findViewById(R.id.et_appid);
        etAppId.getEditText().setText(Authing.getAppId());

        Button btn = findViewById(R.id.btn_save);
        btn.setOnClickListener((v)->{
            String schema = etScheme.getText().toString();
            String host = etHost.getText().toString();
            String appid = etAppId.getText().toString();

            App.saveScheme(this, schema);
            App.saveHost(this, host);
            App.saveAppId(this, appid);

            Authing.setScheme(schema);
            Authing.setHost(host);
            Authing.init(this, appid);

            Toast.makeText(SettingActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        });

        Button restore = findViewById(R.id.btn_restore);
        restore.setOnClickListener((v)->{
            String scheme = "https";
            String host = "authing.cn";
            String appid = "60caaf41df670b771fd08937";
            Authing.setScheme(scheme);
            Authing.setHost(host);
            Authing.init(this, appid);

            etScheme.getEditText().setText(scheme);
            etHost.getEditText().setText(host);
            etAppId.getEditText().setText(appid);

            App.saveScheme(this, scheme);
            App.saveHost(this, host);
            App.saveAppId(this, appid);

            Toast.makeText(SettingActivity.this, "Restored", Toast.LENGTH_SHORT).show();
        });
    }
}