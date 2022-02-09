package cn.authing.guard.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.BindEmailActivity;
import cn.authing.guard.activity.BindPhoneActivity;
import cn.authing.guard.activity.ChangePasswordActivity;
import cn.authing.guard.activity.UpdateUserProfileActivity;
import cn.authing.guard.data.ImageLoader;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.Util;

public class UserProfileContainer extends LinearLayout {

    private static final int AVATAR_LAYOUT_HEIGHT = 64;
    private static final int AVATAR_HEIGHT = 40;
    private static final int AVATAR_MARGIN = (AVATAR_LAYOUT_HEIGHT - AVATAR_HEIGHT) / 2;
    private static final int TEXT_LAYOUT_HEIGHT = 48;

    private final int padding;
    private ImageView ivAvatar;

    public UserProfileContainer(Context context) {
        this(context, null);
    }

    public UserProfileContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserProfileContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UserProfileContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        padding = (int)Util.dp2px(getContext(), 12);
        setOrientation(VERTICAL);

        addAvatarLayout();

        create(context.getString(R.string.authing_nickname), "nickname");
        create(context.getString(R.string.authing_name), "name");
        create(context.getString(R.string.authing_username), "username");
        create(context.getString(R.string.authing_phone), "phone");
        create(context.getString(R.string.authing_email), "email");

        addPasswordLayout();
    }

    private void addAvatarLayout() {
        LinearLayout layout = createRoot(AVATAR_LAYOUT_HEIGHT);
        layout.addView(createLabel(getContext().getString(R.string.authing_avatar)));
        layout.addView(createSpace());

        ivAvatar = new ImageView(getContext());
        int size = (int)Util.dp2px(getContext(), AVATAR_HEIGHT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        int m = (int)Util.dp2px(getContext(), AVATAR_MARGIN);
        params.setMargins(0, m, 0, m);
        ivAvatar.setLayoutParams(params);
        ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        layout.addView(ivAvatar);

        layout.addView(createArrow());
        addView(layout);
        addSeparator();

        layout.setOnClickListener((v)->{
            Intent i = new Intent();
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            ((Activity) getContext()).startActivityForResult(Intent.createChooser(i, "Select Picture"), 1000);
        });
    }

    private void create(String label, String key) {
        LinearLayout layout = createRoot(TEXT_LAYOUT_HEIGHT);
        layout.addView(createLabel(label));
        layout.addView(createSpace());

        LinearLayout.LayoutParams labelParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tvValue = new TextView(getContext());
        tvValue.setLayoutParams(labelParam);
        tvValue.setTextSize(16);
        tvValue.setGravity(Gravity.CENTER_VERTICAL);
        tvValue.setTag(key);
        String value = Authing.getCurrentUser().getMappedData(key);
        tvValue.setText(Util.isNull(value) ? getContext().getString(R.string.authing_unspecified) : value);
        layout.addView(tvValue);

        layout.addView(createArrow());

        addView(layout);
        addSeparator();

        layout.setOnClickListener((v)->{
            if ("phone".equals(key)) {
                Intent intent = new Intent(getContext(), BindPhoneActivity.class);
                getContext().startActivity(intent);
            } else if ("email".equals(key)) {
                Intent intent = new Intent(getContext(), BindEmailActivity.class);
                getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), UpdateUserProfileActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("label", label);
                getContext().startActivity(intent);
            }
        });
    }

    private LinearLayout createRoot(int height) {
        height = (int)Util.dp2px(getContext(), height);
        LinearLayout layout = new LinearLayout(getContext());
        LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        layout.setLayoutParams(containerParam);
        layout.setBackgroundColor(0xffffffff);
        layout.setPadding(padding, 0, padding, 0);
        layout.setOrientation(HORIZONTAL);
        return layout;
    }

    private TextView createLabel(String label) {
        LinearLayout.LayoutParams labelParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tvLabel = new TextView(getContext());
        tvLabel.setLayoutParams(labelParam);
        tvLabel.setTextSize(16);
        tvLabel.setGravity(Gravity.CENTER_VERTICAL);
        tvLabel.setText(label);
        return tvLabel;
    }

    private Space createSpace() {
        Space space = new Space(getContext());
        LinearLayout.LayoutParams lpSpace = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        space.setLayoutParams(lpSpace);
        return space;
    }

    private ImageView createArrow() {
        int ivWidth = (int)Util.dp2px(getContext(), 24);
        ImageView ivArrow = new ImageView(getContext());
        LinearLayout.LayoutParams arrowParam = new LinearLayout.LayoutParams(ivWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        ivArrow.setLayoutParams(arrowParam);
        ivArrow.setImageDrawable(getContext().getDrawable(R.drawable.authing_arrow_right));
        return ivArrow;
    }

    private void addPasswordLayout() {
        LinearLayout layout = createRoot(TEXT_LAYOUT_HEIGHT);
        layout.addView(createLabel(getContext().getString(R.string.authing_modify_password)));
        layout.addView(createSpace());
        layout.addView(createArrow());
        addView(layout);

        layout.setOnClickListener((v)->{
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
            getContext().startActivity(intent);
        });
    }

    private void addSeparator() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        View v = new View(getContext());
        v.setLayoutParams(params);
        v.setBackgroundColor(0xffdddddd);
        v.setPadding(padding, 0, 0, 0);
        addView(v);
    }

    public void refreshData() {
        UserInfo userInfo = Authing.getCurrentUser();

        ImageLoader.with(getContext()).load(userInfo.getPhoto()).into(ivAvatar);

        for (int i = 0;i < getChildCount();++i) {
            View child = getChildAt(i);
            if (child instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup)child;
                for (int j = 0;j < vg.getChildCount();++j) {
                    View v = vg.getChildAt(j);
                    if (v instanceof TextView &&  v.getTag() != null) {
                        String key = (String)v.getTag();
                        String value = userInfo.getMappedData(key);
                        ((TextView)v).setText(Util.isNull(value) ? getContext().getString(R.string.authing_unspecified) : value);
                    }
                }
            }
        }
    }
}
