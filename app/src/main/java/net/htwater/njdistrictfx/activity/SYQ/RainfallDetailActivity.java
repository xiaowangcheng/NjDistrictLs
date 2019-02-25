package net.htwater.njdistrictfx.activity.SYQ;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RainfallDetailActivity extends BaseActivity {

    private String stcd;
    private BarChart barChart;
    private BarData barData;
    private ArrayList<String> xValues = new ArrayList<>();
    private ArrayList<BarEntry> yValues = new ArrayList<>();

    private String nowStartDateTime;
    private String nowEndDateTime;

    private TextView tvStartDateTime;
    private TextView tvEndDateTime;
    private TextView tvDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rainfall_detail);

        RelativeLayout rlBack = findViewById(R.id.leftButton);
        TextView tvTitle = findViewById(R.id.title);
        tvStartDateTime = findViewById(R.id.tv_start_date_time);
        tvEndDateTime = findViewById(R.id.tv_end_date_time);
        LinearLayout llDateTime = findViewById(R.id.ll_date_time);
        barChart = findViewById(R.id.bar_char);
        tvDetail = findViewById(R.id.tv_detail);

        barChart.setFocusable(true);
        barChart.setFocusableInTouchMode(true);
        barChart.requestFocus();

        Intent intent = getIntent();
        stcd = intent.getStringExtra("stcd");
        tvTitle.setText(intent.getStringExtra("stnm") + "雨量过程");


        nowStartDateTime = DateUtil.parseDate2String(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), DateUtil.DATETIME_FORMAT4);
        nowEndDateTime = DateUtil.parseDate2String(new Date(System.currentTimeMillis()), DateUtil.DATETIME_FORMAT4);

        tvStartDateTime.setText(nowStartDateTime);
        tvEndDateTime.setText(nowEndDateTime);

        llDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog();
            }
        });

        apiRainchart();

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void apiRainchart() {
        String result ="[{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 10\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 11\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 12\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 13\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 14\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 15\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 16\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 17\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 18\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 19\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 20\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 21\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 22\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-10 23\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 00\"},{\"VALUE\":\"0.5\",\"TM\":\"2019-01-11 01\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 02\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 03\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 04\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 05\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 06\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 07\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 08\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 09\"},{\"VALUE\":\"0.0\",\"TM\":\"2019-01-11 10\"}]";

        try {
            JSONArray resultJsonArray = new JSONArray(result);
            xValues.clear();
            yValues.clear();

            for (int i = 0; i < resultJsonArray.length(); i++) {
                JSONObject jsonObject = resultJsonArray.getJSONObject(i);
                String val = jsonObject.optString("TM");
                float yValue=(float) jsonObject.optDouble("VALUE");
                xValues.add(val);
                yValues.add(new BarEntry(i,yValue, val));
            }

            BarDataSet barDataSet = new BarDataSet(yValues, "雨量(m)");
            barDataSet.setColor(Color.rgb(143, 221, 143));
            barDataSet.setBarShadowColor(Color.TRANSPARENT);

            barData = new BarData(barDataSet);
            initBarChart();
            barChart.setData(barData);
            barChart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }


       /* String url = Constants.ServerURL + "apiRainchart";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("tm1", nowStartDateTime + ":00");
        params.addBodyParameter("tm2", nowEndDateTime + ":00");
        params.addBodyParameter("stcd", stcd);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray resultJsonArray = new JSONArray(result);
                    xValues.clear();
                    yValues.clear();

                    for (int i = 0; i < resultJsonArray.length(); i++) {
                        JSONObject jsonObject = resultJsonArray.getJSONObject(i);
                        String val = jsonObject.optString("TM");
                        float yValue=(float) jsonObject.optDouble("VALUE");
                        xValues.add(val);
                        yValues.add(new BarEntry(i,yValue, val));
                    }

                    BarDataSet barDataSet = new BarDataSet(yValues, "雨量(m)");
                    barDataSet.setColor(Color.rgb(143, 221, 143));
                    barDataSet.setBarShadowColor(Color.TRANSPARENT);

                    barData = new BarData(barDataSet);
                    initBarChart();
                    barChart.setData(barData);
                    barChart.invalidate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });*/
    }


    /**
     * 设置图表属性
     */
    private void initBarChart() {
        barChart.setDescription(null);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        barChart.setTouchEnabled(true);
        barChart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setPinchZoom(true);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight h) {
                tvDetail.setText("时间:" + xValues.get((int) entry.getX()) + "      水位:" + entry.getY() + "m");

            }

            @Override
            public void onNothingSelected() {

            }
        });

        XAxis xAxis=barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(3);

        YAxis rightYAxis = barChart.getAxisRight();
        YAxis leftYAxis = barChart.getAxisLeft();

        rightYAxis.setEnabled(false);
    }

    @SuppressLint("NewApi")
    private void showDateTimeDialog() {
        final View view = View.inflate(this, R.layout.dialog_datetime_s2e_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final DatePicker datePicker = view.findViewById(R.id.datePicker);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);
        final DatePicker datePicker_end = view.findViewById(R.id.datePicker_end);
        final TimePicker timePicker_end = view.findViewById(R.id.timePicker_end);
        resizePikcer(datePicker);
        resizePikcer(datePicker_end);
        resizePikcerTime(timePicker);
        resizePikcerTime(timePicker_end);

        datePicker.init(Integer.valueOf(nowStartDateTime.substring(0, 4)), Integer.valueOf(nowStartDateTime.substring(5, 7)) - 1, Integer.valueOf(nowStartDateTime.substring(8, 10)), null);
        datePicker_end.init(Integer.valueOf(nowEndDateTime.substring(0, 4)), Integer.valueOf(nowEndDateTime.substring(5, 7)) - 1, Integer.valueOf(nowEndDateTime.substring(8, 10)), null);
        timePicker.setIs24HourView(true);
        timePicker_end.setIs24HourView(true);
        timePicker.setHour(Integer.valueOf(nowStartDateTime.substring(11, 13)));
        timePicker.setMinute(Integer.valueOf(nowStartDateTime.substring(14, 16)));
        timePicker_end.setHour(Integer.valueOf(nowEndDateTime.substring(11, 13)));
        timePicker_end.setMinute(Integer.valueOf(nowEndDateTime.substring(14, 16)));

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 开始时间
                int year;
                int month;
                int day;
                year = datePicker.getYear();
                month = datePicker.getMonth() + 1;
                day = datePicker.getDayOfMonth();
                String monthStr;
                String dayStr;
                if (month < 10) {
                    monthStr = "0" + month;
                } else {
                    monthStr = "" + month;
                }
                if (day < 10) {
                    dayStr = "0" + day;
                } else {
                    dayStr = "" + day;
                }

                int hour;
                int min;
                hour = timePicker.getHour();
                min = timePicker.getMinute();
                String hourStr;
                String minStr;
                if (hour < 10) {
                    hourStr = "0" + hour;
                } else {
                    hourStr = "" + hour;
                }
                if (min < 10) {
                    minStr = "0" + min;
                } else {
                    minStr = "" + min;
                }

                nowStartDateTime = year + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minStr;
                tvStartDateTime.setText(nowStartDateTime);

                // 截止时间
                year = datePicker_end.getYear();
                month = datePicker_end.getMonth() + 1;
                day = datePicker_end.getDayOfMonth();
                if (month < 10) {
                    monthStr = "0" + month;
                } else {
                    monthStr = "" + month;
                }
                if (day < 10) {
                    dayStr = "0" + day;
                } else {
                    dayStr = "" + day;
                }
                hour = timePicker_end.getHour();
                min = timePicker_end.getMinute();
                if (hour < 10) {
                    hourStr = "0" + hour;
                } else {
                    hourStr = "" + hour;
                }
                if (min < 10) {
                    minStr = "0" + min;
                } else {
                    minStr = "" + min;
                }

                nowEndDateTime = year + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minStr;
                tvEndDateTime.setText(nowEndDateTime);

                apiRainchart();

            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    /**
     * 调整numberpicker大小
     */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtil.dip2px(this, 48), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 0, 5, 0);
        np.setLayoutParams(params);
    }

    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcerTime(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPickerTime(np);
        }
    }

    /**
     * 调整numberpicker大小
     */
    private void resizeNumberPickerTime(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtil.dip2px(this, 30), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        np.setLayoutParams(params);
    }
}
