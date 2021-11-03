package cn.authing.guard.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

public class SpaceOnTheLeftSpan extends ReplacementSpan {
    private static float padding = 50.0f;

    public SpaceOnTheLeftSpan(float p) {
        padding = p;
    }
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int xPos = Math.round(x + padding);
        canvas.drawText(text, start, end, xPos, y, paint);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end) + padding);
    }
}
