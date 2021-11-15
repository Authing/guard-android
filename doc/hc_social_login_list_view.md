# 社会化登录列表 SocialLoginListView

## 布局文件使用方式

```xml
<cn.authing.guard.social.SocialLoginListView
    android:id="@+id/lv_social"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginBottom="64dp"
    app:layout_constraintBottom_toBottomOf="parent"/>
```

## 效果如下

![](./images/social_login_list_view.png)

### 特性一：
可以通过 *src* 属性，任意组合需要支持的第三方登录平台。顺序和属性值一致。

<br>

### 特性二：
可以通过 *orientation* 指定方向，默认水平方向

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  src     |    string    |  支持的平台：wechat\|wecom\|alipay，用 \| 隔开   |    wechat\|wecom   |
|  orientation（建设中）     |    string    |   列表方向。horizontal或者vertical   |    horizontal   |

<br>

## API

**setOnLoginListener**

```java
public void setOnLoginListener(Callback<UserInfo> callback)
```

第三方登录完成后会触发 callback 回调。如果登录成功，user 为 authing 用户信息。其中包含认证源，可以通过下面方式获得：

```java
String src = user.getThirdPartySource();
```
其值和 src 属性定义一致

使用示例：

```java
SocialLoginListView lv = findViewById(R.id.lv_social);
lv.setOnLoginListener((code, message, user) -> {
    if (code == 200) {
        Intent intent = new Intent(AuthingLoginActivity.this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
});
```