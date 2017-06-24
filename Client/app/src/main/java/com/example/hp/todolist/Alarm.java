package com.example.hp.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import static android.content.Context.ALARM_SERVICE;

public class Alarm {

    public static void setAlarm(Context context) {
        MyDB myDB = new MyDB(context);

        Vector<String> Dates = myDB.queryAll(1).get("dates");
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy年MM月dd日,HH:mm");
        String[] date  = new String[Dates.size()];
        for (int i = 0; i < Dates.size(); i++)
            date[i] = Dates.get(i);
        Arrays.sort(date);
        Date alarmDate = new Date(System.currentTimeMillis());
        String dateStr = sdf.format(alarmDate);
        String target = "";
//        if (target != "")
//            Alarm.setAlarm(MainActivity.this, target);
        try {
            for (int i = 0; i < date.length; i++) {
                Date temp = sdf.parse(date[i]);
                Log.v("compare: ", temp.compareTo(alarmDate) + "");
                if (temp.compareTo(alarmDate) < 1) {
                    continue;
                } else {
                    target = date[i];
                    break;
                }
            }
            if (target != "") {
                alarmDate = sdf.parse(target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (target != "") {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alarmDate);
            //calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

            Toast.makeText(context, calendar.getTime().toString() + "", Toast.LENGTH_LONG).show();
        }
    }
}
