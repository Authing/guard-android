package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.ImageLoader;

public class CaptchaImageView extends androidx.appcompat.widget.AppCompatImageView {

    public CaptchaImageView(Context context) {
        this(context, null);
    }

    public CaptchaImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("CaptchaImageView");
    }
}
