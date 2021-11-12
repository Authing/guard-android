package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.authing.abao.AbaoActivity;
import cn.authing.appauth.AppAuthActivity;
import cn.authing.guard.activity.AuthingLoginActivity;
import cn.authing.nissan.NissanVirtualKeyLoginActivity;
import cn.authing.oneclick.OneClickActivity;
import cn.authing.theragun.TheragunLoginActivity;
import cn.authing.webview.AuthingWebViewActivity;
import cn.authing.wechat.WechatLoginActivity;

public class SampleListActivity extends AppCompatActivity {

    private static final int AUTHING_LOGIN = 0;

    String[] from = {
            "Authing 标准登录",
            "Android 默认风格登录",
            "手机号一键登录",
            "微信",
            "Theragun",
            "阿宝说",
            "Nissan Virtual Key",
            "AppAuth",
            "Authing WebView"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = findViewById(R.id.lv_samples);

        final ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.sample_list_item, from);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((arg0, arg1, arg2, pos) -> {
            if (pos == AUTHING_LOGIN) {
                Intent intent = new Intent(SampleListActivity.this, AuthingDemoLoginActivity.class);
                startActivity(intent);
            } else if (pos == 1) {
                Intent intent = new Intent(SampleListActivity.this, AndroidLoginActivity.class);
                startActivity(intent);
            } else if (pos == 2) {
                Intent intent = new Intent(SampleListActivity.this, OneClickActivity.class);
                startActivity(intent);
            } else if (pos == 3) {
                Intent intent = new Intent(SampleListActivity.this, WechatLoginActivity.class);
                startActivity(intent);
            } else if (pos == 4) {
                Intent intent = new Intent(SampleListActivity.this, TheragunLoginActivity.class);
                startActivity(intent);
            } else if (pos == 5) {
                Intent intent = new Intent(SampleListActivity.this, AbaoActivity.class);
                startActivity(intent);
            } else if (pos == 6) {
                Intent intent = new Intent(SampleListActivity.this, NissanVirtualKeyLoginActivity.class);
                startActivity(intent);
            } else if (pos == 7) {
                Intent intent = new Intent(SampleListActivity.this, AppAuthActivity.class);
                startActivity(intent);
            } else if (pos == 8) {
                Intent intent = new Intent(SampleListActivity.this, AuthingWebViewActivity.class);
                startActivity(intent);
            }
        });
    }
}