# 必填字段标题 MandatoryField

## 布局文件使用方式

```xml
<cn.authing.guard.MandatoryField
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

<br>

## 效果如下

![](./images/mandatory.png)

<br>

该控件在文字的左边或者右边添加一个红色的 *

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  asteriskPosition     |    enum    |  红色星号位置。取值：none/left/right   |    right   |
