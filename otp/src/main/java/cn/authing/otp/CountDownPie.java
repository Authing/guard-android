package cn.authing.otp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CountDownPie extends View {

    private final Paint piePaint = new Paint();
    private CountDownListener listener;

    public CountDownPie(Context context) {
        this(context, null);
    }

    public CountDownPie(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownPie(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        piePaint.setColor(0xff396aff);
    }

    public void setListener(CountDownListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float degree = listener == null ? 90 : listener.getDegree();
        canvas.drawArc(0, 0, getWidth(), getHeight(), 270-degree, degree, true, piePaint);
    }
}
