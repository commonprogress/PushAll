package com.dongxl.huawei.demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.dongxl.huawei.hms.agent.common.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 社交工具类 | SNS tools
 */
public final class SnsUtil {

    /**
     * 最大图片数组 | Maximum picture array
     */
    private static final int MAX_BYTE_SIZE = 30*1024;

    // 运行bmp解码图片在内存中的大小，4*300*300。 根据实际需要可修改：太大容易OOM，太小容易图标失真 | Run BMP decoded picture in memory size, 4*300*300. According to the actual needs can be modified: too large easy oom, too small easy to distort the icon
    private static final long MAX_BMP_MEMERY_SIZE = 4*300*300;

    private SnsUtil(){}

    public static byte[] getBytesFromRes(int resId, Context context) {
        try {
            return getSuitableByteArrAndClose(crtResStream(resId, context)
                    , getInSampleSizeAndClose(crtResStream(resId, context)));
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static byte[] getBytesFromFile(String filePath) {
        try {
            return getSuitableByteArrAndClose(crtFileStream(filePath)
                    , getInSampleSizeAndClose(crtFileStream(filePath)));
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * 根据asset图片文件创建图片字节数组 | Create a picture byte array from a asset picture file
     * @param fileName asset中图片名称 | Picture name in asset
     * @param context 上下文 | context
     * @return 图片字节数组 | Picture byte array
     */
    public static byte[] getBytesFromAssetsFile(String fileName, Context context) {
        try {
            return getSuitableByteArrAndClose(crtAssetStream(fileName, context)
                    , getInSampleSizeAndClose(crtAssetStream(fileName, context)));
        } catch (Exception e) {
            return new byte[0];
        }
    }

    /**
     * 根据资源中图片创建图片流 | Create a picture stream from a picture in a resource
     * @param resId 资源id，必须为图片的资源id | Resource ID, must be the resource ID of the picture
     * @param context 上下文 | context
     * @return 图片流 | Picture Stream
     */
    private static InputStream crtResStream(int resId, Context context) {
        if (context == null) {
            return null;
        }

        return context.getResources().openRawResource(resId);
    }

    /**
     * 根据图片路径创建图片流 | Create a picture stream from a picture path
     * @param filePath  图片路径 | picture path
     * @return 图片流 | Picture Stream
     * @throws FileNotFoundException 文件读取异常 | File Read exception
     */
    private static InputStream crtFileStream(String filePath) throws FileNotFoundException {
        if (filePath == null) {
            return null;
        }

        return new FileInputStream(filePath);
    }

    /**
     * 根据Asset中的图片创建图片流 | Create a picture stream from a picture in asset
     * @param assetFileName asset 中图片文件名称 | Picture file name in asset
     * @param context 上下文 | context
     * @return 图片流 | Picture Stream
     */
    private static InputStream crtAssetStream(String assetFileName, Context context){
        if (context == null || assetFileName == null) {
            return null;
        }

        try {
            AssetManager am = context.getResources().getAssets();
            return am.open(assetFileName);
        }catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片字节数组 | Get a picture byte array
     * @param is 图片流 | Picture Stream
     * @param inSampleSize 缩放大小 | Scaling factor
     * @return 字节数组 | byte array
     */
    private static byte[] getSuitableByteArrAndClose(InputStream is, int inSampleSize){

        if (is == null) {
            return new byte[0];
        }

        try {
            Rect rect = new Rect();
            Bitmap bmp = createSuitableBmp(is, rect, inSampleSize);
            is.close();
            return bmpToByteArrayAndRecycle(bmp);
        } catch (IOException e) {
            IOUtils.close(is);
            return new byte[0];
        }
    }

    /**
     * 根据限制的内存大小，计算需要的缩放参数，防止oom | According to the limited memory size, calculate the required scaling parameters to prevent oom
     * @param is 图片流 | Picture Stream
     * @return 需要的缩放参数 | Required Scaling Parameters
     */
    private static int getInSampleSizeAndClose(InputStream is) {
        if (is == null) {
            return 0;
        }

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(is, null, options);
            is.close();

            if (options.outWidth == 0 || options.outHeight == 0) {
                return 0;
            }

            int ratio = 1;
            long curBmpSize = 4L * options.outWidth * options.outHeight;
            while (MAX_BMP_MEMERY_SIZE < curBmpSize/(ratio*ratio)) {
                ratio*=2;
            }
            return ratio;
        } catch (IOException e) {
            IOUtils.close(is);
            return 0;
        }
    }

    /**
     * 创建合适大小的位图 | Create a bitmap of the appropriate size
     * @param is 图片流 | Picture Stream
     * @param rect 区域 | Regional
     * @param inSampleSize 缩放参数 | Scaling factor
     * @return 创建的位图 | Bitmap created
     * @throws IOException 解码异常 | Decoding exceptions
     */
    private static Bitmap createSuitableBmp(InputStream is, Rect rect, int inSampleSize) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(is, rect, options);
    }

    /**
     * 发送图文消息需要icon，icon要求不大于30K，并且以二进制流的方式传递 | Sending a text message requires a ICON,ICON requirement of not more than 30K, and is passed in binary stream mode.
     * 因此该方法首先将bitmap对象压缩到30K以内，并转化为byte数组 | So the method first compresses the bitmap object within 30K and converts it to a byte array.
     * @param image 要转换的位图 | Bitmap to convert
     * @return 转换后的byte数组 | Converted byte array
     */
    public static byte[] bmpToByteArrayAndRecycle(Bitmap image) throws IOException {
        if (image == null)
        {
            return new byte[0];
        }

        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        image.compress(format, 100, out);
        long tmpLen = out.toByteArray().length;
        if (tmpLen <= MAX_BYTE_SIZE) {
            byte[] rst = out.toByteArray();
            out.reset();
            image.recycle();
            return rst;
        }

        float zoom = (float) Math.sqrt(MAX_BYTE_SIZE/(float)tmpLen);
        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);
        Bitmap tmp = image;
        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        if (tmp != result) {
            tmp.recycle();
        }

        out.reset();
        result.compress(format, 100, out);

        while (out.toByteArray().length > MAX_BYTE_SIZE)
        {
            matrix.setScale(0.9f, 0.9f);
            tmp = result;
            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
            if (tmp != result) {
                tmp.recycle();
            }
            out.reset();
            result.compress(format, 100, out);
        }

        result.recycle();
        byte[] rst = out.toByteArray();
        out.reset();
        return rst;
    }
}
