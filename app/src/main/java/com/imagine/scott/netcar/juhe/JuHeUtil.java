package com.imagine.scott.netcar.juhe;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Scott on 17/4/5.
 */

public class JuHeUtil {
    public static String request(String url) {

        // 生成请求对象
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();

        // 发送请求
        try {
            HttpResponse response = httpClient.execute(httpGet);
            return getResponseResult(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getResponseResult(HttpResponse response) {

        if (null == response) {
            return "";
        }

        HttpEntity httpEntity = response.getEntity();
        try {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line = "";
            while (null != (line = reader.readLine())) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
