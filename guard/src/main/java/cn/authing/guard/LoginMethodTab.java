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
import cn.authing.guard.internal.LoginMethodTabItem;
import cn.authing.guard.util.Util;

public class LoginMethodTab extends RelativeLayout {

    private final List<LoginMethodTabItem> items = new ArrayList<>();
    private int itemGravity;

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

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LoginMethodTab);
        itemGravity = array.getInt(R.styleable.LoginMethodTab_itemGravity, 0);
        array.recycle();

        setBackgroundColor(0);

        View underLine = new View(context);
        int height = (int) Util.dp2px(context, 1);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.authing_login_tab_scroll_view);
        underLine.setLayoutParams(lp);
        underLine.setBackgroundColor(Color.parseColor("#EAEBEE"));
        addView(underLine);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setBackgroundColor(0);
        scrollView.setId(R.id.authing_login_tab_scroll_view);
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

            initItemGravity(b);
            if (null != config.getDefaultLoginMethod() && config.getDefaultLoginMethod().equals(s)) {
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
        if (container.getChildCount() > 0) {
            LoginMethodTabItem firstItem = ((LoginMethodTabItem)container.getChildAt(0));
            if (!addDefaultTab){
                firstItem.gainFocus(null);
            }
            showForgotPassWord(firstItem.getType() == LoginContainer.LoginType.EByAccountPassword);
        }

    }

    private void initItemGravity(LoginMethodTabItem view){
        if (itemGravity == 0){
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMarginEnd((int) Util.dp2px(getContext(), 32));
            view.setLayoutParams(params);
        } else if (itemGravity == 1){
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMarginStart((int) Util.dp2px(getContext(), 32));
            view.setLayoutParams(params);
            view.setPadding(0, 0, 0, 0);
        } else if (itemGravity == 2){
            int padding = (int) Util.dp2px(getContext(), 16);
            view.setPadding(padding, 0, padding, 0);
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
            showForgotPassWord(((LoginMethodTabItem)v).getType() == LoginContainer.LoginType.EByAccountPassword);
            Util.setErrorText(this, null);
            Util.hideKeyboard((Activity) getContext());
        });
    }

    private void showForgotPassWord(boolean show){
        post(() -> {
            View view = Util.findViewByClass(LoginMethodTab.this, GoForgotPasswordButton.class);
            if (view != null){
                view.setVisibility(show ? VISIBLE : GONE);
            }
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
