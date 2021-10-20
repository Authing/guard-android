# Authing Android Guard

Authing android guard 是一个面向身份认证领域的业务组件库，该组件库将复杂的认证系统语义化，标准化。通过使用该组件库，业务 App 可以极速搭建登录/注册页面。相比手动实现，效率提升 x10。

<br>

## 快速开始

最快的接入方式是使用我们海量的模板。我们深入调查了业界主流 App 登录界面，通过我们 Guard 组件，预置了 100+ 基于业务（行业）的登录/注册界面模板。开发这只需要选择自己的行业，拷贝对应的模板，即可在 10 分钟内实现复杂的，完整的认证流程。

步骤一：添加依赖

``` gradle
implementation 'cn.authing:guard:1.0.0'
```

步骤二：在本项目的 app/src/main/res/layout 目录下选择适合自己 App 的模板布局文件，如：

![](./doc/images/templates.png)

步骤三：在自己项目的登录 Activity 里面加载对应布局模板文件

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.starbucks_login);
}
```

步骤四：在工程的 themes.xml 里面修改 App 主色调

```xml
<item name="colorPrimary">your_primary_color</item>
```

<br>

## 组件使用指南

[AccountEditText](./doc/hc_account_edit_text.md)

[PasswordEditText](./doc/uc.md)

[LoginButton](./doc/uc.md)