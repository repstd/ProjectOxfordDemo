package com.microsoft.projectoxforddemo.fragment;

import android.support.v7.widget.Toolbar;

/**
 * Created by admin on 7/3/2015.
 */
public interface Container {

    String getName();

    Toolbar getToolbar();

    String getToolbarTitle();

    void setToolbarTitle(String text);

    void setToolbarIcon(int drawable);
}
