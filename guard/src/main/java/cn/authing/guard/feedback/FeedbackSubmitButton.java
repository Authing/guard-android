package cn.authing.guard.feedback;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.network.FeedbackClient;
import cn.authing.guard.util.Util;

public class FeedbackSubmitButton extends PrimaryButton {
    public FeedbackSubmitButton(@NonNull Context context) {
        this(context, null);
    }

    public FeedbackSubmitButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public FeedbackSubmitButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AuthHelpSubmitButton");

        setOnClickListener(v -> submit());
    }

    private void submit() {
        View v = Util.findViewByClass(this, AccountEditText.class);
        if (v == null) {
            return;
        }

        AccountEditText editText = (AccountEditText)v;
        String contact = editText.getText().toString();
        if (Util.isNull(contact)) {
            Util.setErrorText(this, getContext().getString(R.string.authing_contact_info_cannot_be_empty));
            return;
        }

        int type = 0;
        v = Util.findViewByClass(this, IssueLayout.class);
        if (v != null) {
            IssueLayout issueLayout = (IssueLayout)v;
            type = issueLayout.getType();
        }

        String des = "";
        v = Util.findViewByClass(this, FeedbackDescriptionEditText.class);
        if (v != null) {
            FeedbackDescriptionEditText et = (FeedbackDescriptionEditText)v;
            des = et.getText().toString();
        }

        startLoadingVisualEffect();
        FeedbackClient.feedback(contact, type, des, null, ((ok, data) -> {
            stopLoadingVisualEffect();
            if (ok) {
                Activity activity = (Activity) getContext();
                activity.finish();
            } else {
                Util.setErrorText(this, data);
            }
        }));
    }
}
