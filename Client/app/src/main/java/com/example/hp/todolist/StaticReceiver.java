package com.example.hp.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by HP on 2016/10/19.
 */

public class StaticReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Intent notifyIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 0, notifyIntent, 0);
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_todo);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("TodoList")
                .setContentText(data.getInt("count") > 0 ? "你今天还有"+ data.getInt("count") + "项任务。" : "今天没有任务")
                .setTicker("You have a message.")
                .setSmallIcon(R.mipmap.ic_notification)
                .setLargeIcon(bmp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(0, notify);
    }
}
