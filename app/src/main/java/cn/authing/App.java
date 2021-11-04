package cn.authing;

import cn.authing.guard.Authing;
import cn.authing.guard.social.WeCom;
import cn.authing.guard.social.Wechat;

public class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // social public info has to be set manually
        Wechat.appId = "wx1cddb15e280c0f67";

        WeCom.corpId = "ww2fe68893d538b6c1";
        WeCom.agentId = "1000003";
        WeCom.schema = "wwauth2fe68893d538b6c1000003";

        Authing.init(getApplicationContext(), "60caaf41df670b771fd08937");
    }
}
