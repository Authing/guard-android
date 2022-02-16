package cn.authing.guard.profile;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.DeveloperActivity;
import cn.authing.guard.util.Util;

public class GoDeveloperButton extends Button {

    public GoDeveloperButton(Context context) {
        this(context, null);
    }

    public GoDeveloperButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoDeveloperButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GoDeveloperButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        int padding = (int)Util.dp2px(getContext(), 12);
        setPadding(padding, 0, 0, 0);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_developer);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(0xff808080);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(16);
        }

        setTextAppearance(0);

        setOnClickListener((v)->goDeveloper());
    }

    private void goDeveloper() {
        Intent intent = new Intent(getContext(), DeveloperActivity.class);
        intent.putExtra("user", Authing.getCurrentUser());
        getContext().startActivity(intent);
    }
}
