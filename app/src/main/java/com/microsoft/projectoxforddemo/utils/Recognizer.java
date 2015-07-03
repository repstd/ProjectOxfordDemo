package com.microsoft.projectoxforddemo.utils;

import android.util.Log;

import com.microsoft.ProjectOxford.ISpeechRecognitionServerEvents;
import com.microsoft.ProjectOxford.MicrophoneRecognitionClient;
import com.microsoft.ProjectOxford.RecognitionResult;
import com.microsoft.ProjectOxford.SpeechRecognitionMode;
import com.microsoft.ProjectOxford.SpeechRecognitionServiceFactory;

/**
 * Created by admin on 7/3/2015.
 */
public class Recognizer<T extends ISpeechRecognitionServerEvents> extends Thread implements ISpeechRecognitionServerEvents {
    private final String TAG = "Recognizer";
    SpeechRecognitionMode m_recoMode;
    private T m_impl;
    private MicrophoneRecognitionClient m_micClient;
    private int m_waitSeconds;

    public Recognizer(T impl) {
        m_impl = impl;
        m_recoMode = SpeechRecognitionMode.ShortPhrase;
        m_micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
                m_recoMode,
                OxfordRecognitionManager.instance().getLanguage(),
                this,
                OxfordRecognitionManager.instance().getSpeechKey().getPrimary());
        m_waitSeconds = m_recoMode == SpeechRecognitionMode.ShortPhrase ? 20 : 200;
    }

    public void Close() {
        if (m_micClient == null) {
            Log.d(TAG, "Error.Exceptional Exited.");
            return;
        }
        m_micClient.waitForFinalResponse(m_waitSeconds);
        m_micClient.endMicAndRecognition();
        m_micClient.dispose();
        Log.d(TAG, "exited");
    }

    @Override
    public void run() {
        m_micClient.startMicAndRecognition();
    }

    @Override
    public void onPartialResponseReceived(String s) {
        getImpI().onPartialResponseReceived(s);
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult) {
        getImpI().onFinalResponseReceived(recognitionResult);
    }

    @Override
    public void onIntentReceived(String s) {
        getImpI().onIntentReceived(s);
    }

    @Override
    public void onError(int i, String s) {
        getImpI().onError(i, s);
    }

    @Override
    public void onAudioEvent(boolean b) {
        getImpI().onAudioEvent(b);
    }

    public ISpeechRecognitionServerEvents getImpI() {
        return m_impl;
    }
}
