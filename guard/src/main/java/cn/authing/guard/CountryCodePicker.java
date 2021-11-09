package cn.authing.guard;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.data.Country;
import cn.authing.guard.internal.CountryCodeAdapter;

public class CountryCodePicker extends androidx.appcompat.widget.AppCompatTextView {

    private boolean showCountryName;
    private boolean showRightArrow;
    private TextView label;
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
        showCountryName = array.getBoolean(R.styleable.CountryCodePicker_showCountryName, false);
        showRightArrow = array.getBoolean(R.styleable.CountryCodePicker_showRightArrow, true);
        array.recycle();

        if (showRightArrow) {
            Drawable drawable = context.getDrawable(R.drawable.ic_authing_menu_down);
            Drawable[] drawables = this.getCompoundDrawablesRelative();
            setCompoundDrawablesWithIntrinsicBounds(drawables[0], drawables[1], drawable, drawables[3]);
        }

        selected = new Country("CN", "中国大陆", "China", "86");
        updateSelected(selected);

        setOnClickListener((v)->{
            loadData();

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.country_code_picker);

            ListView lv = (ListView) dialog.findViewById(R.id.lv);
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
            JSONArray array = new JSONArray(getFromRaw());
            for (int i = 0;i < array.length();++i) {
                JSONObject obj = array.getJSONObject(i);
                String shortName = obj.getString("short");
                String name = obj.getString("name");
                String en = obj.getString("en");
                String code = obj.getString("tel");
                Country country = new Country(shortName, name, en, code);
                countries.add(country);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFromRaw() {
        String result = "";
        try {
            InputStream in = getResources().openRawResource(R.raw.country);
            int length = in.available();
            byte[]  buffer = new byte[length];
            in.read(buffer);
            result = new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void updateSelected(Country country) {
        selected = country;
        if (showCountryName) {
            setText(country.getName() + " " + "(+" + country.getCode() + ")");
        } else {
            setText("+" + country.getCode());
        }
    }

    public Country getCountry() {
        return selected;
    }
}
