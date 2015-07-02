package com.microsoft.projectoxforddemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.projectoxforddemo.R;

/**
 * Created by admin on 7/2/2015.
 */
public class FaceDemoFragment extends BaseFragment
{
    private static FaceDemoFragment m_inst=null;
    public static FaceDemoFragment instance() {
        if(m_inst==null)
            m_inst=new FaceDemoFragment();
        return m_inst;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    void loadComponents() {

    }
    @Override
    public String getToolbarTitle() {
        return "FaceDemo";
    }

    @Override
    int getLayoutId() {
        return R.layout.layout_blank;
    }
}
