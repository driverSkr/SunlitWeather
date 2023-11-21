package com.driverskr.sunlitweather.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by shiju.wang on 2017/10/17.
 */

public class BitmapUtil {

    /**
     * 以省内存的方式读取图片
     *
     * @param is
     * @return
     */
    public Bitmap getBitmap(InputStream is) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = 4;
        //获取资源图片
        //InputStream is = mContext.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat ||
                drawable instanceof VectorDrawable) {
            return getBitmapFromVectorDrawable(drawable);
        } else {
            return null;
        }
    }

    /**
     * drawable.getIntrinsicWidth() 获取宽度的单位是dp
     *
     * @param drawable
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(null, bitmap);
    }

    /**
     * bitmap添加水印
     *
     * @param src
     * @param watermark
     * @return
     */
    private Bitmap addWatermark(Bitmap src, Bitmap watermark) {
        if (src == null || watermark == null) {
            return src;
        }

        int sWid = src.getWidth();
        int sHei = src.getHeight();
        int wWid = watermark.getWidth();
        int wHei = watermark.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        if (sWid < wWid || sHei < wHei) {
            return src;
        }

        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Bitmap.Config.ARGB_8888);
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawBitmap(watermark, sWid - wWid - 5, sHei - wHei - 5, null);
//            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.save();
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    /**
     * Bitmap保存为图片文件
     *
     * @param bitmap
     * @param fileName
     * @return
     */
    public static boolean saveBitmap(Context context, Bitmap bitmap, String fileName) {
        String imagePath = context.getFilesDir().getAbsolutePath() + "/temp.png";
        File file = new File(imagePath);
        if (!file.exists()) {
            file.mkdir();
        }
        File imageFile = new File(file, fileName);
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 根据分辨率压缩图片比例
     *
     * @param w
     * @param h
     * @return
     */
    public static Bitmap compressByResolution(Context context, int resourceId, int w, int h) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(imgPath, opts);
        BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        float width = options.outWidth;
        float height = options.outHeight;
        int widthScale = (int) (width / w);
        int heightScale = (int) (height / h);

        int scale = Math.min(widthScale, heightScale);//保留压缩比例小的

        if (scale < 1) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        return bitmap;
    }

    /**
     * 将图片压缩到指定大小
     *
     * @param w
     * @param h
     * @return
     */
    public static Bitmap compressBySize(Context context, int resourceId, float w, float h) {
        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(resourceId);
        Matrix matrix = new Matrix();
        Bitmap src = bd.getBitmap();
        matrix.postScale(w / src.getWidth(), h / src.getHeight());
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

}