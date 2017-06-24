package com.example.hp.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Map;
import java.util.Vector;

/**
 * Created by Administrator on 2016/12/10.
 */

public class MyLVAdapter extends BaseAdapter {
    private Context context;
    private Handler handler;
    private Map<String, Vector<String>> tasks;
    private boolean finished;

    public MyLVAdapter(Context context, Handler handler, Map<String, Vector<String>> tasks, boolean finished) {
        this.context = context;
        this.handler = handler;
        this.tasks = tasks;
        this.finished = finished;
    }

    @Override
    public int getCount() {
        if (tasks.get("titles") != null) {
            return tasks.get("titles").size();
        }
        return 0;
    }

    // 返回String类型的标题
    @Override
    public Object getItem(int position) {
        return tasks.get("titles").get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_in_main, null);
        }

        CheckBox finishCB = (CheckBox) convertView.findViewById(R.id.finishCB);
        TextView titleTV = (TextView) convertView.findViewById(R.id.item_title);
        TextView dateTV = (TextView) convertView.findViewById(R.id.item_date);

        finishCB.setChecked(finished);
        titleTV.setText(tasks.get("titles").get(position));
        dateTV.setText(tasks.get("dates").get(position));

        finishCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String title = tasks.get("titles").get(position);
                        String date = tasks.get("dates").get(position);
                        Intent intent = new Intent();
                        intent.putExtra("isfinished", finished);
                        intent.putExtra("title", title);
                        intent.putExtra("date", date);
                        Message message = new Message();
                        message.obj = intent;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
        return convertView;
    }
}
