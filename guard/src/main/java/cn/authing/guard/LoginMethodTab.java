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
import cn.authing.guard.internal.LoginMethodTabItem;
import cn.authing.guard.util.Util;

public class LoginMethodTab extends LinearLayout {

    private final List<LoginMethodTabItem> items = new ArrayList<>();

    public LoginMethodTab(Context context) {
        this(context, null);
    }

    public LoginMethodTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginMethodTab(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LoginMethodTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Analyzer.report("LoginMethodTab");

        if (Authing.getAppId() == null) {
            setVisibility(View.GONE);
            return;
        }

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setBackgroundColor(0);
        scrollView.setHorizontalScrollBarEnabled(false);
        addView(scrollView);

        View underLine = new View(context);
        int height = (int) Util.dp2px(context, 1);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        underLine.setLayoutParams(lp);
        underLine.setBackgroundColor(0xfff4f4f4);
        addView(underLine);

        // contents
        LinearLayout container = new LinearLayout(context);
        container.setClipChildren(false);
        LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
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

        List<String> loginTabList = config.getLoginTabList();
        if (loginTabList == null || loginTabList.size() == 0) {
            initDefaultLogins(container);
            return;
        }

        boolean addDefaultTab = false;
        for (String s : loginTabList) {
            LoginMethodTabItem b = new LoginMethodTabItem(getContext());
            if ("phone-code".equals(s)) {
                b.setText(getResources().getString(R.string.authing_login_by_phone_code));
                b.setType(LoginContainer.LoginType.EByPhoneCode);
            } else if ("password".equals(s)) {
                b.setText(getResources().getString(R.string.authing_login_by_password));
                b.setType(LoginContainer.LoginType.EByAccountPassword);
            } else if ("email-code".equals(s)) {
                b.setText(getResources().getString(R.string.authing_login_by_email_code));
                b.setType(LoginContainer.LoginType.EByEmailCode);
            } else {
                continue;
            }

            if (config.getDefaultLoginMethod().equals(s)) {
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
            ((LoginMethodTabItem)container.getChildAt(0)).gainFocus(null);
        }
    }

    public void addClickListener(View view) {
        view.setOnClickListener((v) -> {
            LoginMethodTabItem lastFocused = null;
            for (LoginMethodTabItem item : items) {
                if (item.isFocused()) {
                    lastFocused = item;
                }
                item.loseFocus();
            }
            ((LoginMethodTabItem)v).gainFocus(lastFocused);
            Util.setErrorText(this, null);
        });
    }

    private void initDefaultLogins(ViewGroup container) {
        LoginMethodTabItem b = new LoginMethodTabItem(getContext());
        b.setText(getResources().getString(R.string.authing_login_by_phone_code));
        container.addView(b);
        b.gainFocus(null);
        b.setType(LoginContainer.LoginType.EByPhoneCode);
        addClickListener(b);
        items.add(b);

        b = new LoginMethodTabItem(getContext());
        b.setText(getResources().getString(R.string.authing_login_by_password));
        b.setType(LoginContainer.LoginType.EByAccountPassword);
        container.addView(b);
        addClickListener(b);
        items.add(b);
    }
}
