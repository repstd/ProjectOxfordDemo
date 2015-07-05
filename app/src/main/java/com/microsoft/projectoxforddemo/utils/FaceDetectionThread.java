package com.microsoft.projectoxforddemo.utils;

import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yulw on 7/5/2015.
 */
public class FaceDetectionThread extends Thread implements Subject {
    private final String TAG = "DetectionTask";
    List<Observer> m_observers;
    private FaceServiceClient m_client = null;
    private Face[] m_result;
    private InputStream m_para;
    FaceDetectionThread(InputStream para)
    {
        m_observers = new ArrayList<Observer>();
        m_client = new FaceServiceClient(OxfordRecognitionManager.instance().getFaceKey().getPrimary());
        if (m_client == null) {
            Log.d(TAG, "Error in connecting to the server and create FaceRecognition client.");
        } else
            Log.d(TAG, "faceServiceClient connected.");
        m_para=para;
    }

    FaceDetectionThread(InputStream para, FaceServiceClient client) {
        m_para=para;
        m_client = client;
    }

    public FaceServiceClient getFaceServiceClient() {
        return m_client;
    }

    public Face[] getResult() {
        return m_result;
    }

    public void run()  {
        doInBackground(m_para);
    }
    protected Face[] doInBackground(InputStream params)
    {
        try {
            m_result = m_client.detect(params, true, true, true, true);
            Log.d(TAG, "FaceServiceReturned");

            alert();
            for (Observer ob : m_observers)
                detach(ob);

            if (m_result.length >= 0) {
                Log.d(TAG, "detect succeed.");
                return m_result;
            } else
                return m_result;
        } catch (ClientException e) {
            Log.d(TAG, "rest.ClientException.");
            return null;
        } catch (IOException e) {
            Log.d(TAG, "IOException");
            return null;
        }
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
