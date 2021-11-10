# 使用 Guard 自带原生界面快速接入 Authing

1. 引入依赖

```groovy
implementation 'cn.authing:guard:+'
```

2. 在应用启动（如 App.java）里面调用：

```java
Authing.init(appContext, "your_authing_app_id");
```

3. 在 App 启动界面，如闪屏，调用

```java
Authing.requestPublicConfig((ok, data) -> continue_your_app_flow());
```

4. 继承 AuthingLoginActivity，处理登录结果

```java
public class AuthingDemoLoginActivity extends AuthingLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginButton btn = findViewById(R.id.btn_login);
        if (btn != null) {
            btn.setOnLoginListener((ok, data) -> {
                if (ok) {
                    Intent intent = new Intent(AuthingDemoLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }

        SocialLoginListView lv = findViewById(R.id.lv_social);
        if (lv != null) {
            lv.setOnLoginListener((ok, data) -> {
                if (ok) {
                    Intent intent = new Intent(AuthingDemoLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
```

这样就实现了原生的登录界面：

![](./images/authing_login.png)