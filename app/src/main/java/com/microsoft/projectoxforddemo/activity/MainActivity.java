package com.microsoft.projectoxforddemo.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.OxfordRecognitionManager;


/**
 * Created by admin on 7/2/2015.
 */
public class MainActivity extends BaseActivity
{
    private SharedPreferences m_sharedPrefereneSetting;
    private SharedPreferences.Editor m_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_sharedPrefereneSetting = getApplicationContext().getSharedPreferences("setting", MODE_PRIVATE);
        m_editor = m_sharedPrefereneSetting.edit();
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
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(this, ScreenLocker.class);
        startService(serviceIntent);
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
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_HOME))
            return super.onKeyDown(keyCode,event);
        else
            return true;
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
                showSettingsPopup();
                break;
        }
        return true;
    }

    void showSettingsPopup() {
        PopupMenu popMenu = new PopupMenu(this, findViewById(R.id.action_settings));
        popMenu.getMenuInflater().inflate(R.menu.menu_main_setting_popup, popMenu.getMenu());
        popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_setting_item_lang:
                        setLanguagePreference();
                        break;
                    case R.id.action_setting_item_face_attr:
                        setShowingFaceAttributes();
                        break;
                    case R.id.action_setting_item_input_type:
                        setInputType();
                        break;
                }
                return true;
            }
        });
        popMenu.show();
    }

    void setLanguagePreference() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Select Language Preference");
        View layout = getLayoutInflater().inflate(R.layout.activity_main_setting_language, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        builder.show();
        layout.findViewById(R.id.activity_main_setting_lang_ch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LanguagePreference", "onclick");
                dialog.dismiss();
                dialog.cancel();
                m_editor.clear();
                m_editor.putString("Lang", "ch-zn");
                OxfordRecognitionManager.instance().setLanguage("ch-zh");
                m_editor.commit();
            }
        });
        layout.findViewById(R.id.activity_main_setting_lang_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                m_editor.clear();
                m_editor.putString("Lang", "en-us");
                OxfordRecognitionManager.instance().setLanguage("en-us");
                m_editor.commit();
            }
        });
    }

    void setShowingFaceAttributes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Show Face Attributes?");
        View layout = getLayoutInflater().inflate(R.layout.activity_main_setting_show_face_attr, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        builder.show();
        layout.findViewById(R.id.activity_main_setting_face_attr_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                m_editor.clear();
                m_editor.putBoolean("ShowingFaceAttr", true);
                m_editor.commit();
                //Log.d("SettingShowingFacialAttr","#yes");
            }
        });
        layout.findViewById(R.id.activity_main_setting_face_attr_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                m_editor.clear();
                m_editor.putBoolean("ShowingFaceAttr", false);
                //Log.d("SettingShowingFacialAttr","#no");
                m_editor.commit();
            }
        });
    }

    void setInputType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Select How To Input");
        View layout = getLayoutInflater().inflate(R.layout.activity_main_setting_input_type, null);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        builder.show();
        layout.findViewById(R.id.activity_main_setting_input_by_speech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                m_editor.clear();
                m_editor.putString("InputType", "Speech");
                m_editor.commit();
            }
        });
        layout.findViewById(R.id.activity_main_setting_input_by_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                m_editor.clear();
                m_editor.putString("InputType", "Text");
                m_editor.commit();
            }
        });
    }

}
