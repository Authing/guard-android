package cn.authing.guard.social.bind;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.SocialBindData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.SocialBindMethodTabItem;
import cn.authing.guard.util.Util;

public class SocialBindMethodTab extends LinearLayout {

    private final List<SocialBindMethodTabItem> items = new ArrayList<>();

    public SocialBindMethodTab(Context context) {
        this(context, null);
    }

    public SocialBindMethodTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SocialBindMethodTab(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SocialBindMethodTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

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

        if (!(context instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = activity.getFlow();

        UserInfo data = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (data != null) {
            post(() -> init(data.getSocialBindData(), container));
        }
    }

    private void init(SocialBindData socialBindData, LinearLayout container) {
        if (socialBindData == null) {
            initDefaultLogins(container);
            return;
        }

        List<String> methods = socialBindData.getMethods();
        if (methods == null || methods.size() == 0) {
            initDefaultLogins(container);
            return;
        }

        List<String> tabMethodList = new ArrayList<>();
        for (String method : methods) {
            if ("email-password".equals(method)
                    || "username-password".equals(method)
                    || "phone-password".equals(method)) {
                if (!tabMethodList.contains("password")) {
                    tabMethodList.add("password");
                }
            } else {
                tabMethodList.add(method);
            }
        }

        boolean addDefaultTab = false;
        for (String s : tabMethodList) {
            SocialBindMethodTabItem b = new SocialBindMethodTabItem(getContext());
            if ("phone-code".equals(s)) {
                b.setText(getResources().getString(R.string.authing_login_by_phone_code));
                b.setType(SocialBindContainer.SocialBindType.EByPhoneCode);
            } else if ("password".equals(s)) {
                b.setText(getResources().getString(R.string.authing_login_by_password));
                b.setType(SocialBindContainer.SocialBindType.EByAccountPassword);
            } else if ("email-code".equals(s)) {
                b.setText(getResources().getString(R.string.authing_login_by_email_code));
                b.setType(SocialBindContainer.SocialBindType.EByEmailCode);
            } else {
                continue;
            }

            if (tabMethodList.get(0).equals(s)) {
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
            ((SocialBindMethodTabItem) container.getChildAt(0)).gainFocus(null);
        }
    }

    public void addClickListener(View view) {
        view.setOnClickListener((v) -> {
            SocialBindMethodTabItem lastFocused = null;
            for (SocialBindMethodTabItem item : items) {
                if (item.isFocused()) {
                    lastFocused = item;
                }
                item.loseFocus();
            }
            ((SocialBindMethodTabItem) v).gainFocus(lastFocused);
            Util.setErrorText(this, null);
        });
    }

    private void initDefaultLogins(ViewGroup container) {
        SocialBindMethodTabItem b = new SocialBindMethodTabItem(getContext());
        b.setText(getResources().getString(R.string.authing_login_by_phone_code));
        container.addView(b);
        b.gainFocus(null);
        b.setType(SocialBindContainer.SocialBindType.EByPhoneCode);
        addClickListener(b);
        items.add(b);

        b = new SocialBindMethodTabItem(getContext());
        b.setText(getResources().getString(R.string.authing_login_by_password));
        b.setType(SocialBindContainer.SocialBindType.EByAccountPassword);
        container.addView(b);
        addClickListener(b);
        items.add(b);
    }
}
