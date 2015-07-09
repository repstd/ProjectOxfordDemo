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
public class SpeechRecognition extends Thread implements ISpeechRecognitionServerEvents
{
    private final String TAG = "SpeechRecognition";
    public final static String HandlerKeyHighestConfidenceResult="HighestConfidenceResult";
    public final static String HandlerKeyPartialResult="PartialResult";
    public final static String HandlerKeyEvent="Event";
    public final static String HandlerKeyException="Exception";
    public final static String HandlerValueAudioReady="AudioService Started";
    private MicrophoneRecognitionClient m_micClient = null;
    private SpeechRecognitionMode m_recoMode;
    private ISpeechRecognitionServerEvents m_eventCallback;
    private Handler m_handler;
    private Activity m_activity;
    private int m_waitSeconds;
    private boolean m_isActive;
    private int m_highestConfidence=-2;
    private String m_hignestResult=null;
    public SpeechRecognition(Activity activity, ISpeechRecognitionServerEvents eventCallback, Handler handler) {
        this.m_activity = activity;
        this.m_handler = handler;
        this.m_eventCallback = eventCallback;
        m_recoMode = SpeechRecognitionMode.ShortPhrase;
        m_waitSeconds = m_recoMode == SpeechRecognitionMode.ShortPhrase ? 20 : 200;
        m_isActive = false;
    }

    public SpeechRecognition(Activity activity, ISpeechRecognitionServerEvents eventCallback, Handler handler, SpeechRecognitionMode mode) {
        this.m_activity = activity;
        this.m_handler = handler;
        this.m_eventCallback = eventCallback;
        m_recoMode = mode;
        m_waitSeconds = m_recoMode == SpeechRecognitionMode.ShortPhrase ? 50 : 500;
        m_isActive = false;
    }

    @Override
    public void run()
    {

        if (m_micClient == null)
            initClient();
        m_micClient.startMicAndRecognition();
    }

    void initClient(){
        try {
            m_micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(m_activity,
                    m_recoMode,
                    OxfordRecognitionManager.instance().getLanguage(),
                    this,
                    OxfordRecognitionManager.instance().getSpeechKey().getPrimary());
        }
        catch (Exception e) {
            sendHandlerMessage(HandlerKeyException, "Exception#"+e.getMessage());
        }
        if (m_micClient == null) {
            sendHandlerMessage(HandlerKeyEvent, "SpeechClientFailedToStart");
            return;
        }
        m_isActive = true;
        sendHandlerMessage(HandlerKeyEvent, "SpeechClientStart");
        Log.d(TAG, "SpeechClientInit");
    }
    synchronized public void closeClient()  {
        Thread th=new Thread(new Runnable() {
            @Override
            public void run()  {
                closeClientRun();
            }
        });
        th.start();
        m_handler=null;
        Log.d(TAG,"ClosingThreadStart");
    }
    public void closeClientRun()
    {
        if (m_micClient != null)
        {
            m_isActive = false;
            //boolean isReceivedResponse = m_micClient.waitForFinalResponse(m_waitSeconds);
            m_micClient.endMicAndRecognition();
            sendHandlerMessage(HandlerKeyEvent, "SpeechClientClosed");
            if(m_recoMode==SpeechRecognitionMode.LongDictation) {
                Log.d(TAG,"sending final result for LongDictation");
                sendHandlerMessage(HandlerKeyHighestConfidenceResult,m_hignestResult);
            }
            m_micClient = null;
            Log.d(TAG,"ClosingThreadEnded");
        }
    }

    synchronized public boolean isActive() {
        return m_isActive;
    }

    @Override
    public void onPartialResponseReceived(String s) {
        if (m_eventCallback != null)
            m_eventCallback.onPartialResponseReceived(s);
        sendHandlerMessage(HandlerKeyPartialResult, s);
        Log.d(TAG, "Partial " + s);
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult)
    {
        if (m_eventCallback != null)
            m_eventCallback.onFinalResponseReceived(recognitionResult);
        boolean isFinalDicationMessage = isFinalDicationMessage(recognitionResult);
        updateResult(recognitionResult);
        if (isFinalDicationMessage && m_recoMode == SpeechRecognitionMode.LongDictation)
            closeClient();
        else if(!isFinalDicationMessage)
            sendHandlerMessage(HandlerKeyHighestConfidenceResult,m_hignestResult);
    }
    boolean isFinalDicationMessage(RecognitionResult recognitionResult)
    {
        boolean isFinalDicationMessage = m_recoMode == SpeechRecognitionMode.LongDictation &&
                (recognitionResult.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        recognitionResult.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        return isFinalDicationMessage;
    }
    String resultFilter(RecognitionResult recognitionResult)
    {
        //choose a result of the highest confidence
        int highestConfidence = -2;
        int result = -1;
        for (int i = 0; i < recognitionResult.Results.length; i++) {
            if (highestConfidence <= recognitionResult.Results[i].Confidence.ordinal()) {
                highestConfidence = recognitionResult.Results[i].Confidence.ordinal();
                result = i;
            }
        }
        if(result==-1)
            return null;
        else
            return recognitionResult.Results[result].DisplayText;
    }
    void updateResult(RecognitionResult recognitionResult)
    {
        for (int i = 0; i < recognitionResult.Results.length; i++)
        {
            if (m_highestConfidence <= recognitionResult.Results[i].Confidence.ordinal())
            {
                m_highestConfidence = recognitionResult.Results[i].Confidence.ordinal();
                m_hignestResult=recognitionResult.Results[i].DisplayText;
            }
        }
    }
    @Override
    public void onIntentReceived(String s) {
        if (m_eventCallback != null)
            m_eventCallback.onIntentReceived(s);
    }

    @Override
    public void onError(int i, String s) {
        if (m_eventCallback != null)
            m_eventCallback.onError(i, s);
        sendHandlerMessage(HandlerKeyEvent, "Error"+" " +s);
        Log.d(TAG, "Error...");
    }

    @Override
    public void onAudioEvent(boolean b) {
        if (m_eventCallback != null)
            m_eventCallback.onAudioEvent(b);
        if(b)
            sendHandlerMessage(HandlerKeyEvent, HandlerValueAudioReady);
        else
            sendHandlerMessage(HandlerKeyEvent, "AudioService Close");
    }

    synchronized void sendHandlerMessage(String key, String value) {
        if (m_handler == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        Message msg = new Message();
        msg.setData(bundle);
        m_handler.sendMessage(msg);
        Log.d(TAG, "sending message " + msg.toString());
    }
    public SpeechRecognitionMode getRecognitionMode() {
        return m_recoMode;
    }
}
