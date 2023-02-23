package cn.authing.guard.push;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.PushData;
import cn.authing.guard.flow.AuthFlow;

public class PushLoginText extends AppCompatTextView {

    public static final int TYPE_APP = 0;
    public static final int TYPE_ACCOUNT = 1;

    public PushLoginText(@NonNull Context context) {
        this(context, null);
    }

    public PushLoginText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PushLoginText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PushLoginText);
        int type = array.getInt(R.styleable.PushLoginText_pushLoginTextType, 0);
        array.recycle();

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow authFlow = activity.getFlow();
        PushData pushData = null;
        if (authFlow != null) {
            pushData = (PushData) authFlow.getData().get(AuthFlow.KEY_PUSH_DATA);
        }
        if (pushData != null) {
            if (type == TYPE_APP) {
                setText(pushData.getAppName());
            } else if (type == TYPE_ACCOUNT) {
                setText(pushData.getAccount());
            }
        }
    }
}
