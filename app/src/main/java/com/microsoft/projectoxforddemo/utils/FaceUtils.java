package com.microsoft.projectoxforddemo.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yulw on 7/4/2015.
 */
public class FaceUtils
{
    private final static String TAG="FaceUtils";
    public static  Face[] detectFace(ByteArrayInputStream image) {
        DetectionTask task=new DetectionTask();
        task.execute(image);
        Log.d(TAG,"AsyncTaskExecuted.");
        return task.getResult();
    }
    public static Face[] detectFace(ByteArrayInputStream image,FaceServiceClient cli) {
        DetectionTask task=new DetectionTask(cli);
        task.execute(image);
        return task.getResult();
    }
    public static Face[] detectFace(byte[] data) {
        Log.d(TAG,"detectFace");
        ByteArrayInputStream inputStream=ImageUtils.getByteArrayInputStream(data);
        return detectFace(inputStream);
    }
}
class DetectionTask extends AsyncTask<InputStream, String, Face[]>
{
    private final String TAG="DetectionTask";
    private FaceServiceClient m_client=null;
    private Face[] m_result;
    private boolean m_isEnded;
    DetectionTask()
    {
        publishProgress("connecting to azureintelligence server...");
        m_client=new FaceServiceClient(OxfordRecognitionManager.instance().getFaceKey().getPrimary());
        if(m_client==null) {
            Log.d(TAG, "Error in connecting to the server and create FaceRecognition client.");
        }
        else
            Log.d(TAG,"faceServiceClient connected.");
    }
    DetectionTask(FaceServiceClient client) {
        m_client=client;
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
            m_result=m_client.detect(params[0],true,true,true,true);
            Log.d(TAG,"FaceServiceReturned");
            if(m_result.length>=0) {
                Log.d(TAG,"detect succeed.");
                publishProgress("detect succeed.");
                return m_result;
            }
            else
                return m_result;
        }
        catch (ClientException e) {
            Log.d(TAG,"rest.ClientException.");
            publishProgress("rest.ClientException.");
            return null;
        }
        catch(IOException e) {
            Log.d(TAG,"IOException");
            publishProgress("IOException");
            return null;
        }
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        m_isEnded=false;
    }

    @Override
    protected void onPostExecute(Face[] faces) {
        super.onPostExecute(faces);
        m_isEnded=true;
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
    synchronized boolean isEnded() {
        return m_isEnded;
    }
}
