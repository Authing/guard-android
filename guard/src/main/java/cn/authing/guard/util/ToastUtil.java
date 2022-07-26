package cn.authing.guard.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.authing.guard.R;

public class ToastUtil {

    public static void controlToastTime(final Toast toast, int duration) {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

    public static void showTop(Context context, String text) {
        showTop(context, text, 0);
    }

    public static void showTop(Context context, String text, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.authing_toast_layout_top, null);
        TextView tv_msg = view.findViewById(R.id.toast_text);
        tv_msg.setText(text);
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        if (duration != 0){
            controlToastTime(toast, duration);
        }
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setView(view);
        toast.show();
    }

    public static void showCenter(Context context, String text) {
        showCenter(context, text, 0);
    }

    public static void showCenter(Context context, String text, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.authing_toast_layout_center, null);
        TextView tv_msg = view.findViewById(R.id.toast_text);
        tv_msg.setText(text);
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        if (duration != 0){
            controlToastTime(toast, duration);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.show();
    }

}
