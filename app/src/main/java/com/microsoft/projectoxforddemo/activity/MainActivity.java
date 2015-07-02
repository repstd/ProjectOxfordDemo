package com.microsoft.projectoxforddemo.activity;

import android.os.Bundle;

import com.microsoft.projectoxforddemo.R;


/**
 * Created by admin on 7/2/2015.
 */
public class MainActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    int getLayoutID() {
        return R.layout.activity_main;
    }
    @Override
    String getToolbarTitle() {
        return "ProjectOxfordDemo";
    }

    @Override
    String getName() {
        return "MainActivity";
    }
    void loadComponents() {}
}
