# 获取验证码按钮 GetVerifyCodeButton

## 布局文件使用方式

```xml
<cn.authing.guard.GetVerifyCodeButton
    android:id="@+id/btn_gvc"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

## 效果如下

![](./images/vcet_normal.png)

### 特性：

* 点击发送验证码后，播放加载动画
* 点击发送验证码后，按钮禁用
* 发送成功后进入倒计时
* 发送失败，若存在 ErrorTextView 类型的控件，将错误信息传给它

![](./images/vcet_count_down.png)

<br>

![](./images/get_vc_loading.png)

<br>

## API

**startCountDown**

```java
public void startCountDown()
public void startCountDown(int cd) // 自定义倒计时时间。单位：秒
```

立即触发倒计时。当采用分段式认证的时候，获取验证码的动作发生在前一个页面，进入输入验证码页面的时候需要立即触发倒计时：

```java
GetVerifyCodeButton gcb = findViewById(R.id.gcb);
gcb.startCountDown();
```
<br>

**setCountDownTip**

```java
public void setCountDownTip(String format)
```

设置倒计时文本格式。采用 Android 风格的占位符，%1 为参数，$d 表示参数类型为整数。使用示例：

```java
GetVerifyCodeButton gcb = findViewById(R.id.gcb);
gcb.setCountDownTip("%1$d"); // 纯数字。显示为：60
gcb.setCountDownTip("%1$d 秒后重新获取验证码"); // 显示为：60 秒后重新获取验证码
```

>注意，如果要立即触发倒计时，先调用该函数再调用 startCountDown，否则第一秒会显示默认的 Tip 文本