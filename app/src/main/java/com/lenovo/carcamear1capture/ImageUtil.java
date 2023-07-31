package com.lenovo.carcamear1capture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class ImageUtil {

    //    yuvToNv21
    public static void ConvertNV21ToBitmap(byte[] nv21, ImageView imageView) {
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, 1920, 1080, null);
        // ByteArrayOutputStream的close中其实没做任何操作，可不执行
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

//         由于U和V一般都有缺损，因此若使用方式，可能会有个宽度为1像素的绿边
        yuvImage.compressToJpeg(new Rect(0, 0, 1920, 1080), 100, byteArrayOutputStream);

        byte[] jpgBytes = byteArrayOutputStream.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
//         原始预览数据生成的bitmap
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(jpgBytes, 0, jpgBytes.length, options);
        imageView.setImageBitmap(originalBitmap);
    }



}

