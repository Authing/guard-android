package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

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

public class GoMFAFinishLoginTipsText extends AppCompatTextView {

    private String countDownTip;
    private int countdown = 5;
    private CountDownTimer countDownTimer;
    private boolean canceled;

    public GoMFAFinishLoginTipsText(@NonNull Context context) {
        this(context, null);
    }

    public GoMFAFinishLoginTipsText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoMFAFinishLoginTipsText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_text_gray));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            countDownTip = getContext().getString(R.string.authing_login_done_tips);
        } else {
            countDownTip = (String) getText();
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GoLoginTipsText);
        countdown = array.getInt(R.styleable.GoLoginTipsText_countDownTime, 5);
        array.recycle();

        startCountDown(context);
    }

    private void startCountDown(Context context) {
        countDownTimer = new CountDownTimer(1000L * countdown, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                String value = String.valueOf((int) millisUntilFinished / 1000 + 1);
                setText(String.format(countDownTip, value));
            }

            @Override
            public void onFinish() {
                if (!canceled && context instanceof AuthActivity) {
                    Safe.saveRecoveryCode("");
                    Authing.getPublicConfig((config) -> {
                        AuthActivity activity = (AuthActivity) getContext();
                        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                        List<ExtendedField> missingFields = FlowHelper.missingFields(config, Authing.getCurrentUser());
                        if (shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                            flow.getData().put(AuthFlow.KEY_USER_INFO, Authing.getCurrentUser());
                            FlowHelper.handleUserInfoComplete(GoMFAFinishLoginTipsText.this, missingFields);
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("user", Authing.getCurrentUser());
                            activity.setResult(AuthActivity.OK, intent);
                            activity.finish();
                            Util.quitActivity();
                        }
                    });
                }
            }
        };
        countDownTimer.start();
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }

    public void cancelCuntDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            canceled = true;
        }
    }

}
