package com.microsoft.projectoxforddemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.ProjectOxford.ISpeechRecognitionServerEvents;
import com.microsoft.ProjectOxford.MicrophoneRecognitionClient;
import com.microsoft.ProjectOxford.RecognitionResult;
import com.microsoft.ProjectOxford.RecognitionStatus;
import com.microsoft.ProjectOxford.RecognizedPhrase;
import com.microsoft.ProjectOxford.SpeechRecognitionMode;
import com.microsoft.ProjectOxford.SpeechRecognitionServiceFactory;
import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.OxfordRecognitionManager;

/**
 * Created by admin on 7/2/2015.
 */
public class SpeechDemoFragment extends BaseFragment implements ISpeechRecognitionServerEvents, SubFragment {
    private final String TAG = "SpeechDemoFragment";
    private FloatingActionsMenu m_fabMenu;
    private FloatingActionButton m_fabStart;
    private FloatingActionButton m_fabStop;
    private TextView m_textView;
    private Container m_container = null;
    private MicrophoneRecognitionClient m_micClient = null;
    private SpeechRecognitionMode m_recoMode;
    private boolean m_status = false;
    private int m_waitSeconds;
    private boolean isReceivedResponse;

    public SpeechDemoFragment() {

    }

    public void setContainer(Container c) {
        m_container = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
        addClickListeners();
        m_container.setToolbarIcon(R.drawable.ic_perm_identity_black_24dp);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_recoMode = SpeechRecognitionMode.ShortPhrase;
        m_waitSeconds = m_recoMode == SpeechRecognitionMode.ShortPhrase ? 20 : 200;
    }

    @Override
    public String getToolbarTitle() {
        return "SpeechDemo";
    }

    @Override
    void loadComponents() {
        m_textView = (TextView) getView().findViewById(R.id.fragment_speech_textView);
        m_fabMenu = (FloatingActionsMenu) getView().findViewById(R.id.fragment_speech_fab_menu);
        m_fabStart = (FloatingActionButton) getView().findViewById(R.id.fragment_speech_fab_start);
        m_fabStop = (FloatingActionButton) getView().findViewById(R.id.fragment_speech_fab_show_results);
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_speech_demo;
    }

    void addClickListeners() {
        //final Recognizer recognizer=new Recognizer(this);
        m_fabStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recognizer.start();
                initClient(OxfordRecognitionManager.instance());
                m_fabMenu.collapse();
            }
        });
        m_fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeClient();
                //recognizer.Close();
                m_fabMenu.collapse();
            }
        });
    }

    void updateTextView(String s) {
        if (m_textView == null)
            return;
        m_textView.append(s + "\n");
    }

    void clearText() {
        if (m_textView == null)
            return;
        m_textView.setText("RealTimeResults:\n");
    }

    void initClient(OxfordRecognitionManager manager) {
        if (m_status)
            return;
        clearText();
        m_micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(getActivity(),
                m_recoMode,
                manager.getLanguage(),
                this,
                manager.getSpeechKey().getPrimary());
        if (m_micClient != null) {
            updateTextView("SpeechRecognitionStarted.");
            m_status = true;
        } else
            updateTextView("Error in Starting Recognition Client");
        if (m_micClient != null)
            m_micClient.startMicAndRecognition();
    }

    void closeClient() {
        if (!m_status)
            return;
        if (m_micClient != null) {
            isReceivedResponse = m_micClient.waitForFinalResponse(m_waitSeconds);
            m_micClient.endMicAndRecognition();
        }
        m_status = false;
    }

    String parseResult(RecognizedPhrase phrase) {
        return "Text: " + phrase.DisplayText + " Confidence: " + phrase.Confidence;
    }

        //Interfaces for Speech Recognition
        @Override
        public void onPartialResponseReceived(String s) {
        updateTextView("Partial " + s);
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult)
    {
        boolean isFinalDicationMessage = m_recoMode == SpeechRecognitionMode.LongDictation &&
                (recognitionResult.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        recognitionResult.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (isFinalDicationMessage && m_recoMode == SpeechRecognitionMode.LongDictation) {
            closeClient();
        } else if (!isFinalDicationMessage) {
            for (int i = 0; i < recognitionResult.Results.length; i++)
            {
                updateTextView("FinalResults:[ " + i + " " + parseResult(recognitionResult.Results[i]) + " ]");
            }
        }
    }

    @Override
    public void onIntentReceived(String s) {
        updateTextView("onIntentReceived: " + s);
    }

    @Override
    public void onError(int i, String s) {
        updateTextView("error: " + i + s);
    }

    @Override
    public void onAudioEvent(boolean b) {
        if (!b) {
            closeClient();
        }
        updateTextView("MicroPhone status: " + b);
    }

    @Override
    public void onPageShifted() {
        m_container.setToolbarIcon(R.drawable.ic_keyboard_voice_black_24dp);
        m_container.setToolbarTitle(getToolbarTitle());
        if (m_status) {
            m_micClient.endMicAndRecognition();
            clearText();
        }
        if (m_fabMenu.isExpanded())
            m_fabMenu.collapse();
    }
}
