package com.microsoft.projectoxforddemo.utils.ISpeechClient;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by v-yuliwa on 7/24/2015.
 */
public class SpeechClient
{
    public static String HOST="http://speech.platform.bing.com/recognize/query";
    public static String TAG="SpeechClient";
    private String m_subscriptionkey;
    private WebServiceRequest m_call;
    private RecognitionParams m_para;
    private Context m_context;
    public static class RecognitionParams {
        public RecognitionParams(){}
        public String lang;
        public String recognitionMode;
        public String codec;
        public int samplerate=-1;
        public int sourcerate=-1;
        public boolean trustsourcerate=false;
    }
    public SpeechClient(Context context,RecognitionParams para,String key) {
        this.m_para=para;
        this.m_subscriptionkey=key;
        this.m_context=context;
    }
    public HttpResponse recognize(String audioName) {
        final String audioPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/ProjectOxford";
        this.m_call=new WebServiceRequest();
        Map<String,Object> header=getHeader();
        Map<String,Object> inputPara=getPara();
        Map<String,Object> data=new HashMap<>();
        File audio=new File(audioPath,audioName);
        if(!audio.isFile()||!audio.exists()) {
            Log.d(TAG, audioPath+ " not exists");
            return null;
        }
        try {
            byte[] b = new byte[(int) audio.length()];
            InputStream input=new FileInputStream(audio);
            int len=input.read(b);
            input.close();
            data.put("data",b);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return this.m_call.request(this.HOST,"POST",header,inputPara,data);
    }
    Map<String,Object> getHeader() {
        Map<String,Object> header=new HashMap<>();
        String result="";
        result+=this.m_para.codec;
        result+=";";
        if(this.m_para.samplerate!=-1)
            result+="samplerate="+this.m_para.samplerate;
        result+=";";
        if(this.m_para.sourcerate!=-1)
            result+="sourcerate="+this.m_para.sourcerate;
        result+=";";
        if(this.m_para.trustsourcerate)
            result+="trustsourcerate=true";
        else
            result+="trustsourcerate=false";
        result+=";";
        header.put("Content-Type",result);
        return header;
    }
    Map<String,Object> getPara() {
        Map<String,Object> inputPara=new HashMap<>();
        try {
            inputPara.put("Version","3.0");
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            inputPara.put("requestid",uuid);
            inputPara.put("appID","D4D52672-91D7-4C74-8AD8-42B1D98141A5");
            inputPara.put("format", URLEncoder.encode("json","UTF-8"));
            inputPara.put("locale",URLEncoder.encode(this.m_para.lang,"UTF-8"));
            inputPara.put("device.os",URLEncoder.encode("Android","UTF-8"));
            inputPara.put("scenarios",URLEncoder.encode("ulm","UTF-8"));
            TelephonyManager tManager = (TelephonyManager)this.m_context.getSystemService(Context.TELEPHONY_SERVICE);
            inputPara.put("instanceid",URLEncoder.encode(tManager.getDeviceId(),"UTF-8"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return inputPara

                ;
    }
}
