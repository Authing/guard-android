package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.flow.AuthFlow;

public class GoRecoveryButton extends androidx.appcompat.widget.AppCompatButton {

    public GoRecoveryButton(@NonNull Context context) {
        this(context, null);
    }

    public GoRecoveryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoRecoveryButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GoRecoveryButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_main));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_use_recovery_code));
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
                AuthActivity activity = (AuthActivity)context;
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaRecoveryLayoutIds()[0]);
                activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            }
        });
    }
}
