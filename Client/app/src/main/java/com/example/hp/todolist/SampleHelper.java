package com.example.hp.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 12136 on 2017/6/24.
 */

public class SampleHelper {

    public static final String DATE_PICKER_FRAGMENT_DIALOG = "DatePickerFragmentDialog";
    public static final String TIME_PICKER_FRAGMENT_DIALOG = "TimePickerFragmentDialog";


    public static String getDateOnly(long time) {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(time);
    }

    //dd MMM yyyy, hh:mm
    public static String getDateAndTime(long time) {
        SimpleDateFormat sample = new SimpleDateFormat("yyyy年MM月dd日,HH:mm", Locale.getDefault());
        return sample.format(new Date(time));
    }

    public static String getTimeOnly(long time) {
        SimpleDateFormat sample = new SimpleDateFormat("hh:mma", Locale.getDefault());
        return sample.format(time);
    }
}