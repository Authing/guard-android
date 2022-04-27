package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.internal.BaseTabItem;
import cn.authing.guard.internal.RegisterMethodTabItem;
import cn.authing.guard.util.Util;

public class RegisterMethodTab extends LinearLayout {

    private final List<RegisterMethodTabItem> items = new ArrayList<>();

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

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setBackgroundColor(0);
        addView(scrollView);

        View underLine = new View(context);
        int height = (int) Util.dp2px(context, 1);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        underLine.setLayoutParams(lp);
        underLine.setBackgroundColor(0xfff4f4f4);
        addView(underLine);

        // contents
        LinearLayout container = new LinearLayout(context);
        container.setClipChildren(false);
        LayoutParams containerParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
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

        for (String s : tabList) {
            RegisterMethodTabItem b = new RegisterMethodTabItem(getContext());
            if ("phone".equals(s)) {
                b.setText(getResources().getString(R.string.authing_register_by_phone_code));
                b.setType(RegisterContainer.RegisterType.EByPhoneCodePassword);
            } else if ("email".equals(s)) {
                b.setText(getResources().getString(R.string.authing_register_by_email));
                b.setType(RegisterContainer.RegisterType.EByEmailPassword);
            }

            if (config.getDefaultRegisterMethod().equals(s)) {
                b.gainFocus(null);
                container.addView(b, 0);
            } else {
                b.loseFocus();
                container.addView(b);
            }
            addClickListener(b);
            items.add(b);
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
