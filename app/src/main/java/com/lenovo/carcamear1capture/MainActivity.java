package com.lenovo.carcamear1capture;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.lenovo.carcamear1capture.mutimodwebsocket.PassengerInfo;
import com.lenovo.carcamear1capture.mutimodwebsocket.WsListener;
import com.lenovo.carcontroler.utils.WebSocketUtil;

import java.util.concurrent.ArrayBlockingQueue;

import okhttp3.WebSocket;

public class MainActivity extends Activity implements WsListener.ReceiveDataListener {

    private SurfaceView surfaceview;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

    private Parameters parameters;
    private CameraDataBean cameraDataInfo;

    private WebSocket websocketClient;
    private Gson gson;
    private static int yuvqueuesize = 10;

    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    private AvcEncoder avcCodec;
    private final static int CAMERA_OK = 10001;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private ImageView imagev;
    private CameraWindow cameraWindow;
    private CameraWindowTwo cameraWindowTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceview = findViewById(R.id.surfaceview);
        requestAlertWindowPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cameraWindow = new CameraWindow();
            cameraWindow.show(this);
//            cameraWindow Two = new CameraWindowTwo();
//            cameraWindowTwo.show(this);
        }
        connectServer();
        SupportAvcCodec();
        moveTaskToBack(true);
    }

    private void connectServer() {
        WebSocketUtil webSocketUtil = new WebSocketUtil();
        webSocketUtil.init(this);
        websocketClient = webSocketUtil.getWebSocketClient();
        webSocketUtil.getWebSocketListener().setReceiveDataListener(this);
        gson = new Gson();
        cameraDataInfo = new CameraDataBean();
    }
    private void requestAlertWindowPermission() {
        try {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1001);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                } else {
                    showWaringDialog();
                }
                break;
            default:
                break;
        }
    }

    private void showWaringDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                        finish();
                    }
                }).show();
    }


    @SuppressLint("NewApi")
    private boolean SupportAvcCodec() {
        if (Build.VERSION.SDK_INT >= 18) {
            for (int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);

                String[] types = codecInfo.getSupportedTypes();
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void onReceiveData(String msg) {
        PassengerInfo passengerInfo = gson.fromJson(msg, PassengerInfo.class);
        Integer status = passengerInfo.getStatus();
        if (status==6){
            if (cameraWindow.getImageData()!=null/*&& cameraWindowTwo.getImageData()!=null*/){
                cameraDataInfo.setIn(Base64.encodeToString(cameraWindow.getImageData(), Base64.DEFAULT));
//            cameraDataInfo.setOut(Base64.encodeToString(cameraWindowTwo.getImageData(), Base64.DEFAULT));
                String cameraJson = gson.toJson(cameraDataInfo);
                //编码base64 发送数据
                Log.e("TAG", "send camera data time iscom"+TimeUtils.getTimeFromTimestamp());
                websocketClient.send(cameraJson);
            }
        }
            if (status==4||status==5||status==6) return;
            Log.e("TAG","send msg to tts");
            Intent intent = new Intent("com.lenovo.vehi_assistant.BroadcastTextReceiver");
            intent.setComponent(new ComponentName("com.lenovo.vehi_assistant", "com.lenovo.vehi_assistant.BroadcastTextReceiver"));
            intent.setAction("android.intent.action.VQA_TTS");
            intent.putExtra("speakText",msg);
            sendBroadcast(intent);

    }
}
