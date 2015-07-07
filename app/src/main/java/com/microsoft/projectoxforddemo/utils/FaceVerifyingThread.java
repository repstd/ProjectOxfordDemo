package com.microsoft.projectoxforddemo.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.VerifyResult;

import java.util.UUID;

/**
 * Created by v-yuliwa on 7/6/2015.
 */
public class FaceVerifyingThread extends Thread {
    private final String TAG = "FceVerifyingThread";
    private Handler m_handler;
    private FaceServiceClient m_client = null;
    private UUID m_faceOld, m_faceNew;

    public FaceVerifyingThread(UUID faceOld, UUID faceNew, Handler handler) {
        m_handler = handler;
        m_faceNew = faceNew;
        m_faceOld = faceOld;
        m_client = new FaceServiceClient(OxfordRecognitionManager.instance().getFaceKey().getPrimary());
        if (m_client == null) {
            Log.d(TAG, "Error in connecting to the server and create FaceRecognition client.");
        } else
            Log.d(TAG, "faceServiceClient connected.");
    }

    @Override
    public void run() {
        try {
            VerifyResult result = m_client.verify(m_faceNew, m_faceOld);
            Log.d(TAG, "Verified");
            sendVerifyResult(result);
        } catch (Exception e) {

        }
    }

    void sendVerifyResult(VerifyResult result) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isIdentical", result.isIdentical);
        bundle.putDouble("confidence", result.confidence);
        Message msg = new Message();
        msg.setData(bundle);
        m_handler.sendMessage(msg);
    }
}
