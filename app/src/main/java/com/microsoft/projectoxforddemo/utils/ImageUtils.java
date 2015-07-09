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
import android.util.Log;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yulw on 7/4/2015.
 */

public class ImageUtils {
    public static final int CAMERA_BACK = 0;
    public static final int CAMERA_FRONT = 1;
    private static final int IMAGE_MAX_SIDE_LENGTH = 1280;
    //by default,we use the front camera;
    public static Config m_config = new Config();
    private static Bitmap m_lastCapture = null;

    public static Config getConfig() {
        return m_config;
    }

    public static void drawHomeImage(Canvas canvas, Drawable drawable, Rect bound) {
        if (canvas == null) {
            return;
        }
        drawable.setBounds(bound);
        drawable.draw(canvas);
    }

    public static void drawHomeImage(Canvas canvas, Drawable drawable, int margin_left, int margin_top) {
        if (canvas == null) {
            return;
        }
        int camera_preview_right = canvas.getWidth() - margin_left;
        int camera_preview_bottom = canvas.getHeight() - margin_top;
        drawable.setBounds(margin_left, margin_top, camera_preview_right, camera_preview_bottom);
        drawable.draw(canvas);
    }
    public static void drawHomeImage(Canvas canvas,byte[] data,int margin_left,int margin_top) {
        Bitmap bmp=getBitmap(data);
        int camera_preview_right = canvas.getWidth() - margin_left;
        int camera_preview_bottom = canvas.getHeight() - margin_top;
        canvas.drawBitmap(bmp,null,new Rect(margin_left, margin_top, camera_preview_right, camera_preview_bottom),null);
    }
    public static void drawContour(Canvas canvas) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int rectWidth = canvasWidth / 2;
        int rectHeight = canvasHeight / 2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        canvas.drawRect(canvasWidth / 2 - rectWidth / 2, canvasHeight / 2 - rectHeight / 2, rectWidth, rectHeight, paint);
    }

    public static void drawClear(Canvas canvas) {
        canvas.drawARGB(0, 1, 1, 1);
    }

    public static void drawFace(Canvas canvas, Face[] faces, int marginLeft, int marginTop) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        //draw the face rectangle
        for (Face face : faces) {
            canvas.drawRect(marginLeft + face.faceRectangle.left, marginTop + face.faceRectangle.top, face.faceRectangle.width, face.faceRectangle.height, paint);
        }
    }

    public static void drawFaces(Canvas canvas, Face[] faces, int marginLeft, int marginTop) {
        Bitmap original = m_lastCapture;
        if (original == null)
            return;
        Bitmap marked = drawFaces(original, faces, true, canvas);
        canvas.drawBitmap(marked, new Rect(0, 0, marked.getWidth(), marked.getHeight()), new Rect(marginLeft, marginTop, canvas.getWidth() - marginLeft, canvas.getHeight() - marginTop), null);
        //canvas.drawBitmap(marked, new Rect(0, 0, marked.getWidth(), marked.getHeight()), new Rect(canvas.getWidth()-marginLeft, marginTop, marginLeft, canvas.getHeight() - marginTop), null);
        //canvas.drawBitmap(marked,marginLeft,marginTop,null);
    }

    private static Bitmap drawFaces(Bitmap originalBitmap, Face[] faces, boolean drawLandmarks, Canvas viewCanvas) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        int stokeWidth = Math.max(originalBitmap.getWidth(), originalBitmap.getHeight()) / 100;
        if (stokeWidth == 0) {
            stokeWidth = 1;
        }
        paint.setStrokeWidth(stokeWidth);

        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                //calculateFaceRectangle(bitmap, face.faceRectangle, FACE_RECT_SCALE_RATIO);
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
                if (drawLandmarks) {
                    int radius = face.faceRectangle.width / 30;
                    if (radius == 0) {
                        radius = 1;
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(radius);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.pupilLeft.x,
                            (float) face.faceLandmarks.pupilLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.pupilRight.x,
                            (float) face.faceLandmarks.pupilRight.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.noseTip.x,
                            (float) face.faceLandmarks.noseTip.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.mouthLeft.x,
                            (float) face.faceLandmarks.mouthLeft.y,
                            radius,
                            paint);

                    canvas.drawCircle(
                            (float) face.faceLandmarks.mouthRight.x,
                            (float) face.faceLandmarks.mouthRight.y,
                            radius,
                            paint);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(stokeWidth);
                }
            }
        }
        return bitmap;
    }

    public static String checkImage(String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + getConfig().getDataDiraName();
        File f = new File(path, name + ".bmp");
        if (!f.exists())
            return null;
        else
            return f.getAbsolutePath();
    }

    public static Bitmap loadImage(String name) {
        String result = checkImage(name);
        if (result == null)
            return null;
        Bitmap bitmap = BitmapFactory.decodeFile(result);
        return bitmap;
    }

    //get bmp from the buffer captured by invoking Camera.TakePicture(...PictureCallback).
    public static Bitmap getBitmap(byte[] data) {
        Bitmap original = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (getConfig().getCamera() == CAMERA_BACK)
            m_lastCapture = resize(ImageUtils.rotateBitmap(original, 90));
        else
            m_lastCapture = resize(ImageUtils.rotateBitmap(original, -90));
        save(m_lastCapture,"capture");
        return m_lastCapture;
    }

    public static ByteArrayInputStream getByteArrayInputStream(byte[] data) {
        //Camera.Parameters ps = camera.getParameters();
        Bitmap bmp = getBitmap(data);
        return getByteArrayInputStream(bmp);
    }

    public static ByteArrayInputStream getByteArrayInputStream(Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream input = new ByteArrayInputStream(outputStream.toByteArray());
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

    public static void saveLastCapture(String name) {
        save(m_lastCapture, name);
    }

    public static void save(Bitmap bmp, String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + getConfig().getDataDiraName();
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdir();
        File image = new File(dir, name + ".bmp");
        try {
            FileOutputStream fout = new FileOutputStream(image);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyInputStreamToFile(InputStream in, String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + getConfig().getDataDiraName();
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdir();
        File image = new File(dir, name + ".bmp");
        try {
            OutputStream out = new FileOutputStream(image);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap resize(Bitmap src) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int maxSideLength = Math.max(srcWidth, srcHeight);
        Log.d("ImageUtil", "input image dimension: " + Integer.toString(srcWidth) + Integer.toString(srcHeight));
        if (maxSideLength <= IMAGE_MAX_SIDE_LENGTH)
            return src;
        double resizeRatio = IMAGE_MAX_SIDE_LENGTH / (double) maxSideLength;
        Log.d("ImageUtil", "resize ratio: " + Double.toString(resizeRatio));
        Bitmap result = Bitmap.createScaledBitmap(src, (int) (srcWidth * resizeRatio), (int) (srcHeight * resizeRatio), false);
        return result;
    }

    public static void setCamera(int cam) {
        getConfig().setCamera(cam);
    }

    static class Config {
        int m_cameraIndex;
        String m_dataDir;
        String m_tempName;

        Config() {
            m_dataDir = "/ProjectOxford";
            m_tempName = "capture";
            m_cameraIndex = ImageUtils.CAMERA_FRONT;
        }

        public int getCamera() {
            return m_cameraIndex;
        }

        public void setCamera(int camIndex) {
            m_cameraIndex = camIndex;
        }

        public String getDataDiraName() {
            return m_dataDir;
        }

        public void setDataDirName(String name) {
            m_dataDir = name;
        }

        public String getTempBmpName() {
            return m_tempName;
        }
    }
}
