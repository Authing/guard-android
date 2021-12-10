# 注册按钮 RegisterButton

## 布局文件使用方式

```xml
<cn.authing.guard.RegisterButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

## 效果如下

![](./images/register_button.png)

### 特性一：
当用户点击注册按钮时，该组件会向 Authing 服务器发起注册请求，然后通过 callback 回传注册结果。参考 setOnRegisterListener API

<br>

### 特性二：
标准的错误处理已经内置到组件里面。当发生错误时，该组件会在父控件的所有子控件中查找 ErrorTextView 组件，并展示错误信息。如果视图结构里面没有 ErrorTextView，则忽略错误。

<br>

### 特性三：
当组件处于按下状态，自动添加蒙版

<br>

### 特性四：
当点击后，自动播放 Loading 动画，并禁用该按钮

<br>

### 特性五：
自动触发 MFA，用户信息补全

<br>

## API

**setOnRegisterListener**

```java
public void setOnRegisterListener(AuthCallback<UserInfo> callback)
```

注册完成后会触发 callback 回调。如果注册成功，user 为 authing 用户信息。使用示例：

```java
RegisterButton btn = findViewById(R.id.btn);
btn.setOnRegisterListener((code, message, userInfo) -> {
    if (code == 200) {
        
    }
});
```