# 自动登录

一般来说，移动端 App 登录之后，除非帐号出现安全风险，就不会再提示用户登录。早期的 App 通过记住用户名和密码来实现自动登录。出于安全考虑，苹果在引入 Keychain 之前的一段时间是不允许记住密码的，因为密码在越狱的手机上可以被黑客拿到，即使有加密。另外一个问题是，现代的认证流程不提倡使用密码，对于使用手机号+验证码或者生物识别的应用无法通过记住密码来实现自动登录。

Guard 的自动登录通过记住 token 来完成。在闪屏页，调用：

```java
Authing.autoLogin((code, message, userInfo) -> {});
```

如果自动登录成功，可以通过下面代码获取用户信息：

```java
Authing.getCurrentUser()
```

考虑到一般的移动闪屏页还需要展示品牌 Logo，闪屏页会有一个最短显示时间。逻辑是：

* 当网络比较慢时会等待网络返回

* 当网络很快时，至少停留 x 秒

* 如果自动登录失败，比如用户首次使用 App，或者 token 过期，则需要跳转到登录界面。

结合过往经验，以下是包含 *自动登录/品牌展示/跳转登录* 逻辑的完整闪屏页代码。其中 MainActivity 需要被替换成应用 App 登录成功后的主页。

```java
import static cn.authing.guard.activity.AuthActivity.OK;
import static cn.authing.guard.activity.AuthActivity.RC_LOGIN;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;

public class SplashActivity extends AppCompatActivity {

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(()-> next(1), 1000);

        Authing.autoLogin((code, message, userInfo) -> next(2));
    }

    private void next(int f) {
        flag |= f;

        // both condition meets
        if (flag == 3) {
            Intent intent;
            if (Authing.getCurrentUser() != null) {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", Authing.getCurrentUser());
                startActivity(intent);
                finish();
            } else {
                AuthFlow.start(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == OK && data != null) {
            Intent intent = new Intent(this, MainActivity.class);
            UserInfo userInfo = (UserInfo) data.getSerializableExtra("user");
            intent.putExtra("user", userInfo);
            startActivity(intent);
            finish();
        }
    }
}
```