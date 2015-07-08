package com.microsoft.projectoxforddemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by admin on 7/3/2015.
 */
public class OxfordRecognitionManager {
    static final String NOTSET = "KeyNotSet";
    static OxfordRecognitionManager m_inst = new OxfordRecognitionManager();
    private Key m_speechKey = null;
    private Key m_faceKey = null;
    private String m_lang;

    OxfordRecognitionManager() {
        //set the language to English by default;
        m_lang = "en-us";
    }

    public static OxfordRecognitionManager instance() {
        return m_inst;
    }

    public void setSpeechAPIKey(String primary, String minor) {
        m_speechKey = new Key(primary, minor);
    }

    public void setFaceAPIKey(String primary, String minor) {
        m_faceKey = new Key(primary, minor);
    }

    public Key getSpeechKey() {
        return m_speechKey;
    }

    public Key getFaceKey() {
        return m_faceKey;
    }

    public String getLanguage() {
        return m_lang;
    }

    public void setLanguage(String lang) {
        m_lang = lang;
    }

    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isAvailable() && wifiInfo.isConnected()) {
            return true;
        } else if (mobileInfo != null && mobileInfo.isAvailable() && mobileInfo.isConnected()) {
            Toast.makeText(context, "Wifi suggested in order to use Oxford service.", Toast.LENGTH_LONG).show();
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setTitle("Warning");
            builder.setMessage("NetworkUnavailable");
            builder.setPositiveButton("Okay", null);
            builder.show();
            return false;
        }
    }

    public class Key {
        private String m_primaryKey = NOTSET;
        private String m_minorKey = NOTSET;

        public Key(String primary, String minor) {
            m_primaryKey = primary;
            m_minorKey = minor;
        }

        public String getPrimary() {
            return m_primaryKey;
        }

        public String getMinor() {
            return m_minorKey;
        }

        @Override
        public String toString() {
            return "Key:[Primary = " + m_primaryKey + " Minor = " + m_minorKey + " ]";
        }
    }
}

