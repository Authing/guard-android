# 邮箱验证码 MFA 按钮 MFAEmailButton

## 布局文件使用方式

```xml
<cn.authing.guard.mfa.MFAEmailButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/authing_button_background"
    android:textColor="#FFF" />
```

<br>

### 特性：

点击后，自动触发邮箱验证码 MFA。需要和以下控件配合使用

[EmailEditText](./hc_email_edit_text.md)

[VerifyCodeEditText](./hc_verify_code_edit_text.md)
