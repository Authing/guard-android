package cn.authing.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.authing.guard.R;


public class LetterDialog {

    private final Dialog selectDialog;
    private final TextView text;

    public LetterDialog(Context context, int weight, int height, int textColor, float textSize, Drawable bg) {
        selectDialog = new Dialog(context, R.style.CountryCodePickerLetterDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.authing_country_code_picker_letter_dialog, null);
        RelativeLayout layout = view.findViewById(R.id.letter_text_layout);
        text = view.findViewById(R.id.letter_text);

        layout.setBackground(bg);
        text.setTextColor(textColor);
        text.setTextSize(textSize);

        selectDialog.getWindow().setGravity(Gravity.BOTTOM);
        selectDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = selectDialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = weight;
        lp.height = height;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        selectDialog.setContentView(view);
    }

    public void dismissD() {
        if (selectDialog != null && selectDialog.isShowing()) {
            selectDialog.dismiss();
        }
    }

    public void showD(String str) {
        if (text != null) text.setText(str);
        if (selectDialog != null && !selectDialog.isShowing()) {
            selectDialog.show();
        }
    }
}
