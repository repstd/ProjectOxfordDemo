package com.microsoft.projectoxforddemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by admin on 7/2/2015.
 */
public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;

    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
        this.mContext = mActivity.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
        mContext = null;
    }

    abstract public String getToolbarTitle();

    abstract void loadComponents();

    abstract int getLayoutId();
}
