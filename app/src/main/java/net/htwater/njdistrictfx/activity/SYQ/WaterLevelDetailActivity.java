package net.htwater.njdistrictfx.activity.SYQ;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class WaterLevelDetailActivity extends BaseActivity {

    private String stcd;

    private String nowStartDate;
    private String nowEndDate;

    private TextView tvStartDate;
    private TextView tvEndDate;
    private TextView tvDetail;
    private LineChart lineChart;
    private ListView lvWaterLevel;

    private ArrayList<String> xVals = new ArrayList<>();
    private ArrayList<Entry> yVals = new ArrayList<>();

    private String wrz;

    private float max;
    private float min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_level);

        RelativeLayout rlBack = findViewById(R.id.leftButton);
        TextView tvTitle = findViewById(R.id.title);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        LinearLayout llDate = findViewById(R.id.ll_date);
        lineChart = findViewById(R.id.line_char);
        tvDetail = findViewById(R.id.tv_detail);
//        lvWaterLevel=findViewById(R.id.lv_water_level);

        Intent intent = getIntent();
        stcd = intent.getStringExtra("stcd");
        tvTitle.setText(intent.getStringExtra("stnm") + "水位过程");
        wrz = intent.getStringExtra("wrz");

        nowStartDate = DateUtil.parseDate2String(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), DateUtil.DATE_FORMAT2);
        nowEndDate = DateUtil.parseDate2String(new Date(System.currentTimeMillis()), DateUtil.DATE_FORMAT2);

        tvStartDate.setText(nowStartDate);
        tvEndDate.setText(nowEndDate);

        apiWaterline();

        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void showDateDialog() {
        final View view = View.inflate(this, R.layout.dialog_date_s2e_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final DatePicker datePicker = view.findViewById(R.id.datePicker);
        final DatePicker datePicker_end = view.findViewById(R.id.datePicker_end);
//        resizePikcer(datePicker);
//        resizePikcer(datePicker_end);
//        resizePikcerTime(timePicker);
//        resizePikcerTime(timePicker_end);

        datePicker.init(Integer.valueOf(nowStartDate.substring(0, 4)), Integer.valueOf(nowStartDate.substring(5, 7)) - 1, Integer.valueOf(nowStartDate.substring(8, 10)), null);
        datePicker_end.init(Integer.valueOf(nowEndDate.substring(0, 4)), Integer.valueOf(nowEndDate.substring(5, 7)) - 1, Integer.valueOf(nowEndDate.substring(8, 10)), null);

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
                nowStartDate = year + "-" + monthStr + "-" + dayStr;
                tvStartDate.setText(nowStartDate);

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
                nowEndDate = year + "-" + monthStr + "-" + dayStr;
                tvEndDate.setText(nowEndDate);

                apiWaterline();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void apiWaterline() {
        String result ="[{\"VALUE\":\"6.96\",\"TM\":\"2019-01-10 08:00:00\"},{\"VALUE\":\"6.97\",\"TM\":\"2019-01-10 09:00:00\"},{\"VALUE\":\"7.0\",\"TM\":\"2019-01-10 10:00:00\"},{\"VALUE\":\"6.99\",\"TM\":\"2019-01-10 11:00:00\"},{\"VALUE\":\"7.01\",\"TM\":\"2019-01-10 12:00:00\"},{\"VALUE\":\"7.01\",\"TM\":\"2019-01-10 13:00:00\"},{\"VALUE\":\"7.0\",\"TM\":\"2019-01-10 14:00:00\"},{\"VALUE\":\"6.99\",\"TM\":\"2019-01-10 15:00:00\"},{\"VALUE\":\"7.04\",\"TM\":\"2019-01-10 16:00:00\"},{\"VALUE\":\"7.03\",\"TM\":\"2019-01-10 17:00:00\"},{\"VALUE\":\"6.96\",\"TM\":\"2019-01-10 18:00:00\"},{\"VALUE\":\"6.92\",\"TM\":\"2019-01-10 19:00:00\"},{\"VALUE\":\"6.92\",\"TM\":\"2019-01-10 20:00:00\"},{\"VALUE\":\"6.91\",\"TM\":\"2019-01-10 21:00:00\"},{\"VALUE\":\"6.91\",\"TM\":\"2019-01-10 22:00:00\"},{\"VALUE\":\"6.91\",\"TM\":\"2019-01-10 23:00:00\"},{\"VALUE\":\"6.89\",\"TM\":\"2019-01-11 00:00:00\"},{\"VALUE\":\"6.87\",\"TM\":\"2019-01-11 01:00:00\"},{\"VALUE\":\"6.85\",\"TM\":\"2019-01-11 02:00:00\"},{\"VALUE\":\"6.84\",\"TM\":\"2019-01-11 03:00:00\"},{\"VALUE\":\"6.82\",\"TM\":\"2019-01-11 04:00:00\"},{\"VALUE\":\"6.81\",\"TM\":\"2019-01-11 05:00:00\"},{\"VALUE\":\"6.79\",\"TM\":\"2019-01-11 06:00:00\"},{\"VALUE\":\"6.83\",\"TM\":\"2019-01-11 07:00:00\"},{\"VALUE\":\"6.86\",\"TM\":\"2019-01-11 08:00:00\"}]";

        try {
            JSONArray resultJsonArray = new JSONArray(result);
            xVals = new ArrayList<>();
            yVals = new ArrayList<>();
            max = (float) resultJsonArray.getJSONObject(0).optDouble("VALUE");
            min = (float) resultJsonArray.getJSONObject(0).optDouble("VALUE");

            for (int i = 0; i < resultJsonArray.length(); i++) {
                JSONObject jsonObject = resultJsonArray.getJSONObject(i);
                String val = jsonObject.optString("TM");
                float yValue=(float) jsonObject.optDouble("VALUE");
                xVals.add(val);
                yVals.add(new Entry(i, yValue, val));
                if (yValue>max){
                    max=yValue;
                }
                if (yValue<min){
                    min=yValue;
                }
            }

            LineDataSet dataSet = new LineDataSet(yVals, "水位(m)");
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(2);
            dataSet.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    DecimalFormat df = new DecimalFormat(".##");
                    return df.format(value);
                }
            });

            LineData lineData = new LineData(dataSet);
            initLineChart();
            lineChart.setData(lineData);
            lineChart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*String url = Constants.ServerURL + "apiWaterline";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("tm1", nowStartDate);
        params.addBodyParameter("tm2", nowEndDate);
        params.addBodyParameter("stcd", stcd);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray resultJsonArray = new JSONArray(result);
                    xVals = new ArrayList<>();
                    yVals = new ArrayList<>();
                    max = (float) resultJsonArray.getJSONObject(0).optDouble("VALUE");
                    min = (float) resultJsonArray.getJSONObject(0).optDouble("VALUE");

                    for (int i = 0; i < resultJsonArray.length(); i++) {
                        JSONObject jsonObject = resultJsonArray.getJSONObject(i);
                        String val = jsonObject.optString("TM");
                        float yValue=(float) jsonObject.optDouble("VALUE");
                        xVals.add(val);
                        yVals.add(new Entry(i, yValue, val));
                        if (yValue>max){
                            max=yValue;
                        }
                        if (yValue<min){
                            min=yValue;
                        }
                    }

                    LineDataSet dataSet = new LineDataSet(yVals, "水位(m)");
                    dataSet.setDrawCircles(false);
                    dataSet.setLineWidth(2);
                    dataSet.setValueFormatter(new IValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                            DecimalFormat df = new DecimalFormat(".##");
                            return df.format(value);
                        }
                    });

                    LineData lineData = new LineData(dataSet);
                    initLineChart();
                    lineChart.setData(lineData);
                    lineChart.invalidate();

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

    private void initLineChart() {
        if (Float.parseFloat(wrz)>max){
            max=Float.parseFloat(wrz);
        }
        if (Float.parseFloat(wrz)<min){
            min=Float.parseFloat(wrz);
        }

        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(null);
        lineChart.setDrawBorders(false);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight h) {
                tvDetail.setText("时间:" + xVals.get((int) entry.getX()) + "      水位:" + entry.getY() + "m");
            }

            @Override
            public void onNothingSelected() {
                tvDetail.setText("");
            }
        });

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        xAxis.setLabelCount(3);

        YAxis rightYAxis = lineChart.getAxisRight();
        YAxis leftYAxis = lineChart.getAxisLeft();

        max=max*1.1f;
        min=min*0.9f;
        rightYAxis.setEnabled(false);
        leftYAxis.setAxisMaximum(max);
        leftYAxis.setAxisMinimum(min);

        leftYAxis.removeAllLimitLines();
        LimitLine limitLine = new LimitLine(Float.parseFloat(wrz), "水位警戒线" + wrz+"m"); //得到限制线
        limitLine.setLineWidth(1f); //宽度
        limitLine.setTextSize(10f);
        limitLine.setTextColor(Color.RED);  //颜色
        limitLine.setLineColor(Color.RED);
        leftYAxis.addLimitLine(limitLine); //Y轴添加限制线
    }
}
