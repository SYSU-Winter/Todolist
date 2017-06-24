package com.example.hp.todolist;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import android.util.Log;

public class MyHttpPost {
    // 服务器地址
    private String SERVER = "http://172.18.70.88:8080";
    // 项目地址
    private String PROJECT = "/LoginServer/";

    public void executeHttpPost(String servlet, final Map<String, String> params, final VolleyCallback volleyCallback) {
        final String baseURL = SERVER + PROJECT + servlet;
        // 创建请求队列
        // 在MySingleton中实现, 单例模式

        // 创建一个post请求
        StringRequest stringRequest = new StringRequest(Request.Method.POST, baseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String responseMsg = new String(response.getBytes(), "utf-8");
                            Log.i("tag", "MyHttpPost: responseMsg = 我爱你： " + responseMsg);
                            volleyCallback.onSuccess(responseMsg);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        //stringRequest.setShouldCache(false);

        // 将请求添加到请求队列中
        MySingleton.getInstance(MyApplication.getContext()).addToRequestQueue(stringRequest);
        //volleyCallback.onSuccess("妈卖逼");
        Log.i("tag", "MyHttpPost: responseMsg =  " + "滚");
    }
}
