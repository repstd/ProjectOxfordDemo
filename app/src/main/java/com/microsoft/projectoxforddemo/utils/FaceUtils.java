package com.microsoft.projectoxforddemo.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;

public class FaceUtils {
    private final static String TAG = "FaceUtils";
    public static FaceDetectionObserver FACES = new FaceDetectionObserver(null, null);

    public static Face[] detectFace(ByteArrayInputStream image, Handler handler) {
        FaceDetectionThread task = new FaceDetectionThread(image);
        FACES = new FaceDetectionObserver(task, handler);
        task.attach(FACES);
        task.start();
        Log.d(TAG, "AsyncThreadExecuted.");
        return task.getResult();
    }
    public static Face[] detectFaceInThreading(ByteArrayInputStream image, Handler handler) {
        FaceDetectionThread task= new FaceDetectionThread(image);
        FACES = new FaceDetectionObserver(task, handler);
        task.attach(FACES);
        task.start();
        Log.d(TAG, "AsyncTaskExecuted.");
        return task.getResult();
    }
    public static Face[] detectFace(ByteArrayInputStream image, FaceServiceClient cli, Handler handler) {
        FaceDetectionThread task = new FaceDetectionThread(image,cli);
        FACES = new FaceDetectionObserver(task, handler);
        task.attach(FACES);
        task.start();
        return task.getResult();
    }
    public static Face[] detectFace(byte[] data, Handler handler) {
        Log.d(TAG, "detectFace");
        ByteArrayInputStream inputStream = ImageUtils.getByteArrayInputStream(data);
        return detectFaceInThreading(inputStream, handler);
    }

    /**
     * Created by yulw on 7/5/2015.
     */
    public static class FaceDetectionObserver implements Observer {
        Face[] m_result = null;
        FaceDetectionThread m_subject;
        private Handler m_handler;

        FaceDetectionObserver(FaceDetectionThread task, Handler handler) {
            m_subject = task;
            m_handler = handler;
        }
        @Override
        public void update(Subject sub) {
            if (sub == m_subject) {
                m_result = m_subject.getResult();

                if (m_handler == null)
                    return;
                //tell the calling thread
                Bundle bundle = new Bundle();
                if (m_result == null || m_result.length == 0)
                    bundle.putInt("faces", 0);
                else
                    bundle.putInt("faces", 1);
                Message msg = new Message();
                msg.setData(bundle);
                m_handler.sendMessage(msg);
            }

        }

        public Face[] getResult() {
            return m_result;
        }
    }
}
