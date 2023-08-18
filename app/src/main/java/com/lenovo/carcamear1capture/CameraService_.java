package com.lenovo.carcamear1capture;

import static com.lenovo.carcamear1capture.MainActivity.YUVQueue;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class CameraService_ extends Service implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Parameters parameters;
    private AvcEncoder avcCodec;

    int width = 1920;

    int height = 1080;

    int framerate = 15;

    int biterate = 25000;

    private static int yuvqueuesize = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","camera service onCreate");
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG","onStartCommand");
        init();
        initNotification();
        return super.onStartCommand(intent, flags, startId);
    }
    private void init(){
        SurfaceView surfaceView=new SurfaceView(getApplicationContext());
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        putYUVData(data);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.e("TAG","surfaceCreated  is ------------");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        camera = getBackCamera();
        startcamera(camera);
        Log.e("TAG","preview width is ------------"+width+"height-----"+height);
        avcCodec = new AvcEncoder(this.width,this.height,framerate,biterate);
        avcCodec.StartEncoderThread();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            avcCodec.StopThread();
        }
    }

    public void putYUVData(byte[] buffer) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
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

    private void initNotification() {
        String channelID = "1";
        String channelName = "channel_name";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("message")
                    .setContentTitle("title")
                    .setChannelId(channelID)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }
    }
}
