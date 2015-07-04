package com.microsoft.projectoxforddemo.utils;

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

