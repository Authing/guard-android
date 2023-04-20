package cn.authing.guard.activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.Role;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.DarkModeManager;
import cn.authing.guard.util.Util;

public class RolesActivity extends BaseAuthActivity {

    private List<Role> roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_roles);
        DarkModeManager.getInstance().setDarkMode(this);
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo != null) {
            roles = userInfo.getRoles();
        }

        if (roles != null) {
            ListView listView = findViewById(R.id.lv_roles);
            RoleAdapter adapter = new RoleAdapter();
            listView.setAdapter(adapter);
        }
    }

    private class RoleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return roles.size();
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
                view = LayoutInflater.from(RolesActivity.this).inflate(R.layout.authing_role_item, parent, false);
            }

            Role role = roles.get(position);
            TextView tvCode = view.findViewById(R.id.tv_role_code);
            tvCode.setText(role.getCode());
            TextView tvNS = view.findViewById(R.id.tv_role_namespace);
            tvNS.setText(role.getNamespace());
            TextView tvDes = view.findViewById(R.id.tv_role_des);
            String des = role.getDescription();
            if (Util.isNull(des)) {
                tvDes.setText(getString(R.string.authing_no_description));
            } else {
                tvDes.setText(des);
            }
            return view;
        }
    }
}