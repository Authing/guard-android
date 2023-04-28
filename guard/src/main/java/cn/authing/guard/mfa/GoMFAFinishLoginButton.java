package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.Safe;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.util.Util;

public class GoMFAFinishLoginButton extends androidx.appcompat.widget.AppCompatButton {

    public GoMFAFinishLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public GoMFAFinishLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoMFAFinishLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_white));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_login_done));
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
            Safe.saveRecoveryCode("");
            Authing.getPublicConfig((config) -> {
                if (getContext() instanceof AuthActivity) {
                    View view = Util.findViewByClass(this, GoMFAFinishLoginTipsText.class);
                    if (view instanceof GoMFAFinishLoginTipsText){
                        GoMFAFinishLoginTipsText goFinishLoginTipsText = (GoMFAFinishLoginTipsText) view;
                        goFinishLoginTipsText.cancelCuntDown();
                    }

                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    List<ExtendedField> missingFields = FlowHelper.missingFields(config, Authing.getCurrentUser());
                    if (shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                        flow.getData().put(AuthFlow.KEY_USER_INFO, Authing.getCurrentUser());
                        FlowHelper.handleUserInfoComplete(this, missingFields);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("user", Authing.getCurrentUser());
                        activity.setResult(AuthActivity.OK, intent);
                        activity.finish();
                        Util.quitActivity();
                    }
                }
            });
        });
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }
}
