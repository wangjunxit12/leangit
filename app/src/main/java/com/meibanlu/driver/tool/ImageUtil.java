package com.meibanlu.driver.tool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片相关的工具类
 * Created by leigang on 2016/11/24.
 */

public class ImageUtil {

    public static void loadImage(Context context, String url, ImageView view) {
        //判断Activity
        Glide.with(context).load(url)
                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                .into(view);
    }

    public static void loadImage(Activity context, String url, ImageView view) {
        //判断Activity
        Glide.with(context).load(url)
                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                .into(view);
    }

    public static void loadImageWithAllSize(Activity context, String url, ImageView view) {
        //判断Activity
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                .into(view);
    }

    public static void loadImageWithAllSize(Activity context, int url, ImageView view) {
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                .into(view);
    }


    public static void loadImage(Context context, String url, ImageView view, int photoWith, int photoHigh) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(photoWith, photoHigh) // resizes the image to these dimensions (in pixel)
                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
                .into(view);
    }

    public static void compressPicture(String srcPath, String desPath, float maxSize) {
        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > maxSize) {
            be = w / maxSize;
        } else if (w < h && h > maxSize) {
            be = h / maxSize;
        }
        if (be <= 0) {
            be = 1.0f;
        }
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            File desFile = new File(desPath);
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            if (!desFile.exists()) {
                desFile.createNewFile();
            }
            fos = new FileOutputStream(desPath);

            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void getPhotoSize(final Activity context, final String url, ImageView imageView,final GetPhotoSize photoSize) {
        //获取图片显示在ImageView后的宽高
        Glide.with(context)
                .load(url)
                .asBitmap()//强制Glide返回一个Bitmap对象
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        double width = bitmap.getWidth();
                        double height = bitmap.getHeight();
                        double proportion = height / width;
                        photoSize.getSize(width, height);
                        photoSize.getProportion(proportion);
                        return false;
                    }
                }).into(imageView);
    }

    public interface GetPhotoSize {
        void getSize(double width, double height);

        void getProportion(double proportion);
    }
}
