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
import cn.authing.guard.data.Application;
import cn.authing.guard.data.Resource;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.DarkModeManager;

public class AuthorizedResourcesActivity extends BaseAuthActivity {

    private List<Resource> resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_authorized_resources);
        DarkModeManager.getInstance().setDarkMode(this);
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo != null) {
            resources = userInfo.getResources();
        }

        if (resources != null) {
            ListView listView = findViewById(R.id.lv_resources);
            ResourceAdapter adapter = new ResourceAdapter();
            listView.setAdapter(adapter);
        }
    }

    private class ResourceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return resources.size();
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
                view = LayoutInflater.from(AuthorizedResourcesActivity.this).inflate(R.layout.authing_resource_item, parent, false);
            }

            Resource data = resources.get(position);
            TextView tv = view.findViewById(R.id.tv_res_code);
            tv.setText(data.getCode());
            tv = view.findViewById(R.id.tv_res_type);
            tv.setText(data.getType());
            return view;
        }
    }
}