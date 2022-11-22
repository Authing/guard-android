package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;

public class GoMFABindButton extends androidx.appcompat.widget.AppCompatButton {

    public GoMFABindButton(@NonNull Context context) {
        this(context, null);
    }

    public GoMFABindButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoMFABindButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_white));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_go_to_bind_mfa));
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

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GoMFABindButton);
        int currentMfaBindType = array.getInt(R.styleable.GoMFABindButton_currentMfaBindType, -1);
        array.recycle();

        setOnClickListener((v) -> {
            if (context instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) context;
                AuthFlow flow = activity.getFlow();
                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                if (currentMfaBindType == 0) {
                    int step = flow.getMfaPhoneCurrentStep();
                    flow.setMfaPhoneCurrentStep(step++);
                    int[] ids = flow.getMfaPhoneLayoutIds();
                    if (step < ids.length) {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                    } else {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_phone_verify);
                    }
                } else if (currentMfaBindType == 1) {
                    int step = flow.getMfaEmailCurrentStep();
                    flow.setMfaEmailCurrentStep(step++);
                    int[] ids = flow.getMfaEmailLayoutIds();
                    if (step < ids.length) {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                    } else {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_email_verify);
                    }
                } else if (currentMfaBindType == 2) {
                    int step = flow.getMfaOTPCurrentStep();
                    flow.setMfaOTPCurrentStep(step++);
                    int[] ids = flow.getMfaOTPLayoutIds();
                    if (step < ids.length) {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                    } else {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_otp_verify);
                    }
                } else if (currentMfaBindType == 3) {
                    int step = flow.getMfaFaceCurrentStep();
                    flow.setMfaFaceCurrentStep(step++);
                    int[] ids = flow.getMfaFaceLayoutIds();
                    if (step < ids.length) {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                    } else {
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_face_verify_before);
                    }
                } else {
                    return;
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                //activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }
}
