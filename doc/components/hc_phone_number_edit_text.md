# 手机号输入框 PhoneNumberEditText

继承至 [EditTextLayout](./hc_edit_text_layout.md)

## 布局文件使用方式

```xml
<cn.authing.guard.PhoneNumberEditText
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

## 效果如下

![](./images/pnet_normal.png)

<br>

### 特性：
支持按某种长度格式分隔数字。比如中国的手机号码一般按照 344 长度来分隔

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  dividerPattern     |    integer    |   分隔数字的长度模式，如 344 表示前三个数字后有一个空格，然后4个数字之后再有一个空格   |    344   |
