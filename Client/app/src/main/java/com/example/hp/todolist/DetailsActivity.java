package com.example.hp.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.fastaccess.datetimepicker.DatePickerFragmentDialog;
import com.fastaccess.datetimepicker.DateTimeBuilder;
import com.fastaccess.datetimepicker.callback.DatePickerCallback;
import com.fastaccess.datetimepicker.callback.TimePickerCallback;

import java.util.Calendar;

import static com.example.hp.todolist.SampleHelper.getDateAndTime;
import static com.example.hp.todolist.SampleHelper.getTimeOnly;
import static com.example.hp.todolist.SampleHelper.getDateOnly;

public class DetailsActivity extends AppCompatActivity implements DatePickerCallback, TimePickerCallback{
    private MyDB myDB;
    private CheckBox finishCB;
    private Button dateBtn;
    private EditText titleET;
    private EditText descriptionET;
    private String originalTitle;
    private String newTitle;
    private String description;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new MyOnMenuItemClickListener());

        finishCB = (CheckBox) findViewById(R.id.finishCBinDetails);
        dateBtn = (Button) findViewById(R.id.dateBtn);
        titleET = (EditText) findViewById(R.id.titleET);
        descriptionET = (EditText) findViewById(R.id.descriptionET);

        myDB = new MyDB(this);

        // 用传入的title检索数据库，据此填充详情页信息
        Intent intent = getIntent();
        originalTitle = intent.getStringExtra("title");
        if (!originalTitle.equals("")) {
            newTitle = originalTitle;
            titleET.setText(originalTitle);
            String[] info = myDB.findInfo(originalTitle);
            description = info[0];
            date = info[1];
            if (description != null) descriptionET.setText(description);
            if (date != null) {
                dateBtn.setText(date);
                dateBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            if (info[2] != null && info[2].equals("true")) {
                finishCB.setChecked(true);
            }
        }

        finishCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                newTitle = titleET.getText().toString();
                if (newTitle.equals("")) {
                    Toast.makeText(DetailsActivity.this, R.string.empty_title, Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(!isChecked);
                }
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar minDate = Calendar.getInstance();
//                minDate.set(2016, minDate.get(Calendar.MONTH), minDate.get(Calendar.DAY_OF_MONTH));
//                Calendar maxDate = Calendar.getInstance();
//                maxDate.set(2016, minDate.get(Calendar.MONTH) + 1, minDate.get(Calendar.DAY_OF_MONTH));
//                Calendar currentDate = Calendar.getInstance();
//                currentDate.set(2016, currentDate.get(Calendar.MONTH) + 1, currentDate.get(Calendar.DAY_OF_MONTH));
                DatePickerFragmentDialog.newInstance(
                        DateTimeBuilder.get()
                                .withTime(true)
                                .with24Hours(true)
//                                .withSelectedDate(currentDate.getTimeInMillis())
//                                .withMinDate(minDate.getTimeInMillis())
//                                .withMaxDate(maxDate.getTimeInMillis())
                                .withCurrentHour(12)
                                .withCurrentMinute(30)
                                .withTheme(R.style.PickersTheme))
                        .show(getSupportFragmentManager(), "DatePickerFragmentDialog");
//                Intent intent1 = new Intent(DetailsActivity.this, SetDateActivity.class);
//                date = dateBtn.getText().toString();
//                intent1.putExtra("date", date);
//                startActivityForResult(intent1, 0);
            }
        });
    }

    @Override
    public void onDateSet(long m) {
        dateBtn.setText(String.valueOf(m));
    }

    /**
     * @param timeOnly
     *         time only
     * @param dateWithTime
     *         full date with time
     */
    @Override
    public void onTimeSet(long timeOnly, long dateWithTime) {
        dateBtn.setText(String.format("%s", getDateAndTime(dateWithTime)));
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (data != null) {
//            date = data.getStringExtra("date");
//        }
//        dateBtn.setText(date);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
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

    private class MyOnMenuItemClickListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_save_in_details:
                    date = dateBtn.getText().toString();
                    newTitle = titleET.getText().toString();
                    description = descriptionET.getText().toString();
                    if (newTitle.equals("")) {
                        Toast.makeText(DetailsActivity.this, R.string.empty_title, Toast.LENGTH_SHORT).show();
                    } else if (!originalTitle.equals(newTitle) && myDB.ifExists(newTitle)) {
                        Toast.makeText(DetailsActivity.this, R.string.same_title, Toast.LENGTH_SHORT).show();
                    } else {
                        if (originalTitle.equals("")) {
                            myDB.insert(newTitle, description, date);
                        } else {
                            myDB.update(originalTitle, newTitle, description, date);
                        }
                        if (finishCB.isChecked()) {
                            myDB.finishThis(newTitle);
                        } else {
                            myDB.unfinishThis(newTitle);
                        }
                        sendBroadcast(new Intent(NewAppWidget.ACTION_UPDATE_ALL));
                        finish();
                        overridePendingTransition(R.anim.fin_anim, R.anim.fout_anim);
                    }
                    break;
                case R.id.action_delete:
                    if (myDB.ifExists(originalTitle)) {
                        myDB.delete(originalTitle);
                    }
                    sendBroadcast(new Intent(NewAppWidget.ACTION_UPDATE_ALL));
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}
