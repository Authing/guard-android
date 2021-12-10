# 密码输入框 PasswordEditText

继承至 [EditTextLayout](./hc_edit_text_layout.md)

## 布局文件使用方式

```xml
<cn.authing.guard.PasswordEditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

## 效果如下

![](./images/pet_normal.png)

<br>

### 特性：

* 根据 authing 后台设置，校验密码强度。
* 可以通过 *toggleEnabled* 来显示/隐藏密码明文

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  toggleEnabled     |    boolean    |   当值为 true 且输入框有内容时，<br>输入框右侧会出现一个显示/隐藏密码明文的按钮   |    true   |
