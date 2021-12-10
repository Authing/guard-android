# Recovery MFA 按钮 MFARecoveryButton

OTP 是 One Time Password 的缩写

## 布局文件使用方式

```xml
<cn.authing.guard.mfa.MFARecoveryButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/authing_button_background"
    android:textColor="#FFF" />
```

<br>

### 特性：

点击后，自动触发 Recovery Code MFA。需要和以下控件配合使用

[RecoveryCodeEditText](./hc_recovery_code_edit_text.md)
