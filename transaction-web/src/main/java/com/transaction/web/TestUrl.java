package com.transaction.web;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by HuaWeiBo on 2019/4/16.
 */
public class TestUrl {
    public static String result(String info) {
        String requestUrl = "http://localhost:8070/hello/distributedNoDelay";
        Map params = new HashMap();
        params.put("goodsId", 1);
        params.put("userId", 1);
        params.put("count", 1);
        String string = httpRequest(requestUrl,params);
        return string;
    }

    private static String httpRequest(String requestUrl,Map params) {
        StringBuffer buffer = new StringBuffer();
        try {
            String s = requestUrl + "?" + urlencode(params);
            // 多线程并发访问
            int num = 5;
            CountDownLatch cdl = new CountDownLatch(num);
            for (int i = 0; i < num; i++) {
                URL url = new URL(s);
                new Thread(()->{
                    try {
                        //打开http连接
                        HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
                        httpUrlConn.setDoInput(true);
                        httpUrlConn.setRequestMethod("POST");
                        cdl.await();
                        httpUrlConn.connect();
                        InputStream inputStream = httpUrlConn.getInputStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                cdl.countDown();
            }

            /*//获得输入
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //将bufferReader的值给放到buffer里
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            //关闭bufferReader和输入流
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            //断开连接
            httpUrlConn.disconnect();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回字符串
        return buffer.toString();
    }

    public static String urlencode(Map<String,Object>data) {
        //将map里的参数变成像 showapi_appid=###&showapi_sign=###&的样子
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
    //测试是否有效
    public static void main(String[] args) {

        System.out.println(result("你好啊"));
    }
}
