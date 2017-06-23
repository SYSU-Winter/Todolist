package com.example.hp.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

public class SetDateActivity extends AppCompatActivity {
    private String date;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_date);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbarInSetDate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_save_in_set_date:
                        date = year + "年" + month + "月" + day + "日，";
                        if (hour < 10) {
                            date += "0";
                        }
                        date += hour + ":";
                        if (minute < 10) {
                            date += "0";
                        }
                        date += minute;
                        Intent intent = new Intent();
                        intent.putExtra("date", date);
                        setResult(0, intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        date = getIntent().getStringExtra("date");
        if (date.equals("未设置日期")) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        } else {
            String[] str = date.split("，");
            year = new Integer(str[0].split("年")[0]);
            month = new Integer(str[0].split("年")[1].split("月")[0]);
            day = new Integer(str[0].split("年")[1].split("月")[1].split("日")[0]);
            hour = new Integer(str[1].split(":")[0]);
            minute = new Integer(str[1].split(":")[1]);
        }

        datePicker.init(year, month - 1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SetDateActivity.this.year = year;
                month = monthOfYear + 1;
                day = dayOfMonth;
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                SetDateActivity.this.minute = minute;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_set_date, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
