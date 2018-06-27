package com.valuecomvikaskumar.searchplaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vikas on 10/6/18.
 */

public class HttpDataHandler {

    public HttpDataHandler(){

    }

    public String getHttpData(String requestUrl) {

        URL url;
        String response="";
        try{
            url =new URL(requestUrl);
            HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/x-www-from-urlencoded");
            int responseCode=conn.getResponseCode();
            if(responseCode== HttpsURLConnection.HTTP_OK){

                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = br.readLine())!=null)
                    response+=line;
            }
            else{ response="";}

    } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
