package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.ImageLoader;

public class AppLogo extends androidx.appcompat.widget.AppCompatImageView {

    public AppLogo(Context context) {
        this(context, null);
    }

    public AppLogo(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppLogo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("AppLogo");

        Authing.getPublicConfig((config)->{
            if (config == null) {
                return;
            }

            String url = config.getLogo();
            if (url != null) {
                ImageLoader.with(context).load(url).into(this);
            }
        });
    }
}
