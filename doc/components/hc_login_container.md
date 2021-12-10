# 登录容器 LoginContainer

## 布局文件使用方式

```xml
<cn.authing.guard.LoginContainer
    app:type="accountPassword"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <cn.authing.guard.AccountEditText
        app:leftIconDrawable="@drawable/ic_authing_user"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    <cn.authing.guard.PasswordEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:leftIconDrawable="@drawable/ic_authing_password"
        app:clearAllEnabled="false"
        android:layout_marginTop="32dp"/>
</cn.authing.guard.LoginContainer>
```

## 效果如下

![](./images/login_container.png)

### 特性：

使用 LoginContainer 作为登录控件的容器，可以帮助 Guard 其他组件感知当前的状态。当登录操作被触发时，比如用户点击了登录按钮，或者在验证码输入框中粘贴了验证码，Guard 就可以自动获得必要信息完成登录操作。

如果不使用 LoginContainer，则需要开发人员手动去监听事件以及手动获取输入框信息

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  type     |    string    |  容器类型，取值范围：accountPassword/phoneCode   |    phoneCode   |

