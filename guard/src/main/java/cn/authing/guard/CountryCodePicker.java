package cn.authing.guard;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ListView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import cn.authing.guard.data.Country;
import cn.authing.guard.internal.CountryCodeAdapter;

public class CountryCodePicker extends androidx.appcompat.widget.AppCompatTextView {

    private boolean showFlag;
    private boolean showCountryName;
    private boolean showRightArrow;
    private ArrayList<Country> countries;
    private Country selected;

    public CountryCodePicker(Context context) {
        this(context, null);
    }

    public CountryCodePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountryCodePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER_VERTICAL);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountryCodePicker);
        showFlag = array.getBoolean(R.styleable.CountryCodePicker_showFlag, false);
        showCountryName = array.getBoolean(R.styleable.CountryCodePicker_showCountryName, false);
        showRightArrow = array.getBoolean(R.styleable.CountryCodePicker_showRightArrow, true);
        array.recycle();

        if (showRightArrow) {
            Drawable drawable = context.getDrawable(R.drawable.ic_authing_menu_down);
            Drawable[] drawables = this.getCompoundDrawablesRelative();
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3]);
        }

        selected = new Country("CN", "中国大陆", "86", "🇨🇳");
        updateSelected(selected);

        setOnClickListener((v)->{
            loadData();

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.country_code_picker);

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
        });
        loadData();
    }

    private void loadData() {
        if (countries != null) {
            return;
        }

        countries = new ArrayList<>();
        try {
//            JSONArray array = new JSONArray(getFromRaw());
//            for (int i = 0;i < array.length();++i) {
//                JSONObject obj = array.getJSONObject(i);
//                String abbrev = obj.getString("abbrev");
//                String name = obj.getString("name");
//                String code = obj.getString("code");
//                Country country = new Country(abbrev, name, code, obj.getString("emoji"));
//                countries.add(country);
//            }

            InputStream inputStream = getResources().openRawResource(R.raw.country);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            do {
                line = reader.readLine();
                if (line != null) {
                    String[] data = line.split(",");
                    Country country = new Country(data[0], data[3], data[2], data[1]);
                    countries.add(country);
                }
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setCountry(Country country) {
        updateSelected(country);
    }
}
