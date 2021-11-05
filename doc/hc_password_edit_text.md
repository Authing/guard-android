# 密码输入框 PasswordEditText

## 布局文件使用方式

```xml
<cn.authing.guard.PasswordEditText
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    android:layout_marginStart="24dp"
    android:layout_marginEnd="24dp"
    app:layout_constraintTop_toTopOf="parent"/>
```

## 效果如下

![](./images/pet_normal.png)

### 特性一：
和账号输入框 AccountEditText 一样，提示语支持三种模式：

* *normal* 当用户输入时，提示语消失；当输入框没有内容时，提示语显示。
* *animated* 当输入框获得焦点时，提示语移动至顶部；当失去焦点时，提示语移动至输入框内。
* *fixed* 提示语总是固定在顶部。

可以通过 *hint_mode* 属性来控制，如：
```xml
<cn.authing.guard.PasswordEditText
    app:hintMode="animated" />
```

<br>

### 特性二：
根据 authing 后台设置，校验密码强度。

<br>

### 特性三：
可以通过 *toggleEnabled* 来显示/隐藏密码明文

<br>

### 特性四：
和账号输入框 AccountEditText 一样，开发者可以通过获取内部的原生 EditText 对象来完成任意原生操作：

```java
PasswordEditText passwordEditText = findViewById(R.id.aet);
EditText editText = passwordEditText.getEditText();
```

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  hintMode（建设中）     |    string    |  normal/animated/fixed   |    normal   |
|  toggleEnabled     |    boolean    |   当值为 true 且输入框有内容时，<br>输入框右侧会出现一个显示/隐藏密码明文的按钮   |    true   |
