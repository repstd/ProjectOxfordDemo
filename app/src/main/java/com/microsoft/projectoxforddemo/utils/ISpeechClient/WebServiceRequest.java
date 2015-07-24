package com.microsoft.projectoxforddemo.utils.ISpeechClient;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by v-yuliwa on 7/24/2015.
 */
public class WebServiceRequest
{
    private DefaultHttpClient client = new DefaultHttpClient();

    public HttpResponse request(String host,String method,Map<String,Object> header,Map<String,Object>params,Map<String,Object> data) {
        if(method.equals("POST")) {
            String url= getUrl(host,params);
            return post(url,header,data);
        }
        else
            return null;
    }
    //post a ByteArray
    public HttpResponse post(String url,Map<String,Object> header,Map<String,Object> data) {
        Log.d(getClass().toString(),url);
        HttpPost request=new HttpPost(url);
        for(Map.Entry<String,Object> ele:header.entrySet()) {
            try {
                request.setHeader(ele.getKey(), URLEncoder.encode(ele.getValue().toString(), "UTF-8"));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpResponse response=null;
        try {
            request.setEntity(new ByteArrayEntity((byte[]) data.get("data")));
            response = client.execute(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    public static String getUrl(String host, Map<String, Object> params) {
        String url = host;
        boolean start = true;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (start) {
                url += "?";
                start = false;
            } else {
                url += "&";
            }
            try {
                url += param.getKey() + "=" + URLEncoder.encode(param.getValue().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }
    public static String readInput(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String json = "", line;
        while ((line = br.readLine()) != null) {
            json += line;
        }
        return json;
    }
}
