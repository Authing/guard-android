package cn.authing.guard.internal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.authing.guard.R;
import cn.authing.guard.data.Country;
import cn.authing.guard.util.Util;

public class CountryCodeAdapter extends ArrayAdapter<Country> {

    private static class ViewHolder {
        TextView name;
        TextView code;
    }

    public CountryCodeAdapter(Context context, List<Country> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Country country = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.authing_country_code_picker_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.tv_name);
            viewHolder.code = convertView.findViewById(R.id.tv_code);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String countryName = Util.isCn() ? country.getName() : country.getEnName();
        String name = country.getEmoji() + " " + countryName;
        viewHolder.name.setText(name);
        String code = "+" + country.getCode();
        viewHolder.code.setText(code);
        return convertView;
    }
}
