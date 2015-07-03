package com.microsoft.projectoxforddemo.fragment;

/**
 * Created by admin on 7/3/2015.
 */
public class FragmentBuilder {
    private static FragmentBuilder m_inst = null;
    private Container m_fragmentContainer;

    public static FragmentBuilder instance() {
        synchronized (FragmentBuilder.class) {
            if (m_inst == null)
                m_inst = new FragmentBuilder();
        }
        return m_inst;
    }

    FragmentBuilder context(Container c) {
        m_fragmentContainer = c;
        return this;
    }

    BaseFragment createSpeechFragment() {
        SpeechDemoFragment frag = new SpeechDemoFragment();
        if (m_fragmentContainer != null)
            frag.setContainer(m_fragmentContainer);
        return frag;
    }

    BaseFragment createFaceFragment() {
        FaceDemoFragment frag = new FaceDemoFragment();
        if (m_fragmentContainer != null)
            frag.setContainer(m_fragmentContainer);
        return frag;
    }
}
