package com.microsoft.projectoxforddemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.FragmentBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by admin on 7/2/2015.
 */

public class ContainerFragment extends BaseFragment implements Container {
    static BaseFragment frag;
    private final String TAG = "ContainerFragment";
    ArrayList<SubFragment> m_subFragmentList;
    private Toolbar m_toolbar;
    private PagerSlidingTabStrip m_tabStrip;
    private ViewPager m_viewPager;
    private ArrayList<String> m_tabTitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_subFragmentList = new ArrayList<SubFragment>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()), container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
    }

    @Override
    void loadComponents() {
        m_tabTitles = new ArrayList<String>(Arrays.asList(getActivity().getResources().getStringArray(R.array.fragmemt_container_tabs_title)));
        m_toolbar = (Toolbar) getView().findViewById(R.id.activity_main_toolbar);
        m_toolbar.setTitle(getToolbarTitle());
        m_toolbar.setNavigationIcon(R.drawable.abc_ic_voice_search_api_mtrl_alpha);
        m_toolbar.setTitleTextColor(Color.WHITE);
        m_tabStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.fragment_container_tab);
        m_tabStrip.setShouldExpand(true);
        m_tabStrip.setTextSize((int) getActivity().getResources().getDimension(R.dimen.tab_stripe_text_size));
        m_tabStrip.setTextColor(Color.WHITE);
        m_viewPager = (ViewPager) getView().findViewById(R.id.fragment_container_view_pager);
        if (m_viewPager != null) {
            m_viewPager.setAdapter(new ContainerPagerAdapter(getActivity().getSupportFragmentManager()));
            m_tabStrip.setViewPager(m_viewPager);
            m_tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    m_subFragmentList.get(position).onPageShifted();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public String getToolbarTitle() {
        return "ProjectOxfordTest";
    }

    @Override
    public void setToolbarTitle(String text) {
        if (m_toolbar != null)
            m_toolbar.setTitle(text);
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    public String getName() {
        return "HomeFragmentContainer";
    }

    @Override
    public Toolbar getToolbar() {
        if (m_toolbar != null)
            return m_toolbar;
        else {
            Log.d(TAG, "falied to return Toolbar because of incorrect initialization.");
            return null;
        }
    }

    @Override
    public void setToolbarIcon(int drawable) {
        if (m_toolbar != null)
            m_toolbar.setNavigationIcon(drawable);
    }

    class ContainerPagerAdapter extends FragmentPagerAdapter {

        public ContainerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return m_tabTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    frag = FragmentBuilder.instance().context(ContainerFragment.this).createFaceFragment();
                    break;
                case 1:
                    frag = FragmentBuilder.instance().context(ContainerFragment.this).createSpeechFragment();
                    break;
                default:
                    break;
            }
            if (frag != null)
                m_subFragmentList.add((SubFragment) frag);
            return frag;
        }

        @Override
        public int getCount() {
            return m_tabTitles.size();
        }
    }
}
