package cn.authing.guard.social;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.authing.guard.Callback;
import cn.authing.guard.R;
import cn.authing.guard.WeComLoginButton;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.CustomEventButton;
import cn.authing.guard.social.WechatLoginButton;
import cn.authing.guard.util.Util;

public class SocialLoginListView extends LinearLayout {

    protected Callback<UserInfo> callback;

    public SocialLoginListView(Context context) {
        this(context, null);
    }

    public SocialLoginListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialLoginListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SocialLoginListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SocialLoginListView);
        String src = array.getString(R.styleable.SocialLoginListView_src);
        if (TextUtils.isEmpty(src)) {
            src = "wechat|wecom";
        }
        array.recycle();

        setup(context, src);
    }

    private void setup(Context context, String s) {
        String[] sources = s.split("\\|");
        for (int i = 0;i < sources.length;++i) {
            String src = sources[i].trim();

            CustomEventButton button = null;
            if (src.equals("wechat")) {
                button = new WechatLoginButton(context);
            } else if (src.equals("wecom")) {
                button = new WeComLoginButton(context);
            }

            if (button == null) {
                continue;
            }

            int length = (int) Util.dp2px(context, 44);
            int m = (int) Util.dp2px(context, 8);
            LayoutParams lp = new LayoutParams(length, length);
            lp.setMargins(m, 0, m, 0);
            button.setLayoutParams(lp);
            addView(button);
            button.setOnLoginListener((ok, data) -> {
                fireCallback(data, src);
            });
        }
    }

    public void setOnLoginListener(Callback<UserInfo> callback) {
        this.callback = callback;
    }

    private void fireCallback(UserInfo info, String src) {
        if (callback != null) {
            if (info == null) {
                callback.call(false, null);
            } else {
                info.setThirdPartySource(src);
                callback.call(true, info);
            }
        }
    }
}
