package com.microsoft.projectoxforddemo.fragment;

import android.support.v7.widget.Toolbar;

/**
 * Created by admin on 7/3/2015.
 */
public interface Container {

    public String getName();

    public Toolbar getToolbar();

    public String getToolbarTitle();

    public void setToolbarTitle(String text);

    public void setToolbarIcon(int drawable);
}
