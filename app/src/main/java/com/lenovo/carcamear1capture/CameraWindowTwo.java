package com.lenovo.carcamear1capture;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class CameraWindowTwo implements SurfaceHolder.Callback, Camera.PreviewCallback{
    private static final String TAG = CameraWindowTwo.class.getSimpleName();

    private static WindowManager windowManager;

    private static Context applicationContext;

    private static SurfaceView dummyCameraView;
    private Camera camera;
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(10);
    private Camera.Parameters parameters;

    int width = 1920;

    int height = 1080;

    int framerate = 15;

    int biterate = 25000;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceview;
    private byte[] cameraData;
    private boolean can=true;

    /**

     * 显示全局窗体

     *

     * @param context

     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show(Context context) {

        if (applicationContext == null) {
            applicationContext = context.getApplicationContext();

            windowManager = (WindowManager) applicationContext

                    .getSystemService(Context.WINDOW_SERVICE);

            View sufaceLayout = LayoutInflater.from(applicationContext).inflate(R.layout.float_layout_two, null);
            surfaceview=sufaceLayout.findViewById(R.id.floatSurface);
            surfaceHolder = surfaceview.getHolder();
            surfaceHolder.addCallback(this);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            params.width = 100;

            params.height = 100;

            params.alpha = 100;
            params.gravity= Gravity.TOP;
            params.horizontalMargin=200;

            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            // 屏蔽点击事件

            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;


            windowManager.addView(sufaceLayout, params);

            Log.e(TAG, TAG + " showing");

        }

    }

    /**

     * @return 获取窗体视图

     */

    public static SurfaceView getDummyCameraView() {

        return dummyCameraView;

    }

    /**

     * 隐藏窗体

     */

    public static void dismiss() {

        try {

            if (windowManager != null && dummyCameraView != null) {

                windowManager.removeView(dummyCameraView);

                Log.e(TAG, TAG + " dismissed");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera = getBackCamera();
        startcamera(camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        Camera.Size size = camera.getParameters().getPreviewSize();
        try{
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if(image!=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80 ,stream);
                byte[] jpgByte = stream.toByteArray();
                cameraData=jpgByte;
//                if (can){
//                    can=false;
//                    fileToBytes(cameraData, "/storage/emulated/0/Android/data/com.lenovo.mutimodecamera/files","save.jpg");
//                }
            }
        } catch (Exception e){

        }

    }
    public static void fileToBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        Log.e("TAG","file path is"+filePath);
        try {

            file = new File(filePath + fileName);
            if (!file.exists()){
                //文件夹不存在 生成
                file.mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG","file error is ========="+e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startcamera(Camera mCamera){
        if(mCamera != null){
            try {
                mCamera.setPreviewCallback(this);
                mCamera.setDisplayOrientation(90);
                if(parameters == null){
                    parameters = mCamera.getParameters();
                }
                parameters = mCamera.getParameters();
                parameters.setPreviewFormat(ImageFormat.NV21);
                parameters.setPreviewSize(width, height);
                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getImageData(){
        return cameraData;
    }

    @TargetApi(9)
    private Camera getBackCamera() {
        Camera c = null;
        try {
            c = Camera.open(1); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }
}
