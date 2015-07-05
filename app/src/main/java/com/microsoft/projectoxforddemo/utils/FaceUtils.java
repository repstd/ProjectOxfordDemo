package com.microsoft.projectoxforddemo.utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yulw on 7/4/2015.
 */
interface Observer {
    public abstract void update(Subject sub);
}

interface Subject {
    public abstract void alert();

    public abstract void attach(Observer ob);

    public abstract void detach(Observer ob);
}

public class FaceUtils {
    private final static String TAG = "FaceUtils";
    public static FaceResultObserver FACES = new FaceResultObserver(null, null);

    public static Face[] detectFace(ByteArrayInputStream image, Handler handler) {
        DetectionTask task = new DetectionTask();
        FACES = new FaceResultObserver(task, handler);
        task.attach(FACES);
        task.execute(image);
        Log.d(TAG, "AsyncTaskExecuted.");
        return task.getResult();
    }

    public static Face[] detectFace(ByteArrayInputStream image, FaceServiceClient cli, Handler handler) {
        DetectionTask task = new DetectionTask(cli);
        FACES = new FaceResultObserver(task, handler);
        task.attach(FACES);
        task.execute(image);
        return task.getResult();
    }

    public static Face[] detectFace(byte[] data, Handler handler) {
        Log.d(TAG, "detectFace");
        ByteArrayInputStream inputStream = ImageUtils.getByteArrayInputStream(data);
        return detectFace(inputStream, handler);
    }

    /**
     * Created by yulw on 7/5/2015.
     */
    static class DetectionTask extends AsyncTask<InputStream, String, Face[]> implements Subject {
        private final String TAG = "DetectionTask";
        List<Observer> m_observers;
        private FaceServiceClient m_client = null;
        private Face[] m_result;

        DetectionTask() {
            m_observers = new ArrayList<Observer>();
            publishProgress("connecting to AzureIntelligence server...");
            m_client = new FaceServiceClient(OxfordRecognitionManager.instance().getFaceKey().getPrimary());
            if (m_client == null) {
                Log.d(TAG, "Error in connecting to the server and create FaceRecognition client.");
            } else
                Log.d(TAG, "faceServiceClient connected.");
        }

        DetectionTask(FaceServiceClient client) {
            m_client = client;
        }

        public FaceServiceClient getFaceServiceClient() {
            return m_client;
        }

        public Face[] getResult() {
            return m_result;
        }

        @Override
        protected Face[] doInBackground(InputStream... params) {
            publishProgress("detecting...");
            try {
                m_result = m_client.detect(params[0], true, true, true, true);
                Log.d(TAG, "FaceServiceReturned");

                alert();
                for (Observer ob : m_observers)
                    detach(ob);

                if (m_result.length >= 0) {
                    Log.d(TAG, "detect succeed.");
                    publishProgress("detect succeed.");
                    return m_result;
                } else
                    return m_result;
            } catch (ClientException e) {
                Log.d(TAG, "rest.ClientException.");
                publishProgress("rest.ClientException.");
                return null;
            } catch (IOException e) {
                Log.d(TAG, "IOException");
                publishProgress("IOException");
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            super.onPostExecute(faces);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Face[] faces) {
            super.onCancelled(faces);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        @Override
        public void alert() {
            for (Observer ob : m_observers)
                ob.update(this);
            Log.d(TAG, "AsyncTask Finished.");
        }

        @Override
        public void attach(Observer ob) {
            m_observers.add(ob);
        }

        @Override
        public void detach(Observer ob) {
            m_observers.remove(ob);
        }

    }

    /**
     * Created by yulw on 7/5/2015.
     */
    public static class FaceResultObserver implements Observer {
        Face[] m_result = null;
        DetectionTask m_subject;
        private Handler m_handler;

        FaceResultObserver(DetectionTask task, Handler handler) {
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

