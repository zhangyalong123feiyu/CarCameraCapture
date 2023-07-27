package com.lenovo.carcamear1capture;

import android.graphics.ImageFormat;
import android.media.Image;

public class ImageUtil {

    //    yuvToNv21
    public static byte[] yuvToNv21(byte[] y, byte[] u, byte[] v, byte[] nv21, int stride, int height) {
        System.arraycopy(y, 0, nv21, 0, y.length);
        // 注意，若length值为 y.length * 3 / 2 会有数组越界的风险，需使用真实数据长度计算
        int length = y.length + u.length / 2 + v.length / 2;
        int uIndex = 0, vIndex = 0;
        for (int i = stride * height; i < length; i += 2) {
            nv21[i] = v[vIndex];
            nv21[i + 1] = u[uIndex];
            vIndex += 2;
            uIndex += 2;
        }

        return nv21;
    }
//滴滴 小问题
//

    public static byte[] nv21_rotate_to_90(byte[] nv21_data, byte[] nv21_rotated, int width, int height) {
        int y_size = width * height;
        int buffser_size = y_size * 3 / 2;

        // Rotate the Y luma
        int i = 0;
        int startPos = (height - 1) * width;
        for (int x = 0; x < width; x++) {
            int offset = startPos;
            for (int y = height - 1; y >= 0; y--) {
                nv21_rotated[i] = nv21_data[offset + x];
                i++;
                offset -= width;
            }
        }
        // Rotate the U and V color components
        i = buffser_size - 1;
        for (int x = width - 1; x > 0; x = x - 2) {
            int offset = y_size;
            for (int y = 0; y < height / 2; y++) {
                nv21_rotated[i] = nv21_data[offset + x];
                i--;
                nv21_rotated[i] = nv21_data[offset + (x - 1)];
                i--;
                offset += width;
            }
        }
        return nv21_rotated;
    }

    //3/2    2   1
    public static byte[] nv21toNV12(byte[] nv21, byte[] nv12) {
        int size = nv21.length;
        nv12 = new byte[size];
        int len = size * 2 / 3;
        System.arraycopy(nv21, 0, nv12, 0, len);

        int i = len;
        while (i < size - 1) {
            nv12[i] = nv21[i + 1];
            nv12[i + 1] = nv21[i];
            i += 2;
        }
        return nv12;
    }

    public static byte[] rotation90(byte[] nv21Data, byte[] dstData, int widht, int height) {
        int index = 0;
        //u和v
        int ySize = widht * height;
        int uvHeight = height / 2;
        for (int i = 0; i < widht; i++) {
            for (int j = height - 1; j >= 0; j--) {
                dstData[index++] = nv21Data[widht * j + i];
            }
        }
        for (int i = 0; i < widht; i += 2) {
            for (int j = uvHeight - 1; j >= 0; j--) {
                // v
                dstData[index++] = nv21Data[ySize + widht * j + i];
                // u
                dstData[index++] = nv21Data[ySize + widht * j + i + 1];
            }
        }
        return dstData;
    }

    public static byte[] rotateYUV420SP(byte[] src, int width, int height) {
        byte[] dst = new byte[src.length];
        int wh = width * height;
        //旋转Y
        int k = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                dst[k] = src[width * j + i];
                k++;
            }
        }

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        for (int colIndex = 0; colIndex < halfWidth; colIndex++) {
            for (int rowIndex = halfHeight - 1; rowIndex >= 0; rowIndex--) {
                int index = (halfWidth * rowIndex + colIndex) * 2;
                dst[k] = src[wh + index];
                k++;
                dst[k] = src[wh + index + 1];
                k++;
            }
        }
        return dst;
    }


    public static byte[] rotateNV21Degree90(byte[] nv21Data, int width, int height) {

        final int frameSize = width * height;

        byte[] yuv = new byte[frameSize * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                yuv[i] = nv21Data[y * width + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = frameSize;
        for (int x = width - 1; x > 0; x = x - 2) {
            for (int y = 0; y < height / 2; y++) {
                yuv[i] = nv21Data[(width * height) + (y * width) + x];
                i++;
                yuv[i] = nv21Data[(width * height) + (y * width) + (x - 1)];
                i++;
            }
        }
        return yuv;
    }


    public static byte[] YUVGetNV21(Image image,byte[] nv21 ) {
        if (image == null) {
            return null;
        }
        try {
            int w = image.getWidth(), h = image.getHeight();
            // size是宽乘高的1.5倍 可以通过ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)得到
            int i420Size = w * h * 3 / 2;

            Image.Plane[] planes = image.getPlanes();
            //remaining0 = rowStride*(h-1)+w => 27632= 192*143+176 Y分量byte数组的size
            int remaining0 = planes[0].getBuffer().remaining();
            int remaining1 = planes[1].getBuffer().remaining();
            //remaining2 = rowStride*(h/2-1)+w-1 =>  13807=  192*71+176-1 V分量byte数组的size
            int remaining2 = planes[2].getBuffer().remaining();
            //获取pixelStride，可能跟width相等，可能不相等
            int pixelStride = planes[2].getPixelStride();
            int rowOffest = planes[2].getRowStride();
            nv21 = new byte[i420Size];
            //分别准备三个数组接收YUV分量。
            byte[] yRawSrcBytes = new byte[remaining0];
            byte[] uRawSrcBytes = new byte[remaining1];
            byte[] vRawSrcBytes = new byte[remaining2];
            planes[0].getBuffer().get(yRawSrcBytes);
            planes[1].getBuffer().get(uRawSrcBytes);
            planes[2].getBuffer().get(vRawSrcBytes);
            if (pixelStride == 1920) {
                //两者相等，说明每个YUV块紧密相连，可以直接拷贝
                System.arraycopy(yRawSrcBytes, 0, nv21, 0, rowOffest * h);
                System.arraycopy(vRawSrcBytes, 0, nv21, rowOffest * h, rowOffest * h / 2 - 1);
            } else {
                //根据每个分量的size先生成byte数组
                byte[] ySrcBytes = new byte[w * h];
                byte[] uSrcBytes = new byte[w * h / 2 - 1];
                byte[] vSrcBytes = new byte[w * h / 2 - 1];
                for (int row = 0; row < h; row++) {
                    //源数组每隔 rowOffest 个bytes 拷贝 w 个bytes到目标数组
                    System.arraycopy(yRawSrcBytes, rowOffest * row, ySrcBytes, w * row, w);
                    //y执行两次，uv执行一次
                    if (row % 2 == 0) {
                        //最后一行需要减一
                        if (row == h - 2) {
                            System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w - 1);
                        } else {
                            System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w);
                        }
                    }
                }
                //yuv拷贝到一个数组里面
                System.arraycopy(ySrcBytes, 0, nv21, 0, w * h);
                System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nv21;
    }


    public static byte[] getBitmapFromImage(Image image) {
        long time1 = System.currentTimeMillis();
        int w = image.getWidth(), h = image.getHeight();
        int i420Size = w * h * 3 / 2;
        int picel1 = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
        int picel2 = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888);

        Image.Plane[] planes = image.getPlanes();
        //remaining0 = rowStride*(h-1)+w => 27632= 192*143+176
        int remaining0 = planes[0].getBuffer().remaining();
        int remaining1 = planes[1].getBuffer().remaining();
        //remaining2 = rowStride*(h/2-1)+w-1 =>  13807=  192*71+176-1
        int remaining2 = planes[2].getBuffer().remaining();
        //获取pixelStride，可能跟width相等，可能不相等
        int pixelStride = planes[2].getPixelStride();
        int rowOffest = planes[2].getRowStride();
        byte[] nv21 = new byte[i420Size];
        byte[] yRawSrcBytes = new byte[remaining0];
        byte[] uRawSrcBytes = new byte[remaining1];
        byte[] vRawSrcBytes = new byte[remaining2];
        planes[0].getBuffer().get(yRawSrcBytes);
        planes[1].getBuffer().get(uRawSrcBytes);
        planes[2].getBuffer().get(vRawSrcBytes);
        if (pixelStride == w) {
            //两者相等，说明每个YUV块紧密相连，可以直接拷贝
            System.arraycopy(yRawSrcBytes, 0, nv21, 0, rowOffest * h);
            System.arraycopy(vRawSrcBytes, 0, nv21, rowOffest * h, rowOffest * h / 2 - 1);
        } else {
            byte[] ySrcBytes = new byte[w * h];
            byte[] uSrcBytes = new byte[w * h / 2 - 1];
            byte[] vSrcBytes = new byte[w * h / 2 - 1];
            for (int row = 0; row < h; row++) {
                //源数组每隔 rowOffest 个bytes 拷贝 w 个bytes到目标数组
                System.arraycopy(yRawSrcBytes, rowOffest * row, ySrcBytes, w * row, w);

                //y执行两次，uv执行一次
                if (row % 2 == 0) {
                    //最后一行需要减一
                    if (row == h - 2) {
                        System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w - 1);
                    } else {
                        System.arraycopy(vRawSrcBytes, rowOffest * row / 2, vSrcBytes, w * row / 2, w);
                    }
                }
            }
            System.arraycopy(ySrcBytes, 0, nv21, 0, w * h);
            System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1);
        }


        return nv21;
    }

}

