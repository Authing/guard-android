# 登录按钮 LoginButton

## 布局文件使用方式

```xml
<!-- 推荐高度为 44dp -->
<cn.authing.guard.LoginButton
    android:layout_width="0dp"
    android:layout_height="44dp"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    android:layout_marginStart="48dp"
    android:layout_marginEnd="48dp"
    app:layout_constraintTop_toTopOf="parent"/>
```

## 效果如下

![](./images/btn_login_normal.png)

### 特性一：
当用户点击登录按钮时，该组件会向 Authing 服务器发起登录请求，然后通过 callback 回传登录结果。使用示例：

```java
LoginButton btn = findViewById(R.id.btn_login);
btn.setOnLoginListener((ok, data) -> {
    if (ok) {
        // 登录成功，data 为 UserInfo 对象
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", data);
        startActivity(intent);
    } else {
        // 登录失败 
    }
});
```

<br>

### 特性二：
标准的错误处理已经内置到组件里面，如用户名密码错误，账号已锁定，网络异常等。当发生错误时，该组件会在父控件的所有子控件中查找 ErrorTextView 组件，并展示错误信息。如果视图结构里面没有 ErrorTextView，则忽略错误。

<br>

### 特性三：
当组件处于按下状态，自动添加蒙版

<br>

### 特性四：
当输入框内容不合法，disable 按钮

<br>
