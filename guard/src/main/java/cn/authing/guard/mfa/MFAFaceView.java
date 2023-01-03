package cn.authing.guard.mfa;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.Uploader;
import cn.authing.guard.util.FastYuvToRGB;
import cn.authing.guard.util.Util;


public class MFAFaceView extends RelativeLayout {

    private SurfaceView surfaceView;
    private FaceCircleView faceCircleView;
    private ImageView imageView;
    private TextView textView;
    private Camera mCamera;

    private volatile boolean isIdentify = false;
    private int previewWidth;
    private int previewHeight;
    private int checkFaceNum = 0;
    private int checkFaceErrorNum = 0;
    private int bindPassNum = 0;
    private int flag;
    private Bitmap firstBitmap;
    private Bitmap secondBitmap;
    private String firstBitmapKey;
    private String secondBitmapKey;

    public static int MFA_TYPE_BIND = 0;
    public static int MFA_TYPE_VERIFY = 1;
    private int currentMfaType;


    public MFAFaceView(Context context) {
        this(context, null);
    }

    public MFAFaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MFAFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MFAFaceView);
        currentMfaType = array.getInt(R.styleable.MFAFaceView_mfaFaceType, 0);
        array.recycle();

        initView(context);
    }

    private void initView(Context context) {
        surfaceView = new SurfaceView(context);
        LayoutParams surfaceParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(surfaceView, surfaceParams);

        faceCircleView = new FaceCircleView(context);
        LayoutParams circleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(faceCircleView, circleParams);

        imageView = new ImageView(context);
        LayoutParams imageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageParams.topMargin = (int) Util.dp2px(context, 40);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.CENTER_HORIZONTAL);
        addView(imageView, imageParams);
        startLoading();

        textView = new TextView(context);
        LayoutParams textParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.topMargin = (int) Util.dp2px(context, 340);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getContext().getColor(R.color.authing_text_gray));
        textView.setText(getContext().getString(R.string.authing_again_face_verify));
        addView(textView, textParams);
        textView.setVisibility(GONE);

        startPreview();
    }

    private AnimatedVectorDrawable loadingDrawable;

    private void startLoading() {
        if (loadingDrawable == null) {
            loadingDrawable = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.ic_authing_face_animated_loading);
            imageView.setImageDrawable(loadingDrawable);
        }
        loadingDrawable.start();
    }

    private void stopLoading() {
        if (loadingDrawable != null && loadingDrawable.isRunning()) {
            loadingDrawable.stop();
        }
    }

    public void setCurrentMfaType(int currentType) {
        this.currentMfaType = currentType;
    }

    /**
     * 开始预览
     */
    private void startPreview() {
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mCamera = getCustomCamera();
                mCamera.setPreviewCallback((data, camera) -> {
                    // 是否已经检测成功，如果是则不继续检测
                    if (!isIdentify) {
                        checkFaces(data, camera);
                    }
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mCamera != null) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    // 选择合适的图片尺寸，必须是手机支持的尺寸
                    List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
                    // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
                    if (sizeList.size() > 1) {
                        for (int j = 0; j < sizeList.size(); j++) {
                            Camera.Size size = sizeList.get(j);
                            previewWidth = size.width;
                            previewHeight = size.height;
                        }
                    }

                    //设置照片的大小
                    parameters.setPictureSize(previewWidth, previewHeight);
                    mCamera.setDisplayOrientation(90);
                    mCamera.setParameters(parameters);
                    try {
                        mCamera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //调用相机预览功能
                    mCamera.startPreview();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (null != mCamera) {
                    holder.removeCallback(this);
                    mCamera.setPreviewCallback(null);
                    //停止预览
                    mCamera.stopPreview();
                    mCamera.lock();
                    //释放相机资源
                    mCamera.release();
                    mCamera = null;
                }
            }
        });
    }

    /**
     * 获取打开相机
     */
    private Camera getCustomCamera() {
        if (null == mCamera) {
            //Camera.open()方法说明：2.3以后支持多摄像头，所以开启前可以通过getNumberOfCameras先获取摄像头数目，
            // 再通过 getCameraInfo得到需要开启的摄像头id，然后传入Open函数开启摄像头，
            // 假如摄像头开启成功则返回一个Camera对象
            try {
                mCamera = Camera.open(Camera.getNumberOfCameras() - 1);
                //预览画面默认是横屏的，需要旋转90度
                mCamera.setDisplayOrientation(90);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mCamera;
    }

    /**
     * 检测是否存在人脸
     */
    private void checkFaces(byte[] data, Camera camera) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        Bitmap bitmap = new FastYuvToRGB(getContext()).convertYuvToRGB(data, size.width, size.height);
        Bitmap bp = rotateMyBitmap(bitmap);
        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap565 = bp.copy(Bitmap.Config.RGB_565, true);
        //合并图片
        Bitmap screenBitMap = zoomBitmap(bitmap565, faceCircleView.getWidth(), faceCircleView.getHeight());
        Bitmap newBP = mergeBitmap(screenBitMap, getViewBitmap());

        //检测图片是否存在人脸
        FaceDetector face_detector = new FaceDetector(newBP.getWidth(), newBP.getHeight(), 1);
        FaceDetector.Face[] faces = new FaceDetector.Face[1];
        int face_count = face_detector.findFaces(newBP, faces);
        if (checkFaceErrorNum >= 5) {
            textView.setVisibility(View.VISIBLE);
        }
        if (face_count > 0) {
            if (checkFaceNum >= 3) {
                if (firstBitmap == null) {
                    firstBitmap = newBP;
                } else {
                    if (secondBitmap == null && currentMfaType == MFA_TYPE_BIND) {
                        secondBitmap = newBP;
                    }
                }
                doMFA();
                checkFaceNum = 0;
            } else {
                checkFaceNum++;
            }
        } else {
            checkFaceErrorNum++;
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 旋转相机预览图
     */
    public Bitmap rotateMyBitmap(Bitmap bmp) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    /**
     * 圆形透明遮挡层图片
     */
    public Bitmap getViewBitmap() {
        View rootView = faceCircleView;
        rootView.buildDrawingCache();
        //允许当前窗口保存缓存信息
        rootView.setDrawingCacheEnabled(true);
        //去掉状态栏高度
        return Bitmap.createBitmap(rootView.getDrawingCache(), 0, 0, rootView.getWidth(), rootView.getHeight());
    }

    /**
     * 合并两张bitmap图片
     */
    private Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(secondBitmap.getWidth(), secondBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, new Matrix(), null);
        return bitmap;
    }

    /**
     * 图片缩放
     *
     * @param bitmap bitmap 对象
     * @param w      要缩放的宽度
     * @param h      要缩放的高度
     * @return newBmp 新 Bitmap对象
     */
    private Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    private void doMFA() {
        if (currentMfaType == MFA_TYPE_BIND) {
            if (firstBitmap != null && secondBitmap != null) {
                isIdentify = true;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                firstBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream firstInputStream = new ByteArrayInputStream(baos.toByteArray());
                Uploader.uploadFaceImage(firstInputStream, (ok, uploadedUrl) -> {
                    if (ok && !Util.isNull(uploadedUrl)) {
                        firstBitmapKey = uploadedUrl;
                    }
                    mfaBind(1);
                });

                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                secondBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos2);
                InputStream secondInputStream = new ByteArrayInputStream(baos2.toByteArray());
                Uploader.uploadFaceImage(secondInputStream, (ok, uploadedUrl) -> {
                    if (ok && !Util.isNull(uploadedUrl)) {
                        secondBitmapKey = uploadedUrl;
                    }
                    mfaBind(2);
                });
            }
        } else if (currentMfaType == MFA_TYPE_VERIFY) {
            if (firstBitmap != null) {
                isIdentify = true;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                firstBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                Uploader.uploadFaceImage(inputStream, (ok, uploadedUrl) -> {
                    if (ok && !Util.isNull(uploadedUrl)) {
                        AuthClient.mfaVerifyByFace(uploadedUrl, (AuthCallback<UserInfo>) this::mfaVerifyDone);
                    } else {
                        mfaVerifyFailed();
                    }
                });
            }
        }
    }

    private void mfaBind(int f) {
        flag |= f;
        if (flag == 3) {
            if (!TextUtils.isEmpty(firstBitmapKey) && !TextUtils.isEmpty(secondBitmapKey)) {
                AuthClient.mfaBindByFace(firstBitmapKey, secondBitmapKey, (AuthCallback<UserInfo>) this::mfaBindDone);
            } else {
                mfaBindFailed();
            }
        }
    }

    private void mfaBindDone(int code, String message, UserInfo userInfo) {
        if (code == 200) {
            next(R.layout.authing_mfa_face_bind_success);
        } else {
            mfaBindFailed();
        }
    }

    private void mfaBindFailed() {
        firstBitmap = null;
        secondBitmap = null;
        firstBitmapKey = null;
        secondBitmapKey = null;
        if (bindPassNum < 5) {
            bindPassNum++;
            isIdentify = false;
            flag = 0;
        } else {
            next(R.layout.authing_mfa_face_bind_failed);
        }
    }

    private void mfaVerifyDone(int code, String message, UserInfo userInfo) {
        if (code == 200) {
            next(R.layout.authing_mfa_face_verify_success);
        } else {
            mfaVerifyFailed();
        }
    }

    private void mfaVerifyFailed() {
        firstBitmap = null;
        firstBitmapKey = null;
        if (bindPassNum < 5) {
            bindPassNum++;
            isIdentify = false;
        } else {
            next(R.layout.authing_mfa_face_verify_failed);
        }
    }

    private void next(int contentLayoutId) {
        firstBitmap = null;
        secondBitmap = null;
        firstBitmapKey = null;
        secondBitmapKey = null;
        stopPreview();
        stopLoading();

        if (getContext() instanceof AuthActivity){
            if (checkBiometricBind((AuthActivity)getContext())){
                return;
            }
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.putExtra(AuthActivity.AUTH_FLOW, flow);
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, contentLayoutId);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            //activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    protected boolean checkBiometricBind(AuthActivity activity){
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        boolean isBiometricBind = (Boolean) flow.getData().get(AuthFlow.KEY_BIOMETRIC_BIND);
        if (isBiometricBind){
            activity.setResult(AuthActivity.BIOMETRIC_BIND_OK);
            activity.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPreview();
        stopLoading();
    }
}
