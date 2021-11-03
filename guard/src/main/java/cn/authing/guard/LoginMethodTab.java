package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.data.Config;
import cn.authing.guard.internal.LoginMethodTabItem;
import cn.authing.guard.util.Util;

public class LoginMethodTab extends LinearLayout {

    private List<LoginMethodTabItem> items = new ArrayList<>();

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

        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setBackgroundColor(0);
        addView(scrollView);

        View underLine = new View(context);
        int height = (int) Util.dp2px(context, 1);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        underLine.setLayoutParams(lp);
        underLine.setBackgroundColor(0xfff4f4f4);
        addView(underLine);

        // contents
        LinearLayout container = new LinearLayout(context);
        LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        container.setLayoutParams(containerParam);
        container.setOrientation(LinearLayout.HORIZONTAL);
        scrollView.addView(container);

        Config config = Authing.getPublicConfig();
        if (config == null) {
            initDefaultLogins(container);
            return;
        }

        List<String> loginTabList = Authing.getPublicConfig().getLoginTabList();
        if (loginTabList == null || loginTabList.size() == 0) {
            initDefaultLogins(container);
            return;
        }

        for (String s : loginTabList) {
            LoginMethodTabItem b = new LoginMethodTabItem(getContext());
            if ("phone-code".equals(s)) {
                b.setText("验证码登录");
                b.setType(LoginContainer.LoginType.EByPhoneCode);
            } else if ("password".equals(s)) {
                b.setText("密码登录");
                b.setType(LoginContainer.LoginType.EByAccountPassword);
            }

            if (config.getDefaultLoginMethod().equals(s)) {
                b.gainFocus();
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
            for (LoginMethodTabItem item : items) {
                item.loseFocus();
            }
            ((LoginMethodTabItem)v).gainFocus();
        });
    }

    private void initDefaultLogins(ViewGroup container) {
        LoginMethodTabItem b = new LoginMethodTabItem(getContext());
        b.setText("验证码登录");
        container.addView(b);
        b.gainFocus();
        b.setType(LoginContainer.LoginType.EByPhoneCode);
        addClickListener(b);
        items.add(b);

        b = new LoginMethodTabItem(getContext());
        b.setText("密码登录");
        b.setType(LoginContainer.LoginType.EByAccountPassword);
        container.addView(b);
        addClickListener(b);
        items.add(b);
    }
}
