# 输入框 EditTextLayout

## 布局文件使用方式

基类。不建议直接使用，请使用子类。

<br>

### 特性一：

提示语支持以下模式：

* *normal* 当用户输入时，提示语消失；当输入框没有内容时，提示语显示。
* *animated* 当输入框获得焦点时，提示语移动至顶部；当失去焦点时，提示语移动至输入框内。

![](./gif/animated_hint.gif)

可以通过 *hint_mode* 属性来控制，如：
```xml
<cn.authing.guard.AccountEditText
    app:hintMode="animated" />
```

<br>

### 特性二：
默认情况下，当用户输入了任何内容，右侧会出现“全部删除”按钮，当输入框没有内容时，“全部删除”按钮会自动消失。

![](./images/aet_clear_all.png)

如果不需要“全部删除”按钮，可以通过 *clearAllEnabled* 属性来控制。如：

```xml
<cn.authing.guard.AccountEditText
    app:clearAllEnabled="false" />
```

<br>

### 特性三：

通过 *errorEnabled* 属性，可以显示错误信息。

![](./images/edittext_layout_error.png)

<br>

### 特性四：

该控件可以根据 authing 后台配置自动校验用户输入内容的合法性，节约开发者手动实现内容校验的工作量。

<br>

### 特性五：
根据 authing 后台设置，提示语可动态显示，无需手动去判断。如后台只允许邮箱登录，则提示语为：请输入邮箱

<br>

### 特性六：
开发者可以通过获取内部的原生 EditText 对象来完成任意原生操作：

```java
AccountEditText accountEditText = findViewById(R.id.aet);
EditText editText = accountEditText.getEditText();
```

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  hintMode     |    string    |  normal/animated   |    normal   |
|  hintColor     |    reference\|color    |  提示语颜色   |    跟随系统   |
|  leftIconDrawable     |    reference    |   输入框左边图标   |    null   |
|  clearAllEnabled     |    boolean    |   若为 false，则不显示全部删除按钮   |    true   |
|  errorEnabled     |    boolean    |   若为 true，当输入内容有误，自动显示错误信息   |    false   |
