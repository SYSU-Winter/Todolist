package com.example.hp.todolist;

import android.util.Log;

import java.util.Map;

public class LoginPostService {

    public void send(Map<String, String> params, VolleyCallback volleyCallback) {
        // 定位服务器的Servlet
        String servlet = "LoginServlet";
        MyHttpPost myHttpPost = new MyHttpPost();
        myHttpPost.executeHttpPost(servlet, params, volleyCallback);
        Log.i("tag", "LoginService: responseInt = " + 666);
    }
}
