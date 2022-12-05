package cn.authing.guard.oneclick;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.util.Util;

public class OneClickAuthButton extends PrimaryButton {

    private OneClick oneClick;
    protected AuthCallback<UserInfo> callback;

    public OneClickAuthButton(@NonNull Context context) {
        this(context, null);
    }

    public OneClickAuthButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public OneClickAuthButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("OneClickAuthButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_one_click);
        }

        setOnClickListener((v)->{
            startLoadingVisualEffect();
            if (null == oneClick){
                oneClick = new OneClick(context);
            }
            oneClick.start((code, message, userInfo)->{
                stopLoadingVisualEffect();
                if (callback != null) {
                    callback.call(code, message, userInfo);
                    return;
                }
                if (code == 200 && userInfo != null) {
                    Intent intent = new Intent();
                    intent.putExtra("user", userInfo);
                    ((Activity)getContext()).setResult(AuthActivity.OK, intent);
                    ((Activity)getContext()).finish();
                } else {
                    post(() -> Util.setErrorText(v, message));
                }
            });
        });
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

}
