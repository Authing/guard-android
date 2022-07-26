package cn.authing.guard;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.internal.BaseTabItem;
import cn.authing.guard.internal.RegisterMethodTabItem;
import cn.authing.guard.util.Util;

public class RegisterMethodTab extends RelativeLayout {

    private final List<RegisterMethodTabItem> items = new ArrayList<>();
    private int itemGravity;

    public RegisterMethodTab(Context context) {
        this(context, null);
    }

    public RegisterMethodTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterMethodTab(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RegisterMethodTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Analyzer.report("RegisterMethodTab");

        if (Authing.getAppId() == null) {
            setVisibility(View.GONE);
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RegisterMethodTab);
        itemGravity = array.getInt(R.styleable.RegisterMethodTab_itemGravity, 0);
        array.recycle();

        setBackgroundColor(0);

        View underLine = new View(context);
        int height = (int) Util.dp2px(context, 1);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.authing_register_tab_scroll_view);
        underLine.setLayoutParams(lp);
        underLine.setBackgroundColor(Color.parseColor("#EAEBEE"));
        addView(underLine);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setBackgroundColor(0);
        scrollView.setId(R.id.authing_register_tab_scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        addView(scrollView);

        // contents
        LinearLayout container = new LinearLayout(context);
        container.setClipChildren(false);
        LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        container.setLayoutParams(containerParam);
        container.setOrientation(LinearLayout.HORIZONTAL);
        scrollView.addView(container);

        Authing.getPublicConfig((config -> init(config, container)));
    }

    private void init(Config config, LinearLayout container) {
        if (config == null) {
            initDefaultLogins(container);
            return;
        }

        List<String> tabList = config.getRegisterTabList();
        if (tabList == null || tabList.size() == 0) {
            initDefaultLogins(container);
            return;
        }

        boolean addDefaultTab = false;
        for (String s : tabList) {
            RegisterMethodTabItem b = new RegisterMethodTabItem(getContext());
            if ("phone".equals(s)) {
                b.setText(getResources().getString(R.string.authing_register_by_phone_code));
                b.setType(RegisterContainer.RegisterType.EByPhoneCodePassword);
            } else if ("email".equals(s)) {
                b.setText(getResources().getString(R.string.authing_register_by_email));
                b.setType(RegisterContainer.RegisterType.EByEmailPassword);
            } else if ("emailCode".equals(s)) {
                b.setText(getResources().getString(R.string.authing_register_by_email_code));
                b.setType(RegisterContainer.RegisterType.EByEmailCode);
            }

            initItemGravity(b);
            if (null != config.getDefaultRegisterMethod() && config.getDefaultRegisterMethod().equals(s)) {
                b.gainFocus(null);
                container.addView(b, 0);
                addDefaultTab = true;
            } else {
                b.loseFocus();
                container.addView(b);
            }
            addClickListener(b);
            items.add(b);
        }
        if (!addDefaultTab && container.getChildCount() > 0) {
            ((RegisterMethodTabItem)container.getChildAt(0)).gainFocus(null);
        }
    }

    private void initItemGravity(RegisterMethodTabItem view){
        if (itemGravity == 0){
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMarginEnd((int) Util.dp2px(getContext(), 32));
            view.setLayoutParams(params);
        } else if (itemGravity == 1){
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMarginStart((int) Util.dp2px(getContext(), 32));
            view.setLayoutParams(params);
        } else if (itemGravity == 2){
            int padding = (int) Util.dp2px(getContext(), 16);
            view.setPadding(padding, 0, padding, 0);
        }
    }

    public void addClickListener(View view) {
        view.setOnClickListener((v) -> {
            BaseTabItem lastFocused = null;
            for (RegisterMethodTabItem item : items) {
                if (item.isFocused()) {
                    lastFocused = item;
                }
                item.loseFocus();
            }
            ((RegisterMethodTabItem)v).gainFocus(lastFocused);
            Util.setErrorText(this, null);
            Util.hideKeyboard((Activity) getContext());
        });
    }

    private void initDefaultLogins(ViewGroup container) {
        RegisterMethodTabItem b = new RegisterMethodTabItem(getContext());
        b.setText("手机号注册");
        container.addView(b);
        b.gainFocus(null);
        b.setType(RegisterContainer.RegisterType.EByPhoneCodePassword);
        addClickListener(b);
        items.add(b);

        b = new RegisterMethodTabItem(getContext());
        b.setText("邮箱注册");
        b.setType(RegisterContainer.RegisterType.EByEmailPassword);
        container.addView(b);
        addClickListener(b);
        items.add(b);
    }
}
