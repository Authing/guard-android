package cn.authing.guard.mfa;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;

public class GoMFAFaceBindButton extends GoMFAFaceButton {

    public GoMFAFaceBindButton(@NonNull Context context) {
        this(context, null);
    }

    public GoMFAFaceBindButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoMFAFaceBindButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void next() {
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_face_bind);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        //activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
        activity.startActivity(intent);
        activity.finish();
    }

}
