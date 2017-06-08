package com.imagine.scott.netcar.operation;

import android.util.Log;

import com.imagine.scott.netcar.activity.MainActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Scott on 2016/5/6.
 */
public class RequestJuHe {

    public String getReasult(String url, Map<String, Object> paramsMap) {
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder responseResult = new StringBuilder();
        StringBuilder params = new StringBuilder();
        HttpURLConnection conn = null;
        // 组织请求参数

        Iterator it = paramsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            params.append(element.getKey());
            params.append("=");
            String value = "";

            try {
                value = URLEncoder.encode(element.getValue().toString(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            params.append(value);
            params.append("&");
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            printWriter = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
            // 发送请求参数
            printWriter.write(params.toString());
            // flush输出流的缓冲
            printWriter.flush();
            // 根据ResponseCode判断连接是否成功
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Log.v("Post Error! =", Integer.toString(responseCode));
            } else {
                Log.v("Post Success!", Integer.toString(responseCode));
            }
            // 定义BufferedReader输入流来读取URL的ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseResult.toString();
    }

}
