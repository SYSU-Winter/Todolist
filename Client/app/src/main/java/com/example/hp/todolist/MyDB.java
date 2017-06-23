package com.example.hp.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static android.R.attr.version;

/**
 * Created by lkxal on 2016/12.5.
 */


/**
 * 这里的数据库主要有五个值，分别是_id（自动生成），title（题目），description（详细描述），date（日期），
 * finished（是否已完成），全部都是String
 * 要注意的地方：
 * 1. 日期在数据库中存放都是用的字符串，但是实际上为了能够响闹钟啊什么的在实际拿出来用的时候要转换成DateTime，
 *    所以存入的时候也要先做好格式化才不用几种日期格式换来换去
 * 2. 这里的finished为了用的时候方便，默认取"false"，已完成则置为"true"
 * 3. 也是为了方便，不允许输入同名的日程，这个应该没什么问题没人会写两个一模一样的日程的
 * 4. 用之前先看一下我给每个函数的注释为好
 */

public class MyDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyDB";
    private static final String TABLE_NAME = "TodoList";
    private static final int DB_VERSION = 1;
    public static final int ALL = 0;
    public static final int UNFINISHED = 1;
    public static final int FINISHED = 2;
    Context main;

    private final String CREATE_TABLE = "CREATE TABLE if not exists "
            + TABLE_NAME
            + "(_id INTEGER PRIMARY KEY, title TEXT, description TEXT, date TEXT, finished TEXT)";


    public MyDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        main = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //增
    public void insert(String title, String description, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("date", date);
        cv.put("finished", "false");
        db.insert(TABLE_NAME, null, cv);
        db.close();
        Alarm.setAlarm(main);
    }

    //改
    public void update(String originTitle, String title, String description, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("date", date);
        db.update(TABLE_NAME, cv, "title = ?", new String[]{originTitle});
        db.close();
        Alarm.setAlarm(main);
    }

    //完成某项，也就是把那一项的finished置为true
    public void finishThis(String title) {
        String[] which = findInfo(title);
        Log.i("finish", which[0] + " " + which[1]);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", which[0]);
        cv.put("date", which[1]);
        cv.put("finished", "true");
        db.update(TABLE_NAME, cv, "title = ?", new String[]{title});
        db.close();
        Alarm.setAlarm(main);
    }

    //将本来完成的事项返回到未完成
    public void unfinishThis(String title) {
        String[] which = findInfo(title);
        Log.i("finish", which[0] + " " + which[1]);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", which[0]);
        cv.put("date", which[1]);
        cv.put("finished", "false");
        db.update(TABLE_NAME, cv, "title = ?", new String[]{title});
        db.close();
        Alarm.setAlarm(main);
    }

    //删
    public void delete(String title) {
        if (ifExists(title)) {
            Log.i("delete", "exist");
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_NAME, "title=?", new String[]{title});
            db.close();
            Alarm.setAlarm(main);
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "", null);
        db.close();
        Alarm.setAlarm(main);
    }

    //列出全部 注意返回的值是一个map，采用这个结构因为解析起来最方便
    //避免混淆，我就不重载了，直接改参数好了。0表示查询全部，1表示查询未完成，2表示查询已完成
    public Map<String, Vector<String>> queryAll(int i) {
        Map<String, Vector<String>> mp = new HashMap<>();
        Vector<String> titles = new Vector<>();
        Vector<String> descriptions = new Vector<>();
        Vector<String> dates = new Vector<>();
        SQLiteDatabase db = getReadableDatabase();
        String toQuery = "";
        if (i == ALL) toQuery = "select * from " + TABLE_NAME;
        else if (i == UNFINISHED) toQuery = "select * from " + TABLE_NAME+" where finished = 'false'";
        else if (i == FINISHED) toQuery = "select * from " + TABLE_NAME+" where finished = 'true'";
        Cursor cs = db.rawQuery(toQuery, null);
        int t = cs.getColumnIndex("title");
        int de = cs.getColumnIndex("description");
        int da = cs.getColumnIndex("date");
        if (cs.moveToFirst()) {
            do {
                titles.add(cs.getString(t));
                descriptions.add(cs.getString(de));
                dates.add(cs.getString(da));
            } while (cs.moveToNext());
        }
        mp.put("titles", titles);
        mp.put("descriptions", descriptions);
        mp.put("dates", dates);
        Log.i("MyDB.date", Integer.toString(dates.size()));
        db.close();
        return mp;
    }

    //查 注意因为默认是已经知道了title，所以返回的数组里有三元素，0详情1日期2是否完成
    public String[] findInfo(String title) {
        String[] ans = new String[3];
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("select * from " + TABLE_NAME + " where title = '" + title + "'", null);
        int de = cs.getColumnIndex("description");
        int da = cs.getColumnIndex("date");
        int f = cs.getColumnIndex("finished");
        if (cs.moveToFirst()) {
            ans[0] = cs.getString(de);
            ans[1] = cs.getString(da);
            ans[2] = cs.getString(f);
            Log.i("ans", ans[0] + " " + ans[1] + " " + ans[2]);
        }
        db.close();
        return ans;
    }

    //查询是否已经存在
    public boolean ifExists(String title) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cs = db.rawQuery("select * from " + TABLE_NAME + " where title = '" + title + "'", null);
        if (cs.getCount() != 0) return true;
        else return false;
    }
}
