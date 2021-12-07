package cn.authing.guard.internal;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;

public class GoSomewhereButton extends AppCompatButton {

    public GoSomewhereButton(@NonNull Context context) {
        this(context, null);
    }

    public GoSomewhereButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoSomewhereButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_main));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getDefaultText());
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackground(null);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minWidth") == null) {
            setMinWidth(0);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minHeight") == null) {
            setMinHeight(0);
        }

        setOnClickListener((v)->{
            if (context instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) context;
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                int id = getTargetLayoutId();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, id);
                activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            }
        });
    }

    protected String getDefaultText() {
        return "";
    }

    protected int getTargetLayoutId() {
        return 0;
    }
}

