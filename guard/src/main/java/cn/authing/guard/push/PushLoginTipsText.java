package cn.authing.guard.push;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.PushData;
import cn.authing.guard.flow.AuthFlow;

public class PushLoginTipsText extends AppCompatTextView {

    public PushLoginTipsText(@NonNull Context context) {
        this(context, null);
    }

    public PushLoginTipsText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PushLoginTipsText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_text_gray));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        if (!((getContext() instanceof AuthActivity))) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow authFlow = activity.getFlow();
        PushData pushData = null;
        if (authFlow != null) {
            pushData = (PushData) authFlow.getData().get(AuthFlow.KEY_PUSH_DATA);
        }
        if (pushData != null) {
            setText(getContext().getString(R.string.authing_push_login_success_tip, pushData.getAppName()));
        }
    }
}