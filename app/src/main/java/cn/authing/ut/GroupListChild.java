package cn.authing.ut;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.authing.R;

public class GroupListChild extends GroupList{

    public GroupListChild(String title) {
        super(title);
    }

    @Override
    public int getResource() {
        return R.layout.activity_ut_grouplist_child;
    }

    @Override
    public List<GroupListChild> getChild() {
        return null;
    }

    @Override
    public void buildView(View v, int position, int count) {
        TextView textView = v.findViewById(R.id.child_text);
        textView.setText((position+1) +" "+ getTitle());
    }



}
