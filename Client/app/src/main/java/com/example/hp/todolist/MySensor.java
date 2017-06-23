package com.example.hp.todolist;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lkxal on 2016/12/17.
 */

public class MySensor implements SensorEventListener {
    private static Toast toast;
    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 100;
    // 加速度变化阈值，当摇晃速度达到这值后产生作用
    private static final int SPEED_THRESHOLD = 800;

    private Context mContext;
    private SensorManager mSensorManager = null;
    private Vibrator mVibrator = null;


    public MySensor(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
//        showToast(context, "shaking");
    }


    private long lastUpdateTime;
    private float lastX;
    private float lastY;
    private float lastZ;

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();

        long timeInterval = currentUpdateTime - lastUpdateTime;

        if (timeInterval < UPTATE_INTERVAL_TIME) {
            return;
        }

        lastUpdateTime = currentUpdateTime;
        float[] values = event.values;

        // 获得x,y,z加速度
        float x = values[0];
        float y = values[1];
        float z = values[2];

        // 获得x,y,z加速度的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;


//        Log.i("0", "values[0] = " + values[0]);
//        Log.i("1", "values[1] = " + values[1]);
//        Log.i("2", "values[2] = " + values[2]);

        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
//        Log.i("speed", "speed:" + speed);
        if (speed > SPEED_THRESHOLD) {
            //在这里可以提供一个回调
            mVibrator.vibrate(300);
            sendIssues();
        }
    }
    public void sendIssues() {
        Intent i = new Intent(Intent.ACTION_SEND);
        // i.setType("text/plain"); //模拟器请使用这行
        i.setType("message/rfc822"); // 真机上使用这行
        i.putExtra(Intent.EXTRA_EMAIL,
                new String[] { "dingq8@mail2.sysu.edu.cn" });
        i.putExtra(Intent.EXTRA_SUBJECT, "对本APP的意见或建议");
        i.putExtra(Intent.EXTRA_TEXT, "\n以上为我对这款产品的意见和建议");
        mContext.startActivity(Intent.createChooser(i,
                "请选择一个邮箱应用如qq邮箱等"));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void registerSensor() {
        Sensor sensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (null != sensor)
            mSensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterSensor() {

        lastX = 0;
        lastY = 0;
        lastZ = 0;
        mSensorManager.unregisterListener(this);
    }
}
