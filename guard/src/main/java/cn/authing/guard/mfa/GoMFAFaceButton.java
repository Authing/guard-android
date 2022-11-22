package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;

public abstract class GoMFAFaceButton extends androidx.appcompat.widget.AppCompatButton implements AuthActivity.EventListener {

    public GoMFAFaceButton(@NonNull Context context) {
        this(context, null);
    }

    public GoMFAFaceButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoMFAFaceButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_white));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_start_face_verify));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackground(getContext().getDrawable(R.drawable.authing_button_background));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minWidth") == null) {
            setMinWidth(0);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "minHeight") == null) {
            setMinHeight(0);
        }

        setOnClickListener((v) -> {
            if (context instanceof AuthActivity) {
                initPermission();
            }
        });
    }

    private void initPermission() {
        int REQUEST_CODE_CONTACT = 102;
        String[] permissions = {Manifest.permission.CAMERA};
        //验证是否许可权限
        boolean hasPermission = true;
        for (String str : permissions) {
            if (getContext().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                if (getContext() instanceof AuthActivity) {
                    ((AuthActivity) getContext()).subscribe(AuthActivity.EVENT_BIND_FACE_CARE_PERMISSION, this);
                }
                ((Activity) getContext()).requestPermissions(permissions, REQUEST_CODE_CONTACT);
                hasPermission = false;
            }
        }

        if (hasPermission) {
            next();
        }
    }

    public abstract void next();

    @Override
    public void happened(String what) {
        next();
    }
}
