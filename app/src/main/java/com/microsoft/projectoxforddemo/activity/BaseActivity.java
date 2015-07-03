package com.microsoft.projectoxforddemo.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.microsoft.projectoxforddemo.R;


/**
 * Created by admin on 7/2/2015.
 */
public abstract class BaseActivity extends ActionBarActivity {
    private final String TAG = "BaseActivity";
    private Toolbar m_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        loadToolbar();
        loadComponents();
    }

    abstract int getLayoutID();

    abstract String getToolbarTitle();

    abstract String getName();

    abstract void loadComponents();

    void loadToolbar() {
        try {
            m_toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
            if (m_toolbar != null) {
                setSupportActionBar(m_toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            m_toolbar.setTitle(getTitle());
            m_toolbar.setTitleTextColor(getResources().getColor(R.color.background_floating_material_dark));
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
