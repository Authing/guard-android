# Authing Android Guard

Authing android guard 是一个面向身份认证领域的业务组件库，该组件库将复杂的认证系统语义化，标准化。通过使用该组件库，业务 App 可以极速实现认证流程。相比手动实现，效率提升 x10。

<br>

## 快速开始

1. 引入依赖

```groovy
implementation 'cn.authing:guard:+'
```

2. 在应用启动（如 App.java）里面调用：

```java
Authing.init(this, "your_authing_app_id"); // 'this' is your Application or initial activity
```

3. 在需要启动认证流程的 Activity（如闪屏）上，调用：

```java
AuthFlow.start(this); // 'this' is current activity
```

这种方式会使用 Authing 标准 UI，如下图：

![](./images/authing_login.png)

4. 通过 onActivityResult 拿到认证数据：

```java
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
```

若想自定义界面和流程，首先为每个界面编写 Layout，然后只需要将第 3 步替换为：

```java
// replace layouts with your customized layouts
AuthFlow.start(this, R.layout.activity_login_authing)
        .setRegisterLayoutId(R.layout.activity_register_authing)
        .setForgotPasswordLayoutId(R.layout.activity_authing_forgot_password)
        .setResetPasswordByEmailLayoutId(R.layout.activity_authing_reset_password_by_email)
        .setResetPasswordByPhoneLayoutId(R.layout.activity_authing_reset_password_by_phone);
```

这就是所有代码！之所以能如此简单，是因为 Guard 内部使用了 [基于语义化思想的编程模型](./topics/design.md)。通过该模型，我们只需要 **声明** 认证流程就可以了。

<br>

## 组件使用指南

### Widgets

[AppLogo](./hc_app_logo.md)

[AppName](./hc_app_name.md)

[AccountEditText](./hc_account_edit_text.md)

[PasswordEditText](./hc_password_edit_text.md)

[LoginButton](./hc_login_button.md)

[PhoneNumberEditText](./hc_phone_number_edit_text.md)

[VerifyCodeEditText](./hc_verify_code_edit_text.md)

[CountryCodePicker](./hc_country_code_picker.md)

[ErrorTextView](./hc_error_text_view.md)

### 聚合控件

[LoginMethodTab](./hc_login_method_tab.md)

[LoginContainer](./hc_login_container.md)

[PrivacyConfirmBox](./hc_privacy_confirm_box.md)

[SocialLoginListView](./hc_social_login_list_view.md)

<br>

接下来，

### [进一步了解 Guard 背后的计算哲学，以及其他高级功能](./topics/index.md)


