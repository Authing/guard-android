package cn.authing.guard;

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

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.activity.IndexAuthActivity;
import cn.authing.guard.flow.AuthFlow;

public class GoLoginTipsText extends AppCompatTextView {

    private String countDownTip;
    private int countdown = 5;
    private CountDownTimer countDownTimer;
    private boolean canceled;

    public GoLoginTipsText(@NonNull Context context) {
        this(context, null);
    }

    public GoLoginTipsText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoLoginTipsText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_text_gray));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            countDownTip = getContext().getString(R.string.authing_go_back_login_tips);
        }else {
            countDownTip = (String)getText();
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GoLoginTipsText);
        countdown = array.getInt(R.styleable.GoLoginTipsText_countDownTime,5);
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
                    AuthActivity activity = (AuthActivity) context;
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    Intent intent = new Intent(getContext(), IndexAuthActivity.class);
                    intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getIndexLayoutId());
                    activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
                    activity.finish();
                }
            }
        };
        countDownTimer.start();
    }

    public void cancelCuntDown(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            canceled = true;
        }
    }

}
