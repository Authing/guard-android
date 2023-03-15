package cn.authing.guard.profile;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.DeveloperActivity;
import cn.authing.guard.util.Util;

public class GoDeveloperButton extends AppCompatButton {

    public GoDeveloperButton(Context context) {
        this(context, null);
    }

    public GoDeveloperButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoDeveloperButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int padding = (int) Util.dp2px(getContext(), 12);
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

        setOnClickListener((v) -> goDeveloper());
    }

    private void goDeveloper() {
        Intent intent = new Intent(getContext(), DeveloperActivity.class);
        intent.putExtra("user", Authing.getCurrentUser());
        getContext().startActivity(intent);
    }
}
