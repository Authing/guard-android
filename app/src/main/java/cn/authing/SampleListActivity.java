package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SampleListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = (ListView) findViewById(R.id.lv_samples);
        String[] from = { "Authing Standard Login", "Android Default Style Login" };
        final ArrayAdapter adapter = new ArrayAdapter(this,
                R.layout.sample_list_item, from);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long pos) {
                if (pos == 0) {
                    Intent intent = new Intent(SampleListActivity.this, AuthingLoginActivity.class);
                    startActivity(intent);
                } else if (pos == 1) {
                    Intent intent = new Intent(SampleListActivity.this, AndroidLoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}