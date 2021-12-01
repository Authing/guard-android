package cn.authing.guard;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.util.Util;

public class UserInfoFieldForm extends LinearLayout {

    private ExtendedField field;

    public UserInfoFieldForm(Context context) {
        this(context, null);
    }

    public UserInfoFieldForm(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoFieldForm(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UserInfoFieldForm(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(VERTICAL);
    }

    public ExtendedField getFieldWithValue() {
        ExtendedField f = field.clone();
        String type = field.getInputType();
        if ("text".equals(type)) {
            setValueFromEditText(f);
        } else if ("email".equals(type) || "phone".equals(type)) {
            setFieldWithVerifyCode(f);
        }
        return f;
    }

    public void setField(ExtendedField field) {
        this.field = field;
    }

    private void setValueFromEditText(ExtendedField field) {
        View v = Util.findChildViewByClass(this, EditText.class, true);
        if (v != null) {
            EditText et = (EditText) v;
            String value = et.getText().toString();
            if (!TextUtils.isEmpty(value)) {
                field.setValue(value);
            }
        }
    }

    private void setFieldWithVerifyCode(ExtendedField field) {
        View view = Util.findChildViewByClass(this, EditText.class, true);
        if (view == null) {
            return;
        }

        String value = "";
        EditText et = (EditText) view;
        String v = et.getText().toString();
        if (!TextUtils.isEmpty(v)) {
            value += v;
        }

        view = Util.findChildViewByClass(this, VerifyCodeEditText.class, true);
        if (v != null) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText) view;
            v = verifyCodeEditText.getText().toString();
            if (!TextUtils.isEmpty(v)) {
                value += ":" + v;
            }
        }
        field.setValue(value + ":" + v);
    }
}
