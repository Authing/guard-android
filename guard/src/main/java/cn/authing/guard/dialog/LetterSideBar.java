package cn.authing.guard.dialog;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;


public class LetterSideBar extends View {

    private final Context mContext;
    private int mChoose = -1;
    private Paint mSideTextPaint;
    private Paint mSideSelectTextPaint;
    private Paint mSideSelectBgPaint;
    private Rect rect;
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private LetterDialog mTextDialog;

    private CharSequence[] mStringArray;
    private int mSideTextColor;
    private int mSideTextSelectColor;
    private float mSideTextSize;
    private Drawable mSideBackground;

    public LetterSideBar(Context context) {
        super(context);
        mContext = context;
        initData();
        initView(null);
    }

    public LetterSideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initData();
        initView(attrs);
    }

    public LetterSideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initView(attrs);
    }

    private void initData() {
        mSideTextPaint = new Paint(ANTI_ALIAS_FLAG);
        mSideSelectTextPaint = new Paint(ANTI_ALIAS_FLAG);
        mSideSelectBgPaint = new Paint(ANTI_ALIAS_FLAG);
        rect = new Rect();
    }

    private void initView(@Nullable AttributeSet attrs) {
        final Resources res = getResources();
        final CharSequence[] defaultStringArray = res.getTextArray(R.array.dl_side_bar_def_list);
        final int defaultSideTextColor = Color.parseColor("#1D2129");
        final int defaultSideTextSelectColor = Color.parseColor("#ffffffff");
        final float defaultSideTextSize = Util.sp2px(getContext(), 10);
        final Drawable defaultSideBackground = new ColorDrawable(Color.parseColor("#ffb2b2b2"));
        final int defaultDialogTextColor = Color.parseColor("#ffffffff");
        final float defaultDialogTextSize = Util.sp2px(getContext(), 20);
        final Drawable defaultDialogTextBackground = res.getDrawable(R.drawable.authing_country_picker_dialog_text_background);
        final int defaultDialogTextBackgroundWidth = (int) Util.dp2px(getContext(), 100);
        final int defaultDialogTextBackgroundHeight = (int) Util.dp2px(getContext(), 100);

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LetterSideBar);
        mStringArray = a.getTextArray(R.styleable.LetterSideBar_letterTextArray);
        if (null == mStringArray || mStringArray.length <= 0) {
            mStringArray = defaultStringArray;
        }
        mSideTextColor = a.getColor(R.styleable.LetterSideBar_letterTextColor, defaultSideTextColor);
        mSideTextSelectColor = a.getColor(R.styleable.LetterSideBar_letterTextSelectColor, defaultSideTextSelectColor);
        mSideTextSize = a.getDimension(R.styleable.LetterSideBar_letterTextSize, defaultSideTextSize);
        mSideBackground = a.getDrawable(R.styleable.LetterSideBar_letterBackground);
        if (null == mSideBackground) {
            mSideBackground = defaultSideBackground;
        }
        boolean mShowTexDialog = a.getBoolean(R.styleable.LetterSideBar_showTextDialog, false);
        int mDialogTextColor = a.getColor(R.styleable.LetterSideBar_dialogTextColor, defaultDialogTextColor);
        float mDialogTextSize = a.getDimension(R.styleable.LetterSideBar_dialogTextSize, defaultDialogTextSize);
        Drawable mDialogTextBackground = a.getDrawable(R.styleable.LetterSideBar_dialogTextBackground);
        if (null == mDialogTextBackground) {
            mDialogTextBackground = defaultDialogTextBackground;
        }
        int mDialogTextBackgroundWidth = a.getDimensionPixelSize(R.styleable.LetterSideBar_dialogTextBackgroundWidth, defaultDialogTextBackgroundWidth);
        int mDialogTextBackgroundHeight = a.getDimensionPixelSize(R.styleable.LetterSideBar_dialogTextBackgroundHeight, defaultDialogTextBackgroundHeight);

        a.recycle();
        if (mShowTexDialog) {
            mTextDialog = new LetterDialog(mContext, mDialogTextBackgroundWidth, mDialogTextBackgroundHeight,
                    mDialogTextColor, mDialogTextSize, mDialogTextBackground);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // get the height
        int height = getHeight();
        // get the width
        int width = getWidth();
        // get one letter height
        int singleHeight = height / mStringArray.length;

        mSideTextPaint.setColor(mSideTextColor);
        mSideTextPaint.setTextSize(mSideTextSize);
        mSideTextPaint.setTypeface(Typeface.DEFAULT);

        mSideSelectTextPaint.setColor(mSideTextSelectColor);
        mSideSelectTextPaint.setTextSize(mSideTextSize);
        mSideSelectTextPaint.setTypeface(Typeface.DEFAULT);
        mSideSelectTextPaint.setFakeBoldText(true);

        mSideSelectBgPaint.setColor(Color.parseColor("#396aff"));

        float startY = (float) singleHeight / 2;
        for (int i = 0; i < mStringArray.length; i++) {
            String text = mStringArray[i].toString();
            float x = (float) width / 2;
            float y = singleHeight * i + startY;

            if (i == mChoose) {
                mSideSelectTextPaint.getTextBounds(text, 0, text.length(), rect);
                canvas.drawCircle(x, y, 22, mSideSelectBgPaint);
                x = x - (float) rect.width() / 2;
                y = y + (float) rect.height() / 2;
                canvas.drawText(text, x, y, mSideSelectTextPaint);
            } else {
                mSideTextPaint.getTextBounds(text, 0, text.length(), rect);
                x = x - (float) rect.width() / 2;
                y = y + (float) rect.height() / 2;
                canvas.drawText(text, x, y, mSideTextPaint);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        // get the Y
        final float y = event.getY();
        final int oldChoose = mChoose;
        final OnTouchingLetterChangedListener changedListener = onTouchingLetterChangedListener;
        final int letterPos = (int) (y / getHeight() * mStringArray.length);

        if (action == MotionEvent.ACTION_UP) {
            setBackgroundDrawable(new ColorDrawable(0x00000000));
            //mChoose = -1;
            invalidate();
            if (mTextDialog != null) mTextDialog.dismissD();
        } else {
            if (mSideBackground != null){
                setBackground(mSideBackground);
            }
            if (oldChoose != letterPos) {
                if (letterPos >= 0 && letterPos < mStringArray.length) {
                    if (changedListener != null)
                        changedListener.onTouchingLetterChanged(mStringArray[letterPos].toString());
                    if (mTextDialog != null) mTextDialog.showD(mStringArray[letterPos].toString());
                    mChoose = letterPos;
                    invalidate();
                }
            }
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener changedListener) {
        this.onTouchingLetterChangedListener = changedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String str);
    }
}
