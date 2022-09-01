package cn.authing.guard.internal;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class DatePickerView extends LinearLayout {

    private TextView textView;

    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOrientation(HORIZONTAL);

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.authing_text_large_size));
        textView.setTextColor(context.getResources().getColor(R.color.authing_text_black));
        textView.setBackground(null);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding((int) Util.dp2px(context, 8), 0, 0, 0);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(textView, layoutParams);
        textView.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.authing_date_picker_dialog, null);
        final DatePicker datePicker = view.findViewById(R.id.date_picker);
        datePicker.setCalendarViewShown(false);
        builder.setView(view);
        builder.setTitle(getContext().getString(R.string.authing_picker_date));
        builder.setPositiveButton(getContext().getString(R.string.authing_confirm), (dialog, which) -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int dayOfMonth = datePicker.getDayOfMonth();
            String date = year + "/" + month + "/" + dayOfMonth;
            textView.setText(date);
            dialog.cancel();
        });
        builder.setNegativeButton(getContext().getString(R.string.authing_cancel), (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.authing_dialog_background);
        alertDialog.show();
    }

    public String getText() {
        return textView.getText().toString();
    }

}
