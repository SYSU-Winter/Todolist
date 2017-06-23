package com.example.hp.todolist;

import android.app.Application;
import android.content.Context;

/**
 * Created by 12136 on 2017/5/27.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = getApplicationContext();
    }

    //返回
    public static Context getContext(){
        return context;
    }

}
