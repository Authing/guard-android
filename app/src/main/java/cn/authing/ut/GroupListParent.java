package cn.authing.ut;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.authing.R;

public class GroupListParent extends GroupList {

    private final List<GroupListChild> mChildList;

    public GroupListParent(String title, List<GroupListChild> childList) {
        super(title);
        mChildList = childList;
    }

    @Override
    public int getResource() {
        return R.layout.activity_ut_grouplist_parent;
    }

    @Override
    public List<GroupListChild> getChild() {
        return mChildList;
    }

    @Override
    public void buildView(View v, int position, int count) {
        TextView textView = v.findViewById(R.id.parent_text);
        textView.setText(getTitle() + "(" + count + ")");
    }
}
