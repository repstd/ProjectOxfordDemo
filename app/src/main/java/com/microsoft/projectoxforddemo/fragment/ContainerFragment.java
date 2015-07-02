package com.microsoft.projectoxforddemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.microsoft.projectoxforddemo.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by admin on 7/2/2015.
 */
public class ContainerFragment extends  BaseFragment
{
    private Toolbar m_toolbar;
    private PagerSlidingTabStrip m_tabStrip;
    private ViewPager m_viewPager;
    private ArrayList<String> m_tabTitles;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()),container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
    }
    @Override
    void loadComponents() {
        m_tabTitles=new ArrayList<String>(Arrays.asList(getActivity().getResources().getStringArray(R.array.fragmemt_container_tabs_title)));
        m_toolbar=(Toolbar)getView().findViewById(R.id.activity_main_toolbar);
        m_toolbar.setTitle(getToolbarTitle());
        m_toolbar.setNavigationIcon(R.drawable.abc_ic_voice_search_api_mtrl_alpha);
        m_toolbar.setTitleTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        m_tabStrip=(PagerSlidingTabStrip)getView().findViewById(R.id.fragment_container_tab);
        m_tabStrip.setClickable(true);
        m_tabStrip.setShouldExpand(true);
        m_tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        m_viewPager=(ViewPager)getView().findViewById(R.id.fragment_container_view_pager);
        if(m_viewPager!=null) {
            m_viewPager.setAdapter(new ContainerPagerAdapter(getActivity().getSupportFragmentManager()));
            m_tabStrip.setViewPager(m_viewPager);
        }
    }

    class ContainerPagerAdapter extends  FragmentPagerAdapter
    {
        public ContainerPagerAdapter(FragmentManager fm){
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return m_tabTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            //return new BlankFragment();
            BaseFragment frag=null;
            switch (position) {
                case 0:
                    frag=SpeechDemoFragment.instance();
                    break;
                case 1:
                    frag=FaceDemoFragment.instance();
                    break;
                default:
                    break;
            }
            return frag;
        }
        @Override
        public int getCount() {
            return m_tabTitles.size();
        }
    }

    @Override
    public String getToolbarTitle() {
        return "ProjectOxfordTest";
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_container;
    }
}
