package cn.authing.guard.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import cn.authing.guard.R;

public class DarkModeManager {

    private DarkModeManager() {
    }

    public static DarkModeManager getInstance() {
        return DarkModeManager.DarkModeManagerHolder.mInstance;
    }

    @SuppressLint("StaticFieldLeak")
    private static final class DarkModeManagerHolder {
        static final DarkModeManager mInstance = new DarkModeManager();
    }

    public void setDarkMode(Activity activity){
        if (activity == null){
            return;
        }
        View contentView = ((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
        if (contentView != null){
            contentView.setBackgroundResource(R.color.authing_page_bg);
        }
    }

}
