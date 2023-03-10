package cn.authing.guard.social.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.social.SocialAuthenticator;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Const;

public class QQ extends SocialAuthenticator {

    private static final String TAG = "QQ";
    public static String appId;
    public static String scope = "get_user_info,list_photo,add_album,list_album,upload_pic,get_vip_rich_info,get_vip_info";
    private Tencent mTencent;
    private BaseUiListener baseUiListener;

    private QQ() {
    }

    public static QQ getInstance() {
        return QQInstanceHolder.mInstance;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mTencent != null) {
            Tencent.onActivityResultData(requestCode, resultCode, data, baseUiListener);
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            if (appId == null && config != null) {
                appId = config.getSocialAppId(Const.EC_TYPE_QQ);
            }
            mTencent = Tencent.createInstance(appId, context.getApplicationContext());
            if (!mTencent.isSessionValid()) {
                baseUiListener = new BaseUiListener(this, context, mTencent, callback);
                mTencent.login((Activity) context, scope, baseUiListener);
            }
        });
    }

    public void logout(Context context) {
        if (mTencent != null) {
            mTencent.logout(context);
        }
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        AuthClient.loginByQQ(authCode, callback);
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        new OIDCClient().loginByQQ(authCode, callback);
    }

    private static final class QQInstanceHolder {
        static final QQ mInstance = new QQ();
    }

    public static class BaseUiListener implements IUiListener {

        private final QQ mInstance;
        private final Context context;
        private final Tencent mTencent;
        private final AuthCallback<UserInfo> callback;

        public BaseUiListener(QQ instance, Context context, Tencent mTencent, AuthCallback<UserInfo> callback) {
            this.mInstance = instance;
            this.context = context;
            this.mTencent = mTencent;
            this.callback = callback;
        }

        @Override
        public void onComplete(Object response) {
            ALog.i(TAG, "Auth onSuccess");
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                mInstance.login(context, accessToken, callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            ALog.e(TAG, "Auth Failed, errorMessage is" + e.errorMessage);
            if (callback != null) {
                callback.call(Const.ERROR_CODE_10013, "Login by QQ failed", null);
            }
        }

        @Override
        public void onCancel() {
            ALog.e(TAG, "Auth Failed, onCancel");
            callback.call(Const.ERROR_CODE_10013, "Login by QQ canceled", null);
        }

        @Override
        public void onWarning(int i) {
            ALog.e(TAG, "Auth Failed, onWarning");
            callback.call(Const.ERROR_CODE_10013, "Login by QQ onWarning", null);
        }

    }

}
