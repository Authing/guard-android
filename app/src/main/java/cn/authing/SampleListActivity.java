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
import android.widget.Toast;

import cn.authing.abao.AbaoActivity;
import cn.authing.appauth.AppAuthActivity;
import cn.authing.guard.Authing;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.oneclick.OneClick;
import cn.authing.nissan.NissanVirtualKeyAuthActivity;
import cn.authing.oneclick.OneClickActivity;
import cn.authing.theragun.TheragunAuthActivity;
import cn.authing.webview.AuthingWebViewActivity;
import cn.authing.wechat.WechatAuthActivity;

public class SampleListActivity extends AppCompatActivity {

    private static final int AUTHING_LOGIN = 0;

    String[] from = {
            "Authing 标准登录",
            "手机号一键登录（Authing UI）",
            "手机号一键登录（纯逻辑）",
            "微信",
            "Theragun",
            "阿宝说",
            "Auth Container",
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
                    if (code != 200) {
                        Toast.makeText(Authing.getAppContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        gotoMain(userInfo);
                    }
                }));
            } else if (pos == 3) {
                Intent intent = new Intent(SampleListActivity.this, WechatAuthActivity.class);
                startActivity(intent);
            } else if (pos == 4) {
                Intent intent = new Intent(SampleListActivity.this, TheragunAuthActivity.class);
                startActivity(intent);
            } else if (pos == 5) {
                Intent intent = new Intent(SampleListActivity.this, AbaoActivity.class);
                startActivity(intent);
            } else if (pos == 6) {
                AuthFlow flow = AuthFlow.start(this);
                flow.setAuthProtocol(AuthContainer.AuthProtocol.EOIDC);
//                Intent intent = new Intent(SampleListActivity.this, NissanVirtualKeyAuthActivity.class);
//                startActivity(intent);
            } else if (pos == 7) {
                Intent intent = new Intent(SampleListActivity.this, AppAuthActivity.class);
                startActivity(intent);
            } else if (pos == 8) {
                Intent intent = new Intent(SampleListActivity.this, AuthingWebViewActivity.class);
                startActivity(intent);
            } else if (pos == 9) {
                Authing.init(SampleListActivity.this, "61c173ada0e3aec651b1a1d1");
                AuthFlow.start(this);
            } else if (pos == 10) {
                Authing.init(SampleListActivity.this, "61ae0c9807451d6f30226bd4");
                AuthFlow.start(this);
            } else if (pos == 11) {
                Intent intent = new Intent(SampleListActivity.this, AndroidAuthActivity.class);
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
            AuthFlow.showUserProfile(this);
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