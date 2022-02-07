package cn.authing.guard.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;

public class FeedbackDescriptionEditText extends EditText {
    public FeedbackDescriptionEditText(Context context) {
        super(context, null);
    }

    public FeedbackDescriptionEditText(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.editTextStyle);
    }

    public FeedbackDescriptionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);

        Analyzer.report("HelpDescriptionEditText");
    }
}
