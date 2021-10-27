# 企业微信登录

集成企业微信需要三个主要步骤：
* 在企业微信管理控制台进行配置
* 在 Authing 管理控制台进行配置
* 集成 Android SDK

<br>

## 在企业微信管理控制台的操作步骤
1. 获取企业 ID（CorpID）

![](./images/wecom/1.png)

2. 获取 AgentID 和 Secret

你需要先在 [应用管理 - 应用管理](https://work.weixin.qq.com/wework_admin/frame#apps) 创建一个自建应用

![](./images/wecom/2.png)

创建完成之后，你可以获取该应用的 AgentId 和 Secret：

![](./images/wecom/3.png)

3. 启用企业微信授权登录

在应用详情页，点击设置企业微信授权登录：

![](./images/wecom/4.png)

启用之后，再次点击 “已启用” 按钮，进入配置界面，填入 App 签名的 MD5 码和包名：

![](./images/wecom/5.png)

> 特别注意：这里的签名 MD5 不能包含冒号

<br>

## 在 Authing 管理控制台的操作步骤

<br>

## 集成 Android SDK 步骤

1. 下载 <a href="attachment/wechatwork.jar" target="_blank">企业微信 SDK</a>
2. 将 jar 包拷贝到工程的 libs 文件夹下面
3. 设置依赖：
```groovy
implementation 'cn.authing:guard:1.0.1'
implementation files('libs/wechatwork.jar')
```
4. 在应用启动的时候初始化：
```java
// appId 是 authing 的应用 id，可以在 authing 控制台里面获取
Authing.init(context, appId);
```
5. 调用 Authing 企业微信登录 API：
```java
import cn.authing.guard.social.WeCom;

WeCom.login(appContext, ((ok, data) -> {
    if (ok) {
        // 登录成功，data 是用户信息，里面有 accessToken
    } else {
        // 登录失败
    }
}));
```