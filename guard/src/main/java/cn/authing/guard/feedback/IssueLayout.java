package cn.authing.guard.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class IssueLayout extends LinearLayout {

    private final Spinner spinner;
    private TextView detail;

    public IssueLayout(Context context) {
        this(context, null);
    }

    public IssueLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IssueLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        String[] TYPES = new String[]{context.getString(R.string.authing_cannot_get_verify_code),
                context.getString(R.string.authing_cannot_login),
                context.getString(R.string.authing_cannot_register),
                context.getString(R.string.authing_lost_account),
                context.getString(R.string.authing_cannot_reset_password),
                context.getString(R.string.authing_account_is_locked),
                context.getString(R.string.authing_other)};

        setOrientation(VERTICAL);

        LinearLayout wrapper = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(params);
        wrapper.setBackground(context.getDrawable(R.drawable.authing_edit_text_background_normal));
        addView(wrapper);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Util.dp2px(context, 48));
        spinner = new Spinner(context);
        spinner.setLayoutParams(params);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.authing_spinner_item,
                TYPES);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = "";
                if (position == 0) {
                    text = getContext().getString(R.string.authing_cannot_get_verify_code_tips);
                } else if (position == 1) {
                    text = getContext().getString(R.string.authing_cannot_login_tips);
                } else if (position == 2) {
                    text = getContext().getString(R.string.authing_cannot_register_tips);
                } else if (position == 3) {
                    text = getContext().getString(R.string.authing_lost_account_tips);
                } else if (position == 4) {
                    text = getContext().getString(R.string.authing_cannot_reset_password_tips);
                } else if (position == 5) {
                    text = getContext().getString(R.string.authing_account_is_locked_tips);
                }
                detail.setText(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        wrapper.addView(spinner);

        detail = new TextView(context);
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.topMargin = (int) Util.dp2px(getContext(), 24);
        detail.setLayoutParams(textParams);
        detail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        detail.setTextColor(getContext().getColor(R.color.authing_text_gray));
        detail.setText(getContext().getString(R.string.authing_cannot_get_verify_code_tips));
        addView(detail);
    }

    public int getType() {
        return spinner.getSelectedItemPosition();
    }
}
