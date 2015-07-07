package com.microsoft.projectoxforddemo.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

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
                }
                return true;
            }
        });
        popMenu.show();
    }

    void setLanguagePreference() {
        final SharedPreferences settings = getApplicationContext().getSharedPreferences("setting", MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
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
                editor.clear();
                editor.putString("Lang", "ch-zn");
                OxfordRecognitionManager.instance().setLanguage("ch-zh");
                editor.commit();
            }
        });
        layout.findViewById(R.id.activity_main_setting_lang_en).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                editor.clear();
                editor.putString("Lang", "en-us");
                OxfordRecognitionManager.instance().setLanguage("en-us");
                editor.commit();
            }
        });
    }

    void setShowingFaceAttributes() {
        final SharedPreferences settings = getApplicationContext().getSharedPreferences("setting", MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
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
                editor.clear();
                editor.putBoolean("ShowingFaceAttr", true);
                editor.commit();
                //Log.d("SettingShowingFacialAttr","#yes");
            }
        });
        layout.findViewById(R.id.activity_main_setting_face_attr_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                editor.clear();
                editor.putBoolean("ShowingFaceAttr", false);
                //Log.d("SettingShowingFacialAttr","#no");
                editor.commit();
            }
        });
    }
}
