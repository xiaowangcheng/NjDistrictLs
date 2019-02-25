package net.htwater.njdistrictfx.widget;

import android.content.Context;

import com.github.mikephil.charting.charts.BarChart;

public class WaterWidget extends Widget {
    private final Context context;
    private BarChart chart;

    public WaterWidget(Context context) {
        super(context, "water");
        this.context = context;

//        initView();
    }

//    private void initView() {
//        contentView = View.inflate(context, R.layout.layout_widget_water, null);
//        chart = (BarChart) contentView.findViewById(R.id.chart);
//
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//        contentView.setLayoutParams(params);
//
//        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry entry, int i) {
//                Intent intent = null;
//                switch (entry.getXIndex()) {
//                    case 0:
//                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RES_WATER;
//                        intent = new Intent(context, SyqActivity.class);
//                        intent.putExtra("iniType", "中型水库");
//                        break;
//                    case 1:
//                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RES_WATER;
//                        intent = new Intent(context, SyqActivity.class);
//                        intent.putExtra("iniType", "小一型");
//                        break;
//                    case 2:
//                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RES_WATER;
//                        intent = new Intent(context, SyqActivity.class);
//                        intent.putExtra("iniType", "小二型");
//                        break;
//                    case 3:
//                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_RIVER_WATER;
//                        intent = new Intent(context, SyqActivity.class);
//                        break;
//                    case 4:
//                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_FLOOD;
//                        intent = new Intent(context, SyqActivity.class);
//                        break;
//                    case 5:
//                        MyApplication.CONSTANS_WATER_TYPE = Constants.CONSTANS_PUMP_WATER;
//                        intent = new Intent(context, SyqActivity.class);
//                        break;
//                }
//                context.startActivity(intent);
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
//
//        setBarChart();
//
//        request();
//    }

//    private void setBarChart() {
//        chart.setDescription("");
//        chart.setDrawGridBackground(false);
//        chart.setDrawBarShadow(false);
//        chart.setDrawVerticalGrid(false);
//        chart.setDrawHorizontalGrid(false);
//        chart.setTouchEnabled(true);
//        chart.setDrawYLabels(false);
//        chart.setNoDataText("");
//        chart.setNoDataTextDescription("");
//        // 动画效果
//        ValueFormatter valueFormatter = new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                int index = String.valueOf(value).indexOf(".");
//                return String.valueOf(value).substring(0, index);
//            }
//        };
//        chart.setValueFormatter(valueFormatter);
//        chart.animateY(2500);
//
//        XLabels xLabels = chart.getXLabels();
//        xLabels.setPosition(XLabelPosition.BOTTOM);
//        xLabels.setCenterXLabelText(true);
//    }
//
//    private void request() {
//        String url = Constants.ServerIP + "/njfx/getWaterSta!SYQ";
//        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("TAG", response);
//                if (null == response || response.length() <= 0) {
//                    return;
//                }
//                try {
//                    JSONObject object = new JSONObject(response);
//                    handleData(object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                volleyError.printStackTrace();
//            }
//        });
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
//        MyApplication.mQueue.add(stringRequest);
//    }

//    private void handleData(JSONObject object) {
//        try {
//            String bigmid2 = object.getString("bigmid2");
//            String[] array1 = bigmid2.replace("[", "").replace("]", "").split(",");
//            StackedBarBean entity1 = new StackedBarBean("中型水库",
//                    Integer.valueOf(array1[1]), Integer.valueOf(array1[0]));
//
//            String s2_1 = object.getString("s2_1");
//            String[] array2 = s2_1.replace("[", "").replace("]", "").split(",");
//            StackedBarBean entity2 = new StackedBarBean("小一型",
//                    Integer.valueOf(array2[1]), Integer.valueOf(array2[0]));
//
//            String s2_2 = object.getString("s2_2");
//            String[] array3 = s2_2.replace("[", "").replace("]", "").split(",");
//            StackedBarBean entity3 = new StackedBarBean("小二型",
//                    Integer.valueOf(array3[1]), Integer.valueOf(array3[0]));
//
//            String river2 = object.getString("river2");
//            String[] array4 = river2.replace("[", "").replace("]", "").split(",");
//            StackedBarBean entity4 = new StackedBarBean("河道",
//                    Integer.valueOf(array4[1]), Integer.valueOf(array4[0]));
//
//            String flood2 = object.getString("flood2");
//            String[] array5 = flood2.replace("[", "").replace("]", "").split(",");
//            StackedBarBean entity5 = new StackedBarBean("积淹点",
//                    Integer.valueOf(array5[1]), Integer.valueOf(array5[0]));
//
//            StackedBarBean entity6 = null;
//            if (object.has("pump2")) {
//                String pump2 = object.getString("pump2");
//                String[] array6 = pump2.replace("[", "").replace("]", "").split(",");
//                entity6 = new StackedBarBean("泵站", Integer.valueOf(array6[1]), Integer.valueOf(array6[0]));
//            }
//
//            StackedBarBean[] array = new StackedBarBean[6];
//            array[0] = entity1;
//            array[1] = entity2;
//            array[2] = entity3;
//            array[3] = entity4;
//            array[4] = entity5;
//            if (null != entity6) {
//                array[5] = entity6;
//            }
//
//            chart.setData(getBarData(array));
//            chart.invalidate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private BarData getBarData(StackedBarBean[] entityArray) throws Exception {
//        ArrayList<String> xVals = new ArrayList<>();
//        ArrayList<BarEntry> yVals = new ArrayList<>();
//        for (int i = 0; i < entityArray.length; i++) {
//            try {
//                StackedBarBean entity = entityArray[i];
//                if (null == entity) {
//                    continue;
//                }
//                String xval = entity.getXval();
//                xVals.add(xval);
//
//                float normal_num = entity.getNormalnum();
//                float guard_num = entity.getGuardnum();
//                yVals.add(new BarEntry(new float[]{normal_num, guard_num}, i));
//            } catch (NumberFormatException e) {
//                throw new Exception(e);
//            }
//        }
//        BarDataSet dataSet = new BarDataSet(yVals, "");
//        dataSet.setColors(new int[]{Color.parseColor("#00c10e"), Color.RED});
//        dataSet.setStackLabels(new String[]{"总数", "超警戒"});
//        dataSet.setBarSpacePercent(50);
//        return new BarData(xVals, dataSet);
//    }

//    @Override
//    protected void refresh() {
//        request();
//    }
}
