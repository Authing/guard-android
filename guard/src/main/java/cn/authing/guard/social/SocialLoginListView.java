package cn.authing.guard.social;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class SocialLoginListView extends LinearLayout {

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
            src = "auto"; // auto means from console
        }
        array.recycle();

        if ("auto".equals(src)) {
            StringBuilder sb = new StringBuilder();
            Authing.getPublicConfig((config -> {
                List<SocialConfig> socialConfigs = config.getSocialConfigs();
                for (int i = 0, n = socialConfigs.size();i < n;++i) {
                    SocialConfig sc = socialConfigs.get(i);
                    String type = sc.getType();
                    if ("wechat:mobile".equals(type)) {
                        sb.append("wechat");
                    } else if ("alipay".equals(type)) {
                        sb.append("alipay");
                    } else if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
                        sb.append("wecom");
                    } else if (Const.EC_TYPE_LARK_INTERNAL.equals(type)
                            || Const.EC_TYPE_LARK_PUBLIC.equals(type)) {
                        sb.append("lark");
                    }
                    if (i < n - 1) {
                        sb.append("|");
                    }
                }
                setup(context, sb.toString());
            }));
        } else {
            setup(context, src);
        }
    }

    private void setup(Context context, String s) {
        String[] sources = s.split("\\|");
        for (String source : sources) {
            String src = source.trim();

            SocialLoginButton button = null;
            switch (src) {
                case "wechat":
                    button = new WechatLoginButton(context);
                    break;
                case "alipay":
                    button = new AlipayLoginButton(context);
                    break;
                case "wecom":
                    button = new WeComLoginButton(context);
                    break;
                case "lark":
                    button = new LarkLoginButton(context);
                    break;
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
        }
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        for (int i = 0;i < getChildCount();++i) {
            View child = getChildAt(i);
            if (child instanceof SocialLoginButton) {
                ((SocialLoginButton)child).setOnLoginListener(callback);
            }
        }
    }
}
