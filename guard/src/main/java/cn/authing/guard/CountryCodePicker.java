package cn.authing.guard;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Country;
import cn.authing.guard.internal.CountryCodeAdapter;
import cn.authing.guard.util.Util;

public class CountryCodePicker extends androidx.appcompat.widget.AppCompatTextView {

    private final boolean showFlag;
    private final boolean showCountryName;
    private final boolean showRightArrow;
    private List<Country> countries;
    private Country selected;

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

    private void initData(){
        loadData();

        String phoneCountryCode = Util.getPhoneCountryCodeByCache(getContext());
        if (!Util.isNull(phoneCountryCode)){
            for (Country country : countries){
                if (null == country || TextUtils.isEmpty(country.getCode())){
                    continue;
                }
                if (phoneCountryCode.contains(country.getCode())){
                    selected = country;
                    break;
                }
            }
        }
        if (null == selected){
            selected = new Country("CN", "中国大陆", "Chinese Mainland","86", "🇨🇳");
        }

        updateSelected(selected);
    }


    private void initView(){
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

    private void click(View v){
        loadData();

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.authing_country_code_picker);

        ListView lv = dialog.findViewById(R.id.lv);
        CountryCodeAdapter adapter = new CountryCodeAdapter(getContext(), countries);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((parent, view, position, id)->{
            Country c = countries.get(position);
            updateSelected(c);
            dialog.dismiss();
        });
        dialog.setCancelable(true);
        dialog.setTitle("ListView");
        dialog.show();

//            Rect displayRectangle = new Rect();
//            Window window = ((Activity)getContext()).getWindow();
//            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            ViewGroup viewGroup = findViewById(android.R.id.content);
//            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.country_code_picker, viewGroup, false);
//            dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
//            dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
//            builder.setView(dialogView);
//            ListView lv = (ListView) dialogView.findViewById(R.id.lv);
//            CountryCodeAdapter adapter = new CountryCodeAdapter(getContext(), countries);
//            lv.setAdapter(adapter);
//            final AlertDialog alertDialog = builder.create();
//            alertDialog.show();
    }

    private void loadData() {
        if (countries == null) {
            countries = Util.loadCountryList(getContext());
        }
//            JSONArray array = new JSONArray(getFromRaw());
//            for (int i = 0;i < array.length();++i) {
//                JSONObject obj = array.getJSONObject(i);
//                String abbrev = obj.getString("abbrev");
//                String name = obj.getString("name");
//                String code = obj.getString("code");
//                Country country = new Country(abbrev, name, code, obj.getString("emoji"));
//                countries.add(country);
//            }
    }

//    private String getFromRaw() {
//        String result = "";
//        try {
//            InputStream in = getResources().openRawResource(R.raw.country);
//            int length = in.available();
//            byte[]  buffer = new byte[length];
//            in.read(buffer);
//            result = new String(buffer, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    private void updateSelected(Country country) {
        selected = country;
        String s = "";
        if (showFlag) {
            s += country.getEmoji() + " ";
        }
        if (showCountryName) {
            s += (country.getName() + " " + "(");
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
        if (null == selected || null == selected.getCode()){
            return "";
        }
        return "+" + selected.getCode();
    }

    public void setCountry(Country country) {
        updateSelected(country);
    }
}
