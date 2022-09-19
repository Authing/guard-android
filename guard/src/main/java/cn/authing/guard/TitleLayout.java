package cn.authing.guard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netease.nis.quicklogin.QuickLogin;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.util.NetworkUtils;
import cn.authing.guard.util.Util;

public class TitleLayout extends RelativeLayout {

    private Context mContext;
    private boolean showBackIcon;
    private boolean showTitleText;
    private boolean showSkipText;
    private boolean showSkipIcon;
    private String titleText;
    private float titleTextSie;
    private int titleTextColor;
    private boolean titleTextBold;
    private String skipText;
    private float skipTextSie;
    private int skipTextColor;
    private boolean skipTextBold;
    private boolean checkNetWork;
    private boolean onlyCheckNetWork;

    private ImageView skipImageView;
    private OnClickListener mBackIconClickListener;
    private OnClickListener mSkipClickListener;
    private boolean isNetworkAvailable;
    private boolean skipComplateFileds;

    private TitleLayoutNetworkCallback titleLayoutNetworkCallback;
    private boolean hasRegister;

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        Analyzer.report("TitleLayout");
        init(context, attrs);
        initView();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleLayout);
        showBackIcon = array.getBoolean(R.styleable.TitleLayout_showBackIcon, false);
        showTitleText = array.getBoolean(R.styleable.TitleLayout_showTitleText, false);
        showSkipText = array.getBoolean(R.styleable.TitleLayout_showSkipText, false);
        showSkipIcon = array.getBoolean(R.styleable.TitleLayout_showSkipIcon, false);
        titleText = array.getString(R.styleable.TitleLayout_titleText);
        titleTextSie = array.getDimension(R.styleable.TitleLayout_titleTextSize, Util.sp2px(context, 16));
        titleTextColor = array.getColor(R.styleable.TitleLayout_titleTextColor, context.getColor(R.color.authing_text_black));
        titleTextBold = array.getBoolean(R.styleable.TitleLayout_titleTextBold, true);
        skipText = array.getString(R.styleable.TitleLayout_skipText);
        skipTextSie = array.getDimension(R.styleable.TitleLayout_skipTextSize, Util.sp2px(context, 16));
        skipTextColor = array.getColor(R.styleable.TitleLayout_skipTextColor, context.getColor(R.color.authing_main));
        skipTextBold = array.getBoolean(R.styleable.TitleLayout_skipTextBold, false);
        checkNetWork = array.getBoolean(R.styleable.TitleLayout_checkNetWork, false);
        onlyCheckNetWork = array.getBoolean(R.styleable.TitleLayout_onlyCheckNetWork, false);
        array.recycle();

        Authing.getPublicConfig(config -> {
            if (config != null){
                skipComplateFileds = config.isSkipComplateFileds();
            }
        });
    }

    public void initView() {
        setGravity(Gravity.CENTER_VERTICAL);

        boolean added = checkNetworkLayoutAdded(mContext);
        if (added) {
            setVisibility(View.VISIBLE);
            startListeningNetWork();
            return;
        }

        if (onlyCheckNetWork) {
            setVisibility(GONE);
            startListeningNetWork();
            return;
        }

        setBackground(null);
        removeAllViews();
        setOnClickListener(null);

        addBackIcon(mContext);
        addTitle(mContext);
        addSkipIcon(mContext);
        addSkipText(mContext);

        startListeningNetWork();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addBackIcon(Context context) {
        if (!showBackIcon) {
            return;
        }
        ImageView imageView = new ImageView(context);
        LayoutParams params = new LayoutParams(
                (int) Util.dp2px(context, 30), ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        imageView.setLayoutParams(params);
        imageView.setPadding(0, 0, (int) Util.dp2px(context, 21), 0);
        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_authing_back));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(v -> {
            if (mContext instanceof AuthActivity) {
                ((AuthActivity) mContext).onBackPressed();
            }
            if (mBackIconClickListener != null) {
                mBackIconClickListener.onClick(v);
            }
        });
        addView(imageView);
    }


    private void addTitle(Context context) {
        if (!showTitleText) {
            return;
        }
        TextView textView = new TextView(context);
        textView.setText(titleText);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSie);
        textView.setTextColor(titleTextColor);
        if (titleTextBold) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        textView.setLayoutParams(params);
        addView(textView);
    }

    private void addSkipIcon(Context context) {
        if (!showSkipIcon || !skipComplateFileds) {
            return;
        }

        skipImageView = new ImageView(context);
        LayoutParams params = new LayoutParams(
                (int) Util.dp2px(context, 16), ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        skipImageView.setLayoutParams(params);
        skipImageView.setId(R.id.authing_skip_image_view);
        skipImageView.setPadding((int) Util.dp2px(context, 8), 0, 0, 0);
        skipImageView.setImageDrawable(context.getDrawable(R.drawable.ic_authing_skip));
        skipImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        skipImageView.setOnClickListener(this::skipClicked);
        addView(skipImageView);
    }

    private void addSkipText(Context context) {
        if (!showSkipText || !skipComplateFileds) {
            return;
        }

        TextView textView = new TextView(context);
        textView.setText(TextUtils.isEmpty(skipText) ? getResources().getString(R.string.authing_skip) : skipText);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, skipTextSie);
        textView.setTextColor(skipTextColor);
        if (skipTextBold) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (showSkipIcon && skipImageView != null) {
            params.addRule(RelativeLayout.START_OF, skipImageView.getId());
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
        }
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        textView.setLayoutParams(params);
        textView.setOnClickListener(this::skipClicked);
        addView(textView);
    }

    private void skipClicked(View v){
        if (mContext instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            Intent intent = new Intent();
            intent.putExtra("user", Authing.getCurrentUser());
            activity.setResult(AuthActivity.OK, intent);
            activity.finish();
            QuickLogin.getInstance().quitActivity();
        }
        if (mSkipClickListener != null) {
            mSkipClickListener.onClick(v);
        }
    }

    private boolean checkNetworkLayoutAdded(Context context) {
        if (!checkNetWork) {
            return false;
        }

        isNetworkAvailable = NetworkUtils.isNetworkConnected(context);
        if (!isNetworkAvailable) {
            addNetworkLayout(context);
            setOnClickListener(v -> Util.openSettingUI((Activity) getContext()));
            return true;
        }
        return false;
    }

    private void addNetworkLayout(Context context) {
        removeAllViews();

        ImageView imageView = new ImageView(context);
        LayoutParams leftIconParams = new LayoutParams(
                (int) Util.dp2px(context, 23), ViewGroup.LayoutParams.MATCH_PARENT);
        leftIconParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        leftIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageView.setLayoutParams(leftIconParams);
        imageView.setId(R.id.authing_tip_image_view);
        imageView.setPadding(0, 0, (int) Util.dp2px(context, 5), 0);
        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_authing_prompt));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(imageView);

        TextView textView = new TextView(context);
        textView.setText(context.getString(R.string.authing_network_error_tips));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSie);
        textView.setTextColor(Color.parseColor("#FB9926"));
        LayoutParams textParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.END_OF, imageView.getId());
        textParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textView.setLayoutParams(textParams);
        addView(textView);

        ImageView skipImageView = new ImageView(context);
        LayoutParams rightIconParams = new LayoutParams(
                (int) Util.dp2px(context, 16), ViewGroup.LayoutParams.MATCH_PARENT);
        rightIconParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        rightIconParams.addRule(RelativeLayout.CENTER_VERTICAL);
        skipImageView.setLayoutParams(rightIconParams);
        skipImageView.setPadding((int) Util.dp2px(context, 8), 0, 0, 0);
        skipImageView.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_yellow));
        skipImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(skipImageView);

        setBackgroundColor(Color.parseColor("#FFF8E8"));
    }

    public void setBackIconClickListener(OnClickListener listener) {
        this.mBackIconClickListener = listener;
    }

    public void setSkipClickListener(OnClickListener listener) {
        this.mSkipClickListener = listener;
    }

    private void startListeningNetWork() {
        if (hasRegister || mContext == null){
            return;
        }
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return;
        }
        if (titleLayoutNetworkCallback == null){
            titleLayoutNetworkCallback = new TitleLayoutNetworkCallback();
        } else {
            hasRegister = false;
            connMgr.unregisterNetworkCallback(titleLayoutNetworkCallback);
        }
        hasRegister = true;
        connMgr.registerNetworkCallback(request, titleLayoutNetworkCallback);
    }

    private class TitleLayoutNetworkCallback extends ConnectivityManager.NetworkCallback{

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            if (!isNetworkAvailable) {
                isNetworkAvailable = true;
                post(TitleLayout.this::initView);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            if (isNetworkAvailable) {
                isNetworkAvailable = false;
                post(TitleLayout.this::initView);
            }
        }
    }



    public void setShowBackIcon(boolean showBackIcon) {
        this.showBackIcon = showBackIcon;
    }

    public void setShowTitleText(boolean showTitleText) {
        this.showTitleText = showTitleText;
    }

    public void setShowSkipText(boolean showSkipText) {
        this.showSkipText = showSkipText;
    }

    public void setShowSkipIcon(boolean showSkipIcon) {
        this.showSkipIcon = showSkipIcon;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public void setTitleTextSie(float titleTextSie) {
        this.titleTextSie = titleTextSie;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public void setTitleTextBold(boolean titleTextBold) {
        this.titleTextBold = titleTextBold;
    }

    public void setSkipText(String skipText) {
        this.skipText = skipText;
    }

    public void setSkipTextSie(float skipTextSie) {
        this.skipTextSie = skipTextSie;
    }

    public void setSkipTextColor(int skipTextColor) {
        this.skipTextColor = skipTextColor;
    }

    public void setSkipTextBold(boolean skipTextBold) {
        this.skipTextBold = skipTextBold;
    }

    public void setCheckNetWork(boolean checkNetWork) {
        this.checkNetWork = checkNetWork;
    }

    public void setOnlyCheckNetWork(boolean onlyCheckNetWork) {
        this.onlyCheckNetWork = onlyCheckNetWork;
    }
}
