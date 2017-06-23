package com.example.hp.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();
        Intent aIntent = new Intent(context, Alarming.class);
        aIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(aIntent);
    }

}
