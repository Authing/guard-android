# TOTP SDK 接入步骤

> 安卓系统最低版本要求：7.0 （API Level 24）

## 添加依赖

```groovy
implementation 'cn.authing:otp:+'
```

## 绑定帐号

根据 OTP 规范，绑定帐号是通过解析类似这样的 URI 完成的：

```
otpauth://totp/{your_account}?secret={your_secret}
```

这个 URI 可以通过手动输入，也可以通过扫描二维码获取。当拿到这样的 URI 之后，调用下面接口绑定帐号：

```java
public class TOTP {
    public static String bind(Context context, String data)
}
```

示例：

```java
String data = "otpauth://totp/GuardPool:maolongdong%40gmail.com?secret=GN4XCFDLDY4FWMQM&period=30&digits=6&algorithm=SHA1&issuer=GuardPool";
TOTP.bind(this, data); // this is your context
```

## TOTP 管理

我们提供了 TOTP 管理，支持 TOTP 列表展示、倒计时、本地数据库存储、删除、云端备份（需要使用 Authing 帐号登录）

效果图：

<img src="./images/totp.png" alt="drawing" width="400"/>

该界面通过 Fragment 展示，App 只需要引用即可。以 layout 为例：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <fragment
        android:id="@+id/fragment_authenticator"
        android:name="cn.authing.otp.AuthenticatorFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

> 当 App 调用 TOTP.bind(this, data) 时，SDK 会在本地数据库保存 TOTP 数据，然后在 AuthenticatorFragment 类里面，我们会读取本地数据库，所以 App 无需进行任何 UI 操作。 