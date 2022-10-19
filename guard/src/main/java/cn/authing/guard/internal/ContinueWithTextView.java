package cn.authing.guard.internal;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.data.SocialConfig;
import cn.authing.guard.util.Const;

public class ContinueWithTextView extends androidx.appcompat.widget.AppCompatTextView {
    public ContinueWithTextView(Context context) {
        this(context, null);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContinueWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Authing.getPublicConfig(config -> {
            if (config == null || config.getSocialConfigs().size() == 0 ) {
                setVisibility(View.GONE);
                return;
            }
            if (config.getSocialConfigs().size() == 1){
                SocialConfig socialConfig = config.getSocialConfigs().get(0);
                if (socialConfig != null && !TextUtils.isEmpty(socialConfig.getType())
                        && Const.EC_TYPE_YI_DUN.endsWith(socialConfig.getType())){
                    setVisibility(View.GONE);
                }
            }
        });
    }
}
