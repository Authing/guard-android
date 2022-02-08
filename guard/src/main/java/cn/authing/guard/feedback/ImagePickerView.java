package cn.authing.guard.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.R;
import cn.authing.guard.activity.FeedbackActivity;
import cn.authing.guard.util.ImageUtil;

public class ImagePickerView extends GridView {

    private final List<Uri> images = new ArrayList<>();
    private PickerAdapter adapter;

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
        adapter = new PickerAdapter();
        setAdapter(adapter);
    }

    private void pickImage() {
        if (images.size() < 8) {
            Intent i = new Intent();
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            ((Activity) getContext()).startActivityForResult(Intent.createChooser(i, "Select Picture"), FeedbackActivity.SELECT_PICTURE);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();

        int space = getHorizontalSpacing();
        setVerticalSpacing(space);
    }

    public void imagePicked(Uri uri) {
        images.add(uri);
        adapter.notifyDataSetChanged();
        getRootView().requestLayout();
    }

    public List<Uri> getImageUris() {
        return images;
    }

    private class PickerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return images.size() + 1;
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
            View view;
            if (position == getCount() - 1) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.authing_image_picker_add, parent, false);
                view.setOnClickListener(v -> pickImage());
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.authing_image_picker_item, parent, false);
            }

            if (position < getCount() - 1) {
                ImageView ivPicked = view.findViewById(R.id.iv_image_picked);
                try {
                    Bitmap bitmap = ImageUtil.getThumbnail(getContext(), images.get(position));
                    ivPicked.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ImageView ivDeleteImage = view.findViewById(R.id.iv_delete_image);
                ivDeleteImage.setOnClickListener(v->{
                    images.remove(position);
                    adapter.notifyDataSetChanged();
                });
            }
            return view;
        }

    }
}
