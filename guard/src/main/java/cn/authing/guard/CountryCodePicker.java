package cn.authing.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Country;
import cn.authing.guard.dialog.CountryCodePickerDialog;
import cn.authing.guard.util.Util;

public class CountryCodePicker extends androidx.appcompat.widget.AppCompatTextView {

    private final boolean showFlag;
    private final boolean showCountryName;
    private final boolean showRightArrow;
    private List<Country> countries;
    private Country selected;
    private CountryCodePickerDialog dialog;

    public CountryCodePicker(Context context) {
        this(context, null);
    }

    public CountryCodePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountryCodePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("CountryCodePicker");

        setGravity(Gravity.CENTER_VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountryCodePicker);
        showFlag = array.getBoolean(R.styleable.CountryCodePicker_showFlag, false);
        showCountryName = array.getBoolean(R.styleable.CountryCodePicker_showCountryName, false);
        showRightArrow = array.getBoolean(R.styleable.CountryCodePicker_showRightArrow, true);
        array.recycle();

        initData();
        initView();
    }

    private void initData() {
        loadData();

        String phoneCountryCode = Util.getPhoneCountryCodeByCache(getContext());
        if (!Util.isNull(phoneCountryCode)) {
            for (Country country : countries) {
                if (null == country || TextUtils.isEmpty(country.getCode())) {
                    continue;
                }
                if (phoneCountryCode.contains(country.getCode())) {
                    selected = country;
                    break;
                }
            }
        }
        if (null == selected) {
            selected = new Country("CN", "中国大陆", "zhong guo da lu",
                    "Chinese Mainland", "86", "🇨🇳");
        }

        updateSelected(selected);
    }


    private void initView() {
        if (showRightArrow) {
            Drawable drawable = getContext().getDrawable(R.drawable.ic_authing_menu_down);
            Drawable[] drawables = this.getCompoundDrawablesRelative();
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3]);
        }
        setOnClickListener(this::click);
        Authing.getPublicConfig(config -> {
            boolean isEnable = config != null && config.isInternationalSmsEnable();
            if (isEnable) {
                setVisibility(VISIBLE);
                setEnabled(true);
            } else {
                setEnabled(false);
                setVisibility(GONE);
            }
        });
    }

    private void click(View v) {
        loadData();

        if (dialog == null) {
            dialog = new CountryCodePickerDialog(getContext(), R.style.BaseDialog);
            dialog.setData(countries);
            dialog.setOnDialogClickListener(country -> {
                updateSelected(country);
                dialog.dismiss();
            });
        }
        dialog.show();
    }

    private void loadData() {
        if (countries == null) {
            countries = Util.loadCountryList(getContext());
        }
    }

    private void updateSelected(Country country) {
        selected = country;
        String s = "";
        if (showFlag) {
            s += country.getEmoji() + " ";
        }
        if (showCountryName) {
            String countryName = Util.isCn() ? country.getName() : country.getEnName();
            s += (countryName + " ");
        }

        s += "+" + country.getCode();

        if (showCountryName) {
            s += ")";
        }
        setText(s);
    }

    public Country getCountry() {
        return selected;
    }

    public String getCountryCode() {
        if (null == selected || null == selected.getCode()) {
            return "";
        }
        return "+" + selected.getCode();
    }

    public void setCountry(Country country) {
        updateSelected(country);
    }
}
