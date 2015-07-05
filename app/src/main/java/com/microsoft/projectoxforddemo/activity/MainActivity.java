package com.microsoft.projectoxforddemo.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.OxfordRecognitionManager;


/**
 * Created by admin on 7/2/2015.
 */
public class MainActivity extends BaseActivity {
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

    void loadComponents() {
        //load the AzureKeys
        OxfordRecognitionManager.instance().setSpeechAPIKey(getResources().getString(R.string.speech_primary_key), getResources().getString(R.string.speech_minor_key));
        OxfordRecognitionManager.instance().setFaceAPIKey(getResources().getString(R.string.face_primary_key), getResources().getString(R.string.face_minor_key));
        OxfordRecognitionManager.instance().setLanguage("en-us");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Not implemented
                break;
        }
        return true;
    }

}
