package com.microsoft.projectoxforddemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yulw on 7/4/2015.
 */
public class ImageUtils
{
    public static void drawHomeImage(Canvas canvas,Drawable drawable,Rect bound)
    {
        if(canvas==null) {
            return;
        }
        drawable.setBounds(bound);
        drawable.draw(canvas);
    }
    public static void drawHomeImage(Canvas canvas,Drawable drawable,int margin_left,int margin_top)
    {
        if(canvas==null) {
            return;
        }
        int camera_preview_right=canvas.getWidth()-margin_left;
        int camera_preview_bottom=canvas.getHeight()-margin_top;
        drawable.setBounds(margin_left,margin_top,canvas.getWidth()-margin_left,canvas.getHeight()-margin_top);
        drawable.draw(canvas);
    }
    public static  void drawContour(Canvas canvas)
    {
        int canvasWidth=canvas.getWidth();
        int canvasHeight=canvas.getHeight();
        int rectWidth=canvasWidth/2;
        int rectHeight=canvasHeight/2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        canvas.drawRect(canvasWidth / 2 - rectWidth / 2, canvasHeight / 2 - rectHeight / 2, rectWidth, rectHeight, paint);
    }
    public static void drawClear(Canvas canvas)  {
        canvas.drawARGB(0, 1, 1, 1);
    }
    public static boolean checkImage(String name) {

        return true;
    }
    public static void saveImage(String name) {

    }
    public static Bitmap loadImage(String name) {

        return null;
    }
    //get bmp from the buffer captured by invoking Camera.TakePicture(...PictureCallback).
    public static Bitmap getBitmap(byte[] data) {
        Bitmap bmp= BitmapFactory.decodeByteArray(data, 0, data.length);
        save(bmp,"before_rotate");
        Bitmap bmp2=ImageUtils.rotateBitmap(bmp,-90);
        save(bmp2,"after_rotate");
        return bmp2;
    }
    public static ByteArrayInputStream getByteArrayInputStream(byte[] data){
        //Camera.Parameters ps = camera.getParameters();
        Bitmap bmp=getBitmap(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream input=new ByteArrayInputStream(outputStream.toByteArray());
        return input;
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        // If the rotate angle is 0, then return the original image, else return the rotated image
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }
    public static  void save(Bitmap bmp,String name){
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ProjectOxford";
        File dir=new File(path);
        if(!dir.exists())
            dir.mkdir();
        File image=new File(dir,name+".bmp");
        try {
            FileOutputStream fout=new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,fout);
            fout.flush();
            fout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void copyInputStreamToFile( InputStream in, String name) {
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ProjectOxford";
        File dir=new File(path);
        if(!dir.exists())
            dir.mkdir();
        File image=new File(dir,name+".bmp");
            try {
                OutputStream out = new FileOutputStream(image);
                byte[] buf = new byte[1024];
                int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
