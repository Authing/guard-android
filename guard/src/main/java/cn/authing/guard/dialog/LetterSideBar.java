package cn.authing.guard.dialog;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

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
    //private LetterDialog mTextDialog;
    private PopupWindow mPopupWindow;
    private TextView mLetterDialogText;

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
        final int defaultSideTextColor = Color.parseColor("#4E5969");
        final int defaultSideTextSelectColor = Color.parseColor("#4E5969");
        final float defaultSideTextSize = Util.sp2px(getContext(), 12);
        final Drawable defaultSideBackground = new ColorDrawable(Color.parseColor("#F7F8FA"));
        final int defaultDialogTextColor = Color.parseColor("#215AE5");
        final float defaultDialogTextSize = Util.sp2px(getContext(), 16);
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
        setBackground(mSideBackground);
        boolean mShowTexDialog = a.getBoolean(R.styleable.LetterSideBar_showTextDialog, true);
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
//            mTextDialog = new LetterDialog(mContext, mDialogTextBackgroundWidth, mDialogTextBackgroundHeight,
//                    mDialogTextColor, mDialogTextSize, mDialogTextBackground);
            initPopupWindow();
        }
    }

    private void initPopupWindow() {
        mPopupWindow = new PopupWindow(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.authing_country_code_picker_letter_dialog, null);
        mPopupWindow.setContentView(view);
        mPopupWindow.setBackgroundDrawable(null);
        mLetterDialogText = view.findViewById(R.id.letter_text);
    }

    private void showPopupWindow(String text, int position) {
        if (mPopupWindow == null) {
            return;
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mLetterDialogText.setText(text);
        int singleHeight = getHeight() / mStringArray.length;
        int x = getLeft() - (int) Util.dp2px(mContext, 40);
        int y = (singleHeight * position) - (getHeight() / 2) + (int) Util.dp2px(mContext, 48);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        mPopupWindow.showAtLocation(this, Gravity.START, x, y);
    }

    private void dismissPopupWindow() {
        if (mPopupWindow == null) {
            return;
        }
        mPopupWindow.dismiss();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mStringArray.length;

        mSideTextPaint.setColor(mSideTextColor);
        mSideTextPaint.setTextSize(mSideTextSize);
        mSideTextPaint.setTypeface(Typeface.DEFAULT);

        mSideSelectTextPaint.setColor(mSideTextSelectColor);
        mSideSelectTextPaint.setTextSize(mSideTextSize);
        mSideSelectTextPaint.setTypeface(Typeface.DEFAULT);
        //mSideSelectTextPaint.setFakeBoldText(true);

        //mSideSelectBgPaint.setColor(Color.parseColor("#FFFFFF"));

        float startY = (float) singleHeight / 2;
        for (int i = 0; i < mStringArray.length; i++) {
            String text = mStringArray[i].toString();
            float x = (float) width / 2;
            float y = singleHeight * i + startY;

            if (i == 0) {
                Bitmap bitmap = getBitmap(mContext, R.drawable.ic_authing_sidebar_top, 0, 0);
                canvas.drawBitmap(bitmap, x - (float) bitmap.getWidth() / 2, y, mSideSelectTextPaint);
                continue;
            }

            if (i == mChoose) {
                mSideSelectTextPaint.getTextBounds(text, 0, text.length(), rect);
                //canvas.drawCircle(x, y, 22, mSideSelectBgPaint);
                Bitmap bitmap = getBitmap(mContext, R.drawable.ic_authing_sidebar_bg, width, singleHeight);
                canvas.drawBitmap(bitmap, 0, singleHeight * i, mSideSelectBgPaint);

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

    @SuppressLint("UseCompatLoadingForDrawables")
    private static Bitmap getBitmap(Context context, int vectorDrawableId, int width, int height) {
        Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
        Bitmap bitmap = Bitmap.createBitmap(width == 0 ? vectorDrawable.getIntrinsicWidth() : width,
                height == 0 ? vectorDrawable.getIntrinsicHeight() : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        final float y = event.getY();
        final int oldChoose = mChoose;
        final OnTouchingLetterChangedListener changedListener = onTouchingLetterChangedListener;
        final int letterPos = (int) (y / getHeight() * mStringArray.length);

        if (action == MotionEvent.ACTION_UP) {
            //setBackgroundDrawable(new ColorDrawable(0x00000000));
            invalidate();
            //if (mTextDialog != null) mTextDialog.dismissD();
            dismissPopupWindow();
        } else {
//            if (mSideBackground != null){
//                setBackground(mSideBackground);
//            }
            if (oldChoose != letterPos) {
                if (letterPos >= 0 && letterPos < mStringArray.length) {
                    if (changedListener != null)
                        changedListener.onTouchingLetterChanged(letterPos, mStringArray[letterPos].toString());
                    //if (mTextDialog != null) mTextDialog.showD(mStringArray[letterPos].toString());
                    if (letterPos != 0) {
                        showPopupWindow(mStringArray[letterPos].toString(), letterPos);
                    }

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
        void onTouchingLetterChanged(int position, String str);
    }
}
