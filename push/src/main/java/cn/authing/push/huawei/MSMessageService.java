package cn.authing.push.huawei;

import android.os.Bundle;
import android.text.TextUtils;

import com.huawei.hms.push.HmsMessageService;

import cn.authing.guard.util.ALog;

public class MSMessageService extends HmsMessageService {

    private static final String TAG = "MSMessageService";

    @Override
    public void onNewToken(String token, Bundle bundle) {
        // 获取token
        ALog.i(TAG, "have received refresh token " + token);

        // 判断token是否为空
        if (!TextUtils.isEmpty(token)) {
            refreshedTokenToServer(token);
        }
    }

    private void refreshedTokenToServer(String token) {
        ALog.i(TAG, "sending token to server. token:" + token);
        HuaweiPush.sendRegTokenToHMSServer(token);
    }
}
