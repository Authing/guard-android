# 接入步骤

1. 添加依赖

```groovy
implementation 'cn.authing:guard:+'
implementation 'io.github.yidun:quicklogin:3.1.1'
```

2. 在应用启动（如 App.java）里面初始化易盾业务 ID 以及 Authing 应用 ID：

```java
OneClick.bizId = "your_yidun_business_id";
Authing.init(this, "your_authing_app_id"); // 'this' is your Application or initial activity
```

3. 发起认证。有以下几种场景：

* 推荐使用 Authing [语义化编程模型](./design.md)，只需在布局文件里面放置一个 OneClickAuthButton，如：

 ```xml
 <cn.authing.guard.oneclick.OneClickAuthButton
    android:background="@drawable/authing_button_background"
    android:textColor="@color/white"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
 ```

认证结果的返回方式为 Guard 标准返回方式，参考 [这里](../index_zh.md)。也就是说，如果之前已经实现了基础的登录界面，添加“手机号一键登录”能力就只需要放置一个语义化的按钮。

* 若需要自己处理逻辑，则可以调用：

```java
OneClick oneClick = new OneClick(this); // 'this' is your current activity
oneClick.start(((code, message, userInfo) -> {
    // logged in
}));
```

* 若需要自定义 UI，首先参考 [易盾文档](https://gitee.com/netease_yidun/quickpass-android-demo) 生成 UnifyUiConfig 对象，然后调用：

```java
UnifyUiConfig config = new UnifyUiConfig.Builder()
                 // build your config here
                .build(this);
OneClick oneClick = new OneClick(this); // 'this' is your current activity
oneClick.start(config, ((code, message, userInfo) -> {
    // logged in
}));
```