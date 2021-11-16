package cn.authing.guard.internal;

import android.text.TextPaint;
import android.text.style.URLSpan;

public class CustomURLSpan extends URLSpan {

    private int color;

    public CustomURLSpan(String url, int color) {
        super(url);
        this.color = color;
    }

    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(color);
    }
}
