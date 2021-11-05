package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.authing.oneclick.OneClickActivity;
import cn.authing.wechat.WechatLoginActivity;

public class SampleListActivity extends AppCompatActivity {

    String[] from = {
            "Authing 标准登录",
            "Android 默认风格登录",
            "一键登录",
            "微信"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = (ListView) findViewById(R.id.lv_samples);

        final ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.sample_list_item, from);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((arg0, arg1, arg2, pos) -> {
            if (pos == 0) {
                Intent intent = new Intent(SampleListActivity.this, AuthingLoginActivity.class);
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
            }
        });
    }
}