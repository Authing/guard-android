package cn.authing.guard.push;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.PushData;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.ToastUtil;

public class PushLoginButton extends PrimaryButton {

    private int pushLoginType;
    public static final int TYPE_CONFIRM = 0;
    public static final int TYPE_CANCEL = 1;

    public PushLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public PushLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public PushLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PushLoginButton);
        pushLoginType = array.getInt(R.styleable.PushLoginButton_pushLoginType, 0);
        array.recycle();

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_push_confirm_login);
        }

        if (!((getContext() instanceof AuthActivity))) {
            return;
        }

        setOnClickListener(v -> click());
    }

    private void click() {
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow authFlow = activity.getFlow();
        PushData pushData = null;
        if (authFlow != null) {
            pushData = (PushData) authFlow.getData().get(AuthFlow.KEY_PUSH_DATA);
        }
        String pushCodeId = (pushData != null ? pushData.getRandom() : "");
        if (pushLoginType == TYPE_CONFIRM) {
            AuthClient.changePushCodeStatus(pushCodeId, "CONFIRM", (AuthCallback<JSONObject>) (code, message, data) -> {
                if (code == 200) {
                    next();
                } else {
                    post(() -> ToastUtil.showCenter(getContext(), message));
                }
            });
        } else if (pushLoginType == TYPE_CANCEL) {
            AuthClient.changePushCodeStatus(pushCodeId, "CANCEL", (AuthCallback<JSONObject>) (code, message, data) -> {
                activity.finish();
            });
        }
    }

    protected void next() {
        if (getContext() instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.putExtra(AuthActivity.AUTH_FLOW, flow);
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getPushLoginSuccessLayoutId());
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    public int getPushLoginType() {
        return pushLoginType;
    }

    public void setPushLoginType(int pushLoginType) {
        this.pushLoginType = pushLoginType;
    }
}
