package cn.authing;

import static cn.authing.guard.activity.AuthActivity.OK;
import static cn.authing.guard.activity.AuthActivity.RC_LOGIN;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executor;

import cn.authing.abao.AbaoActivity;
import cn.authing.appauth.AppAuthActivity;
import cn.authing.authenticator.AuthenticatorActivity;
import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.social.handler.Line;
import cn.authing.guard.social.handler.OneClick;
import cn.authing.oneclick.OneClickActivity;
import cn.authing.push.LoginByPushNotificationActivity;
import cn.authing.scan.ScanAuthActivity;
import cn.authing.theragun.TheragunAuthActivity;
import cn.authing.ut.UTActivity;
import cn.authing.webview.AuthingWebViewActivity;
import cn.authing.wechat.WechatAuthActivity;

public class SampleListActivity extends AppCompatActivity {

    private static final int AUTHING_LOGIN = 0;

    private boolean biometric;

    String[] from = {
            "Authing 标准登录",
            "手机号一键登录（Authing UI）",
            "手机号一键登录（纯逻辑）",
            "微信",
            "Theragun",
            "阿宝说",
            "Auth Container",
            "AppAuth",
            "Authing WebView 登录",
            "Authing 自定义 WebView 登录",
            "MFA",
            "登录/注册后用户信息完善",
            "扫码登录",
            "生物二次验证",
            "Authenticator",
            "Login by push notification",
            "谷歌登录",
            "场景测试-api-v2",
            "Line登录"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = findViewById(R.id.lv_samples);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.sample_list_item, from);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((arg0, arg1, arg2, pos) -> {
            if (pos == 7) {//AppAuth
                startActivity(AppAuthActivity.class);
                return;
            } else if (pos == 12) {//扫码登录
                startActivity(ScanAuthActivity.class);
                return;
            } else if (pos == 14) {//Authenticator
                startActivity(AuthenticatorActivity.class);
                return;
            } else if (pos == 16) {//google登录
                Intent intent = new Intent(SampleListActivity.this, SignInActivity.class);
                startActivity(intent);
                return;
            } else if (pos == 17) {//ut-v2
                Intent intent = new Intent(SampleListActivity.this, UTActivity.class);
                startActivity(intent);
                return;
            }

            if (null != Authing.getCurrentUser()) {
                gotoMain();
                return;
            }
            if (pos == 18) {//Line登录
                Authing.setAuthProtocol(Authing.AuthProtocol.EOIDC);
                Line.getInstance().setChannelID(null);
                Line.getInstance().login(this, new AuthCallback<UserInfo>() {
                    @Override
                    public void call(int code, String message, UserInfo data) {
                        Log.e("TAG_登录222","code="+code+";message="+message);
                        if (code == 200) {
                            // 登录成功，data 是用户信息
                        } else {
                            // 登录失败
                            showMultiLineSnackbar(findViewById(android.R.id.content), message);
                        }
                    }
                });
            }else if (pos == AUTHING_LOGIN) {//Authing 标准登录
                AuthFlow authFlow = AuthFlow.start(this);
//                authFlow.setAuthCallback(new AuthFlow.Callback<UserInfo>() {
//                    @Override
//                    public void call(Context context, int code, String message, UserInfo userInfo) {
//                        Log.e("zjh", "code = " + code);
//                    }
//                });
            } else if (pos == 1) {//手机号一键登录（Authing UI）
                Intent intent = new Intent(SampleListActivity.this, OneClickActivity.class);
                startActivityForResult(intent, RC_LOGIN);
            } else if (pos == 2) {//手机号一键登录（纯逻辑）
                // if you want to return refreshToken、idToken、refreshToken
                Authing.setAuthProtocol(Authing.AuthProtocol.EOIDC);
                OneClick oneClick = new OneClick(SampleListActivity.this);
                oneClick.start(((code, message, userInfo) -> {
                    if (code != 200) {
                        Toast.makeText(Authing.getAppContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        gotoMain(userInfo);
                    }
                }));
            } else if (pos == 3) {//微信
                startActivity(WechatAuthActivity.class);
            } else if (pos == 4) {//Theragun
                startActivity(TheragunAuthActivity.class);
            } else if (pos == 5) {//阿宝说
                startActivity(AbaoActivity.class);
            } else if (pos == 6) {//Auth Container
                Authing.setAuthProtocol(Authing.AuthProtocol.EOIDC);
                AuthFlow.start(this);
//                Intent intent = new Intent(SampleListActivity.this, NissanVirtualKeyAuthActivity.class);
//                startActivity(intent);
            } else if (pos == 8) {//Authing WebView 登录
                AuthFlow flow = AuthFlow.startWeb(this);
//                flow.setScope("openid");
                flow.setSkipConsent(true);
            } else if (pos == 9) {//Authing 自定义 WebView 登录
                startActivity(AuthingWebViewActivity.class);
            } else if (pos == 10) {//MFA
                Authing.init(SampleListActivity.this, "61c173ada0e3aec651b1a1d1");
                AuthFlow.start(this);
            } else if (pos == 11) {//登录/注册后用户信息完善
                Authing.init(SampleListActivity.this, "61ae0c9807451d6f30226bd4");
                AuthFlow.start(this);
            } else if (pos == 13) {//生物二次验证
                biometric = true;
                AuthFlow.start(this);
            } else if (pos == 15) {//Login by push notification
                Intent intent = new Intent(SampleListActivity.this, LoginByPushNotificationActivity.class);
                startActivityForResult(intent, RC_LOGIN);
            }
        });
    }
    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(SampleListActivity.this, cls);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("针对Line_登录","requestCode="+requestCode + "\nresultCode="+resultCode + "\ndata="+data);
        Line.getInstance().onActivityResult(requestCode, resultCode, data);

        //登录成功后获取本地用户数据
        if (requestCode == RC_LOGIN && resultCode == OK && data != null) {
            if (biometric) {
                startBiometric();
            } else {
                gotoMain();
            }
        }
    }

    private void startBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "" + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                gotoMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.authing_biometric_title))
                .setSubtitle(getString(R.string.authing_biometric_tip))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
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

    private void gotoMain() {
        //new Push().registerDevice(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    void showMultiLineSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        // 获取 Snackbar 的 TextView 并设置多行显示
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        if (textView != null) {
            textView.setMaxLines(5); // 设置最大行数
        }

        snackbar.show();
    }
}