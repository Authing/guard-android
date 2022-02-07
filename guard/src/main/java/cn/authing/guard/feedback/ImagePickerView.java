package cn.authing.guard.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import androidx.annotation.NonNull;

import cn.authing.guard.R;

public class ImagePickerView extends GridView {


    public ImagePickerView(Context context) {
        this(context, null);
    }

    public ImagePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImagePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    private class PickerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
            View listitemView = convertView;
            if (listitemView == null) {
                if (position == getCount() - 1) {
                    listitemView = LayoutInflater.from(getContext()).inflate(R.layout.authing_image_picker_add, parent, false);
                } else {

                }

            }
//            CourseModel courseModel = getItem(position);
//            TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
//            ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);
//            courseTV.setText(courseModel.getCourse_name());
//            courseIV.setImageResource(courseModel.getImgid());
            return listitemView;
        }
    }
}
