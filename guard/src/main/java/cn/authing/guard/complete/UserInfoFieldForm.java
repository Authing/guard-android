package cn.authing.guard.complete;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Country;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.internal.DatePickerView;
import cn.authing.guard.util.Util;

public class UserInfoFieldForm extends LinearLayout {

    private static String[] GENDERS;
    private static Country[] COUNTRIES;

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

        Analyzer.report("UserInfoFieldForm");

        setOrientation(VERTICAL);

        GENDERS = new String[]{context.getString(R.string.authing_male), context.getString(R.string.authing_female)};

        List<Country> countryList = Util.loadCountryList(context);
        COUNTRIES = new Country[countryList.size()];
        for (int i = 0;i < countryList.size();++i) {
            COUNTRIES[i] = countryList.get(i);
        }
    }

    public ExtendedField getFieldWithValue() {
        ExtendedField f = field.clone();
        String type = field.getInputType();
        if ("text".equals(type)) {
            setValueFromEditText(f);
        } else if ("email".equals(type) || "phone".equals(type)) {
            setFieldWithVerifyCode(f);
        } else if ("select".equals(type)) {
            setValueFromSelect(f);
        } else if ("datetime".equals(type)) {
            setValueFromDatePicker(f);
        }
        return f;
    }

    public void setField(ExtendedField field) {
        this.field = field;

        View view = Util.findChildViewByClass(this, Spinner.class, false);
        if (view != null) {
            Spinner spinner = (Spinner) view;
            if ("select".equals(field.getInputType())) {
                if ("gender".equals(field.getName())) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            R.layout.authing_spinner_item,
                            GENDERS);
                    spinner.setAdapter(adapter);
                } else if ("country".equals(field.getName())) {
                    ArrayAdapter<Country> adapter = new ArrayAdapter<>(
                            getContext(),
                            R.layout.authing_spinner_item,
                            COUNTRIES);
                    spinner.setAdapter(adapter);
                }
            }
        }
    }

    private void setValueFromEditText(ExtendedField field) {
        View v = Util.findChildViewByClass(this, EditText.class, false);
        if (v != null) {
            EditText et = (EditText) v;
            String value = et.getText().toString();
            if (!TextUtils.isEmpty(value)) {
                field.setValue(value);
            }
        }
    }

    private void setFieldWithVerifyCode(ExtendedField field) {
        View view = Util.findChildViewByClass(this, EditText.class, false);
        if (view == null) {
            return;
        }

        String value = "";
        CountryCodePicker countryCodePickerView = (CountryCodePicker)Util.findChildViewByClass(
                this, CountryCodePicker.class, true);
        if (countryCodePickerView != null) {
            value = countryCodePickerView.getCountryCode() + ":";
        }
        EditText et = (EditText) view;
        String v = et.getText().toString();
        if (!TextUtils.isEmpty(v)) {
            value += v;
        }

        view = Util.findChildViewByClass(this, VerifyCodeEditText.class, false);
        if (view != null) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText) view;
            v = verifyCodeEditText.getText().toString();
            if (!TextUtils.isEmpty(v)) {
                value += ":" + v;
            }
        }
        field.setValue(value + ":" + v);
    }

    private void setValueFromSelect(ExtendedField field) {
        View view = Util.findChildViewByClass(this, Spinner.class, false);
        if (view == null) {
            return;
        }

        Spinner spinner = (Spinner) view;
        Object selected = spinner.getSelectedItem();
        if (selected != null) {
            if (selected instanceof Country) {
                Country country = (Country) selected;
                field.setValue(country.getAbbrev());
            } else if ("gender".equals(field.getName())) {
                int pos = spinner.getSelectedItemPosition();
                if (pos == 0) {
                    field.setValue("M");
                } else if (pos == 1) {
                    field.setValue("F");
                }
            }
        }
    }

    private void setValueFromDatePicker(ExtendedField field) {
        View view = Util.findChildViewByClass(this, DatePickerView.class, false);
        if (view == null) {
            return;
        }

        DatePickerView datePickerView = (DatePickerView) view;
        String selected = datePickerView.getText();
        field.setValue(selected);
    }
}
