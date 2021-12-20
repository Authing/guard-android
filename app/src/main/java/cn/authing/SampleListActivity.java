package cn.authing;

import static cn.authing.guard.activity.AuthActivity.OK;
import static cn.authing.guard.activity.AuthActivity.RC_LOGIN;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.authing.abao.AbaoActivity;
import cn.authing.appauth.AppAuthActivity;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.oneclick.OneClick;
import cn.authing.nissan.NissanVirtualKeyLoginActivity;
import cn.authing.oneclick.OneClickActivity;
import cn.authing.theragun.TheragunLoginActivity;
import cn.authing.webview.AuthingWebViewActivity;
import cn.authing.wechat.WechatLoginActivity;

public class SampleListActivity extends AppCompatActivity {

    private static final int AUTHING_LOGIN = 0;

    String[] from = {
            "Authing 标准登录",
            "手机号一键登录（Authing UI）",
            "手机号一键登录（纯逻辑）",
            "微信",
            "Theragun",
            "阿宝说",
            "Nissan Virtual Key",
            "AppAuth",
            "Authing WebView",
            "MFA",
            "登录/注册后用户信息完善",
            "Android 默认风格登录",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = findViewById(R.id.lv_samples);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.sample_list_item, from);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((arg0, arg1, arg2, pos) -> {
            if (pos == AUTHING_LOGIN) {
                AuthFlow.start(this);
            } else if (pos == 1) {
                Intent intent = new Intent(SampleListActivity.this, OneClickActivity.class);
                startActivityForResult(intent, RC_LOGIN);
            } else if (pos == 2) {
                OneClick oneClick = new OneClick(SampleListActivity.this);
                oneClick.start(((code, message, userInfo) -> {
                    gotoMain(userInfo);
                }));
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
            } else if (pos == 9) {
                Authing.init(SampleListActivity.this, "610932784e4bb719b5787ad7");
                AuthFlow.start(this);
            } else if (pos == 10) {
                Authing.init(SampleListActivity.this, "61ae0c9807451d6f30226bd4");
                AuthFlow.start(this);
            } else if (pos == 11) {
                Intent intent = new Intent(SampleListActivity.this, AndroidLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == OK && data != null) {
            Intent intent = new Intent(this, MainActivity.class);
            UserInfo userInfo = (UserInfo) data.getSerializableExtra("user");
            intent.putExtra("user", userInfo);
            startActivity(intent);
        }
    }

    private void gotoMain(UserInfo data) {
        if (data != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", data);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

//    private static void gotoMain(Context context, int code, String message, UserInfo data) {
//        if (code != 200) {
//            return;
//        }
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra("user", data);
//        context.startActivity(intent);
//    }
}