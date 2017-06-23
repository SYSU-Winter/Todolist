package com.example.hp.todolist;

import java.util.Map;

public class RegisterPostService {

    public void send(Map<String, String> params, VolleyCallback volleyCallback) {
        // 定位服务器的Servlet
        String servlet = "RegisterServlet";
        // 通过 POST 方式获取 HTTP 服务器数据
        MyHttpPost myHttpPost = new MyHttpPost();
        myHttpPost.executeHttpPost(servlet, params, volleyCallback);
    }
}
