package com.example.hp.todolist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Map;
import java.util.Vector;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    //更新widget的广播对应的action
    public static final String ACTION_UPDATE_ALL = "android.appwidget.action.UPDATE_ALL";
    private MyDB myDB;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            myDB = new MyDB(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            refreshContent(context, views);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(ACTION_UPDATE_ALL)) {
            myDB = new MyDB(context);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, NewAppWidget.class);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            refreshContent(context, views);

            appWidgetManager.updateAppWidget(componentName, views);
        }
    }

    public void refreshContent(Context context, RemoteViews views) {
        Map<String,Vector<String>> unfinished = myDB.queryAll(MyDB.UNFINISHED);
        Vector<String> titles = unfinished.get("titles");
        Vector<String> dates = unfinished.get("dates");
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < titles.size(); i++) {
            strBuf.append("Task" +  Integer.toString(i + 1) + ": " + titles.get(i) + "\nDDL: " + dates.get(i) + "\n\n");
        }
        String content = strBuf.toString();
        if (content.equals("")) {
            views.setTextViewText(R.id.unfinishedInWidget, "当前没有任务");
        } else {
            views.setTextViewText(R.id.unfinishedInWidget, content);
        }
    }
}

