package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import cn.authing.guard.data.Config;
import cn.authing.guard.data.ImageLoader;

public class AppLogo extends androidx.appcompat.widget.AppCompatImageView {

    public AppLogo(Context context) {
        super(context);
        init(context);
    }

    public AppLogo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppLogo(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Config config = Authing.getPublicConfig();
        if (config == null) {
            return;
        }

        String url = config.getUserpoolLogo();
        ImageLoader.with(context).load(url).into(this);
    }
}
