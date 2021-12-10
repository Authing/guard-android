# 注册方法切换器 RegisterMethodTab

## 布局文件使用方式

```xml
<cn.authing.guard.RegisterMethodTab
    android:layout_width="match_parent"
    android:layout_height="52dp" />
```

## 效果如下

![](./images/register_method_tab.png)

### 特性：

根据 Authing 后台注册方式的配置动态显示注册方法。同时将 “默认注册方式” 放到最左边。

![](./images/login_methods.png)

>注意：切换的时候，该控件会遍历查找 [RegisterContainer](./hc_register_container.md) 来完成切换。如果开发者未使用 RegisterContainer，则需要自行处理注册方式的切换

<br>
