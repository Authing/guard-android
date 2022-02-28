# 重置密码按钮 ResetPasswordButton

## 布局文件使用方式

```xml
<cn.authing.guard.ResetPasswordButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

## 效果如下

![](./images/reset_password_button.png)

### 特性：
当用户点击时，如果 Auth Flow 里面已经有必要的信息，如邮箱地址和验证码，该组件会向 Authing 服务器发起重置密码请求；如果 Auth Flow 里面信息不全，则进入下一个页面。页面通过下面接口设置。

AuthFlow.setForgotPasswordLayoutId

AuthFlow.setResetPasswordByEmailLayoutId

AuthFlow.setResetPasswordByPhoneLayoutId
