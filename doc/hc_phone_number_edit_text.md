# 手机号输入框 PhoneNumberEditText

## 布局文件使用方式

```xml
<cn.authing.guard.PhoneNumberEditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

## 效果如下

![](./images/pnet_normal.png)

<br>

### 特性一：
提示语支持以下模式：

* *normal* 当用户输入时，提示语消失；当输入框没有内容时，提示语显示。
* *animated* 当输入框获得焦点时，提示语移动至顶部；当失去焦点时，提示语移动至输入框内。

![](./gif/animated_hint.gif)

可以通过 *hint_mode* 属性来控制，如：
```xml
<cn.authing.guard.PhoneNumberEditText
    app:hintMode="animated" />
```

<br>

### 特性二：
支持按某种长度格式分隔数字。比如中国的手机号码一般按照 344 长度来分隔

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  hintMode     |    string    |  normal/animated   |    normal   |
|  leftIconDrawable     |    reference    |   输入框左边图标   |    @drawable/ic_authing_cellphone   |
|  dividerPattern     |    integer    |   分隔数字的长度模式，如 344 表示前三个数字后有一个空格，然后4个数字之后再有一个空格   |    344   |
