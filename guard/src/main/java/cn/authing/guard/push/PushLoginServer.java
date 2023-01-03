package cn.authing.guard.push;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.Authing;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.PushData;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.util.Util;

/**
 * 继承 GTIntentService 接收来自个推的消息，所有消息在主线程中回调，如果注册了该服务，则务必要在 AndroidManifest 中声明，否则无法接受消息
 */
public class PushLoginServer extends GTIntentService {

    private static final String TAG = "PushLoginServer";
    private String mMid = "";

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid");
    }

    /**
     * 此方法用于接收和处理透传消息。透传消息个推只传递数据，不做任何处理，客户端接收到透传消息后需要自己去做后续动作处理，如通知栏展示、弹框等。
     * 如果开发者在客户端将透传消息创建了通知栏展示，建议将展示和点击回执上报给个推。
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        byte[] payload = msg.getPayload();
        String data = new String(payload);
        Log.d(TAG, "onReceiveMessageData");

        //taskid和messageid字段，是用于回执上报的必要参数。详情见下方文档“6.2 上报透传消息的展示和点击数据”
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();

        PushData pushData = parseData(data);
        if (mMid.equals(pushData.getMid())) {
            return;
        }
        mMid = pushData.getMid();
        goToPushLoginPage(context, pushData);
    }

    private PushData parseData(String data){
        PushData pushData = new PushData();
        try {
            JSONObject result = new JSONObject(data);
            if (result.has("userId")) {
                pushData.setUserId(result.getString("userId"));
            }
            if (result.has("userPoolId")) {
                pushData.setUserPoolId(result.getString("userPoolId"));
            }
            if (result.has("appId")) {
                pushData.setAppId(result.getString("appId"));
            }
            if (result.has("mid")) {
                pushData.setMid(result.getString("mid"));
            }
            if (result.has("type")) {
                pushData.setType(result.getString("type"));
            }
            if (result.has("content")) {
                JSONObject content = result.getJSONObject("content");
                if (content.has("random")) {
                    pushData.setRandom(content.getString("random"));
                }
                if (content.has("scene")) {
                    pushData.setScene(content.getString("scene"));
                }
                if (content.has("account")) {
                    pushData.setAccount(content.getString("account"));
                }
                if (content.has("app")) {
                    JSONObject app = content.getJSONObject("app");
                    if (app.has("appName")) {
                        pushData.setAppName(app.getString("appName"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pushData;
    }

    private void goToPushLoginPage(Context context, PushData pushData) {
        AuthFlow authFlow = new AuthFlow();
        authFlow.getData().put(AuthFlow.KEY_PUSH_DATA, pushData);
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, authFlow);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, authFlow.getPushLoginLayoutId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.d(TAG, "onReceiveClientId");
        if (clientid == null) {
            return;
        }
        if (Authing.getCurrentUser() != null) {
            Util.pushCid(context);
        }
    }

    // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState");
    }

    // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onNotificationMessageArrived");
    }

    // 通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        Log.d(TAG, "onNotificationMessageArrived");
    }

    // 通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        Log.d(TAG, "onNotificationMessageClicked");
    }


    /**
     * 上报个推透传消息的展示回执。如果透传消息本地创建通知栏消息“展示”了，则调用此方法上报。
     */
    public boolean pushGtShow(Context context, String taskid, String messageid) {
        int gtactionid = 60001;//gtactionid传入60001表示个推渠道消息展示了
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, gtactionid);
        return result;
    }

    /**
     * 上报个推透传消息的点击回执。如果透传消息本地创建通知栏消息被“点击”了，则调用此方法上报。
     */
    public boolean pushGtClick(Context context, String taskid, String messageid) {
        int gtactionid = 60002;//gtactionid传入60002表示个推渠道消息点击了
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, gtactionid);
        return result;
    }
}

