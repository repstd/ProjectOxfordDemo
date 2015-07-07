package com.microsoft.projectoxforddemo.utils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.microsoft.ProjectOxford.ISpeechRecognitionServerEvents;
import com.microsoft.ProjectOxford.MicrophoneRecognitionClient;
import com.microsoft.ProjectOxford.RecognitionResult;
import com.microsoft.ProjectOxford.RecognitionStatus;
import com.microsoft.ProjectOxford.SpeechRecognitionMode;
import com.microsoft.ProjectOxford.SpeechRecognitionServiceFactory;

/**
 * Created by v-yuliwa on 7/7/2015.
 */
public class SpeechRecognitionThread extends Thread implements ISpeechRecognitionServerEvents {
    private final String TAG = "SpeechRecognitionThread";
    private MicrophoneRecognitionClient m_micClient = null;
    private SpeechRecognitionMode m_recoMode = null;
    private ISpeechRecognitionServerEvents m_eventCallback;
    private Handler m_handler = null;
    private Activity m_activity;
    private int m_waitSeconds;
    public SpeechRecognitionThread(Activity activity, ISpeechRecognitionServerEvents eventCallback, Handler handler)
    {
        this.m_activity = activity;
        this.m_handler = handler;
        this.m_eventCallback=eventCallback;
        m_recoMode = SpeechRecognitionMode.ShortPhrase;
        m_waitSeconds = m_recoMode == SpeechRecognitionMode.ShortPhrase ? 20 : 200;
    }

    @Override
    public void run() {
        try
        {
            if(m_micClient==null)
                initClient();
            m_micClient.startMicAndRecognition();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }
    public void initClient() throws Exception
    {
        m_micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(m_activity,
                m_recoMode,
                OxfordRecognitionManager.instance().getLanguage(),
                this,
                OxfordRecognitionManager.instance().getSpeechKey().getPrimary());
        if (m_micClient == null) {
            sendHandlerMessage("Started", Boolean.toString(false));
            throw new Exception("Fail to connect to SpeechRecognitionService");
        }
        else {
            sendHandlerMessage("Started", Boolean.toString(true));
            Log.d(TAG,"SpeechClientInit");
        }
    }

    public void closeClient()
    {
        if (m_micClient != null)
        {
            boolean isReceivedResponse = m_micClient.waitForFinalResponse(m_waitSeconds);
            m_micClient.endMicAndRecognition();
            sendHandlerMessage("Ended", "yes");
        }
    }
    @Override
    public void onPartialResponseReceived(String s) {
        if(m_eventCallback!=null)
            m_eventCallback.onPartialResponseReceived(s);
        sendHandlerMessage("PartialResult: ", s);
        Log.d(TAG,"Partial "+s);
    }
    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult) {
        if(m_eventCallback!=null)
            m_eventCallback.onFinalResponseReceived(recognitionResult);

        boolean isFinalDicationMessage = m_recoMode == SpeechRecognitionMode.LongDictation &&
                (recognitionResult.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        recognitionResult.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (isFinalDicationMessage && m_recoMode == SpeechRecognitionMode.LongDictation) {
            closeClient();
        }
        else if (!isFinalDicationMessage){
            int highestConfidence=-2;
            int result=-1;
            for (int i = 0; i < recognitionResult.Results.length; i++)
            {
                if(highestConfidence<=recognitionResult.Results[i].Confidence.ordinal()) {
                    highestConfidence=recognitionResult.Results[i].Confidence.ordinal();
                    result=i;
                }
                if(result!=-1) {
                    //sendHandlerMessage("HighestConfidenceResult",recognitionResult.Results[result].DisplayText);
                    sendHandlerMessage("HighestConfidenceResult", Integer.toString(result));
                }
            }
        }
    }
    @Override
    public void onIntentReceived(String s) {
        if(m_eventCallback!=null)
            m_eventCallback.onIntentReceived(s);
    }

    @Override
    public void onError(int i, String s){
        if(m_eventCallback!=null)
            m_eventCallback.onError(i, s);
        sendHandlerMessage("Error,", Integer.toString(i) + "#" + s);
        Log.d(TAG,"Error...");
    }

    @Override
    public void onAudioEvent(boolean b) {
        if(m_eventCallback!=null)
            m_eventCallback.onAudioEvent(b);
        sendHandlerMessage("AudioEvent", Boolean.toString(b));
    }
    void sendHandlerMessage(String key, String value)
    {
        if(m_handler==null)
            return;
        Bundle bundle=new Bundle();
        bundle.putString(key, value);
        Message msg=new Message();
        msg.setData(bundle);
        m_handler.sendMessage(msg);
        Log.d(TAG,"sending message "+msg.toString());
    }
}
