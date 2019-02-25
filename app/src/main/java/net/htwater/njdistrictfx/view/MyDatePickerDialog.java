package net.htwater.njdistrictfx.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

/**
 * Created by LZY on 2017/5/31.
 */

public class MyDatePickerDialog extends DatePickerDialog {

//    public MyDatePickerDialog(@NonNull Context context) {
//        super(context);
//    }

//    public MyDatePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
//        super(context, themeResId);
//    }

    public MyDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    public MyDatePickerDialog(@NonNull Context context, @StyleRes int themeResId, @Nullable OnDateSetListener listener, int year, int monthOfYear,
                              int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
    }

    /**
     * @param context
     * @param listener
     * @param date     格式：2017-05-31
     */
    public MyDatePickerDialog(@NonNull Context context, String date, @Nullable OnDateSetListener listener) {
        // String[] dateArray = date.split("-");
        super(context, listener, Integer.valueOf(date.split("-")[0]), Integer.valueOf(date.split("-")[1]) - 1, Integer.valueOf(date.split("-")[2]));
    }

    /**
     *
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @return 格式：2017-05-31
     */
    public static String transformFormat(int year, int monthOfYear, int dayOfMonth) {
        String month;
        if (monthOfYear < 9) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = "" + (monthOfYear + 1);
        }
        String day;
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = "" + dayOfMonth;
        }
        return year + "-" + month + "-" + day;
    }
}
