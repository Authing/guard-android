package cn.authing.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.mfa.MFAListView;
import cn.authing.guard.util.Util;

public class MFAListDialog extends Dialog {

    private final Context mContext;
    private String hideType;

    public MFAListDialog(@NonNull Context context, String hideType) {
        super(context);
        this.mContext = context;
        this.hideType = hideType;
    }

    public MFAListDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public MFAListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected MFAListDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(R.drawable.authing_social_more_dialog_background);
        getWindow().setGravity(Gravity.CENTER);
        Window window = getWindow();
        int padding = (int) Util.dp2px(getContext(), 24);
        int paddingBottom = (int) Util.dp2px(getContext(), 40);
        window.getDecorView().setPadding(padding, padding, padding, paddingBottom);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialogAnimation);

        MFAListView mfaListView = new MFAListView(mContext);
        mfaListView.setMfaListItemClickListener(type -> dismiss());
        mfaListView.setHideType(hideType);
        setContentView(mfaListView);
    }

}
