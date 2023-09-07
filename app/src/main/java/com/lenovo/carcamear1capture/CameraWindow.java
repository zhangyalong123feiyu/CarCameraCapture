package com.lenovo.mutimodecamera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.lenovo.carcontroler.utils.WebSocketUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import okhttp3.WebSocket;

public class CameraWindow implements SurfaceHolder.Callback, Camera.PreviewCallback{
    private static final String TAG = CameraWindow.class.getSimpleName();

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

            View sufaceLayout = LayoutInflater.from(applicationContext).inflate(R.layout.float_layout, null);
            surfaceview=sufaceLayout.findViewById(R.id.floatSurface);
            surfaceHolder = surfaceview.getHolder();
            surfaceHolder.addCallback(this);

//            dummyCameraView = new SurfaceView(applicationContext);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();

            params.width = 100;

            params.height = 100;

            params.alpha = 100;

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

    private void init(){

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
            }
        } catch (Exception e){

        }

    }

    public byte[] getImageData(){
        return cameraData;
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

    @TargetApi(9)
    private Camera getBackCamera() {
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }


}
