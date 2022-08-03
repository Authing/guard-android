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
        String src = array.getString(R.styleable.SocialLoginListView_loginSource);
        if (TextUtils.isEmpty(src)) {
            src = "auto"; // auto means from console
        }
        array.recycle();

        if ("auto".equals(src)) {
            StringBuilder sb = new StringBuilder();
            Authing.getPublicConfig((config -> {
                if (config == null) {
                    return;
                }
                List<SocialConfig> socialConfigs = config.getSocialConfigs();
                for (int i = 0, n = socialConfigs.size();i < n;++i) {
                    SocialConfig sc = socialConfigs.get(i);
                    sb.append(sc.getType());
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
        removeAllViews();
        String[] sources = s.split("\\|");
        for (String source : sources) {
            String type = source.trim();

            SocialLoginButton button = null;
            switch (type) {
                case Const.EC_TYPE_WECHAT:
                    button = new WechatLoginButton(context);
                    break;
                case Const.EC_TYPE_ALIPAY:
                    button = new AlipayLoginButton(context);
                    break;
                case Const.EC_TYPE_WECHAT_COM:
                case Const.EC_TYPE_WECHAT_COM_AGENCY:
                    button = new WeComLoginButton(context);
                    button.setType(type);
                    break;
                case Const.EC_TYPE_LARK_INTERNAL:
                case Const.EC_TYPE_LARK_PUBLIC:
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
