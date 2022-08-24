package cn.authing.ut;

import android.view.View;

import java.util.List;

public abstract class GroupList {

    private final String mTitle;

    public GroupList(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public abstract List<GroupListChild> getChild();

    public abstract int getResource();

    public abstract void buildView(View v, int position, int count);
}
