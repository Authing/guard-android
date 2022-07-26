package cn.authing.guard.feedback;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.Callback;
import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.network.FeedbackClient;
import cn.authing.guard.network.Uploader;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class FeedbackSubmitButton extends PrimaryButton {
    public FeedbackSubmitButton(@NonNull Context context) {
        this(context, null);
    }

    public FeedbackSubmitButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public FeedbackSubmitButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("FeedbackSubmitButton");

        setOnClickListener(v -> submit());
    }

    private void submit() {
        View v = Util.findViewByClass(this, AccountEditText.class);
        if (v == null) {
            return;
        }

        AccountEditText editText = (AccountEditText)v;
        String contact = editText.getText().toString();
        if (Util.isNull(contact)) {
            Util.setErrorText(this, getContext().getString(R.string.authing_contact_info_cannot_be_empty));
            return;
        }

        int type = 0;
        v = Util.findViewByClass(this, IssueLayout.class);
        if (v != null) {
            IssueLayout issueLayout = (IssueLayout)v;
            type = issueLayout.getType();
        }

        String des = "";
        v = Util.findViewByClass(this, FeedbackDescriptionEditText.class);
        if (v != null) {
            FeedbackDescriptionEditText et = (FeedbackDescriptionEditText)v;
            des = et.getText();
        }

        List<Uri> uriList = null;
        v = Util.findViewByClass(this, ImagePickerView.class);
        if (v != null) {
            uriList = ((ImagePickerView)v).getImageUris();
        }

        startLoadingVisualEffect();
        List<String> uploadedImageURLs = new ArrayList<>();
        if (uriList != null && uriList.size() > 0) {
            int finalType = type;
            String finalDes = des;
            uploadImages(uriList, uploadedImageURLs, (ok, data) -> {
                if (ok) {
                    doSubmit(contact, finalType, finalDes, uploadedImageURLs);
                } else {
                    stopLoadingVisualEffect();
                    Util.setErrorText(this, data);
                }
            });
        } else {
            doSubmit(contact, type, des, uploadedImageURLs);
        }
    }

    private void uploadImages(List<Uri> uriList, List<String> uploadedImageURLs, Callback<String> callback) {
        if (uriList.size() == 0) {
            callback.call(true, null);
            return;
        }

        Uri uri = uriList.get(0);
        uriList.remove(0);

        Activity activity = (Activity)getContext();
        try {
            InputStream in = activity.getContentResolver().openInputStream(uri);
            Uploader.uploadImage(in, (ok, data) -> {
                if (ok) {
                    uploadedImageURLs.add(data);
                    uploadImages(uriList, uploadedImageURLs, callback);
                } else {
                    callback.call(false, data);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.call(false, "Exception when uploading image");
        }
    }

    private void doSubmit(String contact, int type, String des, List<String> images) {
        FeedbackClient.feedback(contact, type, des, images, ((ok, data) -> {
            stopLoadingVisualEffect();
            if (ok) {
                post(()-> ToastUtil.showTop(getContext(), getContext().getString(R.string.authing_submit_success)));
                postDelayed(() -> {
                    Activity activity = (Activity) getContext();
                    activity.finish();
                }, 1000);
            } else {
                Util.setErrorText(this, data);
            }
        }));
    }
}
