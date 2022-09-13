package cn.authing.guard.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.authing.guard.R;
import cn.authing.guard.data.Country;
import cn.authing.guard.util.Util;


public class CountryCodePickerAdapter extends BaseAdapter {

    private List<Country> mBeans;

    public CountryCodePickerAdapter(List<Country> beans) {
        mBeans = beans;
    }

    public void updateListView(List<Country> beans) {
        this.mBeans = beans;
        notifyDataSetChanged();
    }

    public List<Country> getList() {
        return mBeans;
    }

    @Override
    public int getCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.authing_country_code_item, parent, false);
            mHolder.txtName = convertView.findViewById(R.id.txt_name);
            mHolder.txtTag = convertView.findViewById(R.id.txt_tag);
            mHolder.txtNum = convertView.findViewById(R.id.txt_num);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setId(position);
        Country country = mBeans.get(position);
        String countryName = Util.isCn() ? country.getName() : country.getEnName();
        //String name = country.getEmoji() + " " + countryName;
        mHolder.txtName.setText(countryName);
        mHolder.txtNum.setText("+"+country.getCode());
        String firstSpell = country.getFirstSpell().toUpperCase();
        if (position == 0) {
            mHolder.txtTag.setVisibility(View.VISIBLE);
            mHolder.txtTag.setText(firstSpell);
        } else {
            String lastFirstSpell = mBeans.get(position - 1).getFirstSpell().toUpperCase();
            if (firstSpell.equals(lastFirstSpell)) {
                mHolder.txtTag.setVisibility(View.GONE);
            } else {
                mHolder.txtTag.setVisibility(View.VISIBLE);
                mHolder.txtTag.setText(firstSpell);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        private TextView txtName, txtTag, txtNum;
    }
}
