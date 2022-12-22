package cn.authing.guard.social.bind;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;

public class GoSocialBindButton extends androidx.appcompat.widget.AppCompatButton {

    public GoSocialBindButton(@NonNull Context context) {
        this(context, null);
    }

    public GoSocialBindButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoSocialBindButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_white));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_bind_to_existing_account));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackground(getContext().getDrawable(R.drawable.authing_button_background));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minWidth") == null) {
            setMinWidth(0);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minHeight") == null) {
            setMinHeight(0);
        }

        setOnClickListener((v) -> {
            if (context instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) context;
                AuthFlow flow = activity.getFlow();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                int step = flow.getSocialBindAccountCurrentStep();
                flow.setMfaPhoneCurrentStep(step++);
                int[] ids = flow.getSocialAccountBindLayoutIds();
                if (step < ids.length) {
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                } else {
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_social_account_bind);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }
}
