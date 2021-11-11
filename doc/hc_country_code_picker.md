# 国家码选择器 CountryCodePicker

## 布局文件使用方式

```xml
<cn.authing.guard.CountryCodePicker
    android:id="@+id/ccp"
    android:layout_width="wrap_content"
    android:layout_height="match_parent"
    android:textSize="17sp"
    android:textColor="#888"
    app:showCountryName="true"
    app:showRightArrow="false"
    android:paddingLeft="24dp"/>
```

默认显示中国大陆。点击会弹出一个包含所有国家电话码列表的 Dialog，用户选择后会自动更新显示内容

## 效果如下

![](./gif/ccp_flag.gif)

<br>

## 特性

通过使用 Emoji 显示国旗，避免引入大量图片资源，从而避免 guard 库 size 过大

<br>

## xml 属性列表

| 属性名                     | 类型 | 说明 | 默认值 |
| ----------------------- |:--------:| :------:| :-----: |
|  showFlag     |    boolean    |  是否显示国家国旗   |    false   |
|  showCountryName     |    boolean    |  是否显示国家名称   |    false   |
|  showRightArrow     |    boolean    |   是否显示右边的箭头   |    true   |

<br>

## API

**getCountry**

```java
public Country getCountry()
```

返回用户选择的国家对象，包含名称，简称，国家码。默认为中国大陆

使用示例：

```java
CountryCodePicker countryCodePicker = findViewById(R.id.ccp);
Country country = countryCodePicker.getCountry();
```
