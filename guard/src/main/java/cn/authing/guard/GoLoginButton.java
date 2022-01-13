package cn.authing.guard;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.activity.IndexAuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.GoSomewhereButton;

public class GoLoginButton extends GoSomewhereButton {

    public GoLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public GoLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GoLoginButton");

        // cannot use super onclick because we need to start IndexAuthActivity instead of AuthActivity
        setOnClickListener((v)->{
            if (context instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity)context;
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                Intent intent = new Intent(getContext(), IndexAuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getIndexLayoutId());
                activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            }
        });
    }

    protected String getDefaultText() {
        return getResources().getString(R.string.authing_go_login);
    }
}
