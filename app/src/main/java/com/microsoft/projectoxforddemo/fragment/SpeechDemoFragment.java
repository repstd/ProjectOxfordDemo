package com.microsoft.projectoxforddemo.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.ProjectOxford.ISpeechRecognitionServerEvents;
import com.microsoft.ProjectOxford.RecognitionResult;
import com.microsoft.ProjectOxford.RecognizedPhrase;
import com.microsoft.ProjectOxford.SpeechRecognitionMode;
import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.OxfordRecognitionManager;
import com.microsoft.projectoxforddemo.utils.SpeechRecognition;

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
    private SpeechRecognition m_speechClient = null;


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
                m_fabMenu.collapse();
                initClient();
            }
        });
        m_fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_fabMenu.collapse();
                closeClient();
            }
        });
    }

    void holdAndSpeak() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        View speechLayout = (View) getActivity().getLayoutInflater().inflate(R.layout.fragment_speech_hold_and_speak, null);
        speechLayout.findViewById(R.id.fragment_speech_demo_hold_and_speak).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        builder.setView(speechLayout);
        builder.show();
    }

    void initClient() {
        clearText();
        if (!OxfordRecognitionManager.instance().isNetworkAvailable(this.getActivity()))
            return;
        if (m_speechClient != null)
            closeClient();
        m_speechClient = new SpeechRecognition(this.getActivity(), this, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String finalResult=msg.getData().getString(SpeechRecognition.HandlerKeyHighestConfidenceResult);
                if(finalResult!=null&&!finalResult.isEmpty()) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(SpeechDemoFragment.this.getActivity(),AlertDialog.THEME_HOLO_DARK);
                    builder.setTitle("RecognizedResult:");
                    builder.setMessage(finalResult);
                    builder.setPositiveButton("Done",null);
                    builder.show();
                }
            }
        }, SpeechRecognitionMode.LongDictation);
        m_speechClient.start();
        updateTextView("Client Started");
    }

    void closeClient() {
        if (m_speechClient == null||!m_speechClient.isActive())
            return;
        updateTextView("Closing Client");
        m_speechClient.closeClientRun();
        updateTextView("Client Closed");
        m_speechClient = null;
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

    String parseResult(RecognizedPhrase phrase) {
        return "[Text: " + phrase.DisplayText + " Confidence: " + phrase.Confidence+" ]";
    }

    //Interfaces for Speech Recognition
    @Override
    public void onPartialResponseReceived(String s) {
        updateTextView("Partial " + s);
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult recognitionResult) {
        for(int i=0;i<recognitionResult.Results.length;i++) {
            updateTextView("Final: "+parseResult(recognitionResult.Results[i]));
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
        if (b)
            updateTextView("AudioEvent: MicroPhone On");
        else
            updateTextView("AudioEvent: MicroPhone Off");
    }

    @Override
    public void onPageShifted() {
        m_container.setToolbarIcon(R.drawable.ic_keyboard_voice_black_24dp);
        m_container.setToolbarTitle(getToolbarTitle());
        clearText();
        closeClient();
        if (m_fabMenu.isExpanded())
            m_fabMenu.collapse();
    }
}
