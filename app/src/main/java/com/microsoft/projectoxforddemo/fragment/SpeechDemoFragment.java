package com.microsoft.projectoxforddemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.projectoxforddemo.R;

/**
 * Created by admin on 7/2/2015.
 */
public class SpeechDemoFragment extends BaseFragment
{
    private static SpeechDemoFragment m_inst=null;
    public static SpeechDemoFragment instance() {
        if(m_inst==null)
            m_inst=new SpeechDemoFragment();
        return m_inst;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()),container,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
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
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_speech_demo;
    }
}
