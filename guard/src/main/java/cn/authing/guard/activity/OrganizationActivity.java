package cn.authing.guard.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.Organization;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.DarkModeManager;

public class OrganizationActivity extends BaseAuthActivity {

    private List<Organization[]> organizations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_organizations);
        DarkModeManager.getInstance().setDarkMode(this);

        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo != null) {
            organizations = userInfo.getOrganizations();
        }

        if (organizations != null) {
            ListView listView = findViewById(R.id.lv_organization);
            ApplicationAdapter adapter = new ApplicationAdapter();
            listView.setAdapter(adapter);
        }
    }

    private class ApplicationAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return organizations.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(OrganizationActivity.this).inflate(R.layout.authing_organization_item, parent, false);
            }

            Organization[] data = organizations.get(position);
            if (data.length > 0) {
                Organization o = data[data.length - 1];
                TextView tv = view.findViewById(R.id.tv_org_name);
                tv.setText(o.getName());
            }
            return view;
        }
    }
}