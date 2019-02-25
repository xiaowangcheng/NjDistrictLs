package net.htwater.njdistrictfx.fragment.ListAndMap;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.SYQ.RainfallDetailActivity;
import net.htwater.njdistrictfx.activity.SYQ.SyqActivity;
import net.htwater.njdistrictfx.activity.SYQ.WaterDetailActivity;
import net.htwater.njdistrictfx.activity.SYQ.WaterLevelDetailActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.core.NewComparator;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by lzy on 2017/3/31
 */
public class SyqListFragment extends Fragment {
    private MyAdapter adapter;
    private SyqActivity activity;
    private TextView tv2, arrow1, arrow2, arrow3, arrow4;
    private LinearLayout layout1;
    private boolean isFirst = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_syq_list, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        FrameLayout topBar = (FrameLayout) view.findViewById(R.id.topBar);
        TextView favoriteTv = (TextView) view.findViewById(R.id.favoriteTv);
        TextView firstTv = (TextView) view.findViewById(R.id.firstTv);

        activity = (SyqActivity) getActivity();

        int id = 0;
        switch (MyApplication.CONSTANS_WATER_TYPE) {
            case Constants.CONSTANS_RIVER_WATER:
                id = R.layout.topbar_river_water;
                break;
            case Constants.CONSTANS_TIDE:
                id = R.layout.topbar_tide;
                break;
            case Constants.CONSTANS_RES_WATER:
                id = R.layout.topbar_res_water;
                break;
            case Constants.CONSTANS_GATE_WATER:
                id = R.layout.topbar_gate_water;
                break;
            case Constants.CONSTANS_PUMP_WATER:
                id = R.layout.topbar_pump_water;
                break;
            case Constants.CONSTANS_FLOOD:
                id = R.layout.topbar_flood;
                favoriteTv.setVisibility(View.GONE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) firstTv.getLayoutParams();
                layoutParams.width = (int) (MyApplication.density * 160);
                break;
            case Constants.CONSTANS_RAIN:
                id = R.layout.topbar_rain;
                break;
        }
        View topBarView = View.inflate(getActivity(), id, null);
        topBar.addView(topBarView);
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            layout1 = (LinearLayout) view.findViewById(R.id.layout1);
            LinearLayout layout2 = (LinearLayout) view.findViewById(R.id.layout2);
            LinearLayout layout3 = (LinearLayout) view.findViewById(R.id.layout3);
            LinearLayout layout4 = (LinearLayout) view.findViewById(R.id.layout4);
            arrow1 = (TextView) view.findViewById(R.id.arrow1);
            arrow2 = (TextView) view.findViewById(R.id.arrow2);
            arrow3 = (TextView) view.findViewById(R.id.arrow3);
            arrow4 = (TextView) view.findViewById(R.id.arrow4);

//            layout1.setOnClickListener(clickListener);
//            layout2.setOnClickListener(clickListener);
//            layout3.setOnClickListener(clickListener);
//            layout4.setOnClickListener(clickListener);
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
            tv2 = (TextView) view.findViewById(R.id.tv2);
//            tv2.setOnClickListener(clickListener2);
        }

        adapter = new MyAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
                    return;
                }
                if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
                    JSONObject jsonObject = (JSONObject) adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), RainfallDetailActivity.class);
                    try {
                        intent.putExtra("stcd", jsonObject.getString("STCD"));
                        intent.putExtra("stnm", jsonObject.getString("STNM"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER){
                    JSONObject jsonObject = (JSONObject) adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), WaterLevelDetailActivity.class);
                    try {
                        intent.putExtra("stcd", jsonObject.getString("STCD"));
                        intent.putExtra("stnm", jsonObject.getString("STNM"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    //sttp：过程性类型（河道RV、潮位TI、水库RS、水闸SL、泵站PU）
                    String sttp = null;
                    switch (MyApplication.CONSTANS_WATER_TYPE) {
                        case Constants.CONSTANS_RIVER_WATER:
                            sttp = "RV";
                            break;
                        case Constants.CONSTANS_TIDE:
                            sttp = "TI";
                            break;
                        case Constants.CONSTANS_RES_WATER:
                            sttp = "RS";
                            break;
                        case Constants.CONSTANS_GATE_WATER:
                            sttp = "SL";
                            break;
                        case Constants.CONSTANS_PUMP_WATER:
                            sttp = "PU";
                            break;
                    }
                    try {
                        JSONObject jsonObject = (JSONObject) adapter.getItem(position);
                        Intent intent = new Intent(getActivity(), WaterDetailActivity.class);
                        intent.putExtra("stnm", jsonObject.getString("STNM"));
                        intent.putExtra("stcd", jsonObject.getString("STCD"));
                        intent.putExtra("sttp", sttp);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(JSONArray jsonArray) {
        if (jsonArray.length() == 0) {
            adapter.setData(jsonArray);
            Toast.makeText(getActivity().getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER
                || MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RIVER_WATER
                || MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            adapter.setData(handleData(jsonArray));
        } else {
            adapter.setData(jsonArray);
        }
        Toast.makeText(getActivity().getApplicationContext(), "共有" + jsonArray.length() + "条记录", Toast.LENGTH_SHORT).show();

        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            arrow1.setText("");
            arrow2.setText("");
            arrow3.setText("");
            arrow4.setText("");
        }

        if (!isFirst) {
            return;
        }
        isFirst = !isFirst;
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RAIN) {
            layout1.performClick();
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_FLOOD) {
            tv2.performClick();
        }
    }

    /**
     * 把超警戒的站点排到最前面
     *
     * @param jsonArray
     * @return
     */
    private JSONArray handleData(JSONArray jsonArray) {
        String alertKey, zKey;
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_RES_WATER) {
            alertKey = "CTZ";
        } else if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            alertKey = "ZUIGAOSHUIWEI";
        } else {
            alertKey = "WRZ";
        }
        if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
            zKey = "PPDWZ";
        } else {
            zKey = "Z";
        }
        List<JSONObject> dataList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Object object = jsonObject.get(alertKey);
                Object zObject = jsonObject.get(zKey);
                if (object.toString().equals("—") || zObject.toString().equals("—")) {
                    dataList.add(jsonObject);
                    continue;
                }
                if (MyApplication.CONSTANS_WATER_TYPE == Constants.CONSTANS_PUMP_WATER) {
                    String zString = jsonObject.getString(zKey);
                    String ctzString = jsonObject.getString(alertKey);
                    if (zString.isEmpty() || ctzString.isEmpty()) {
                        dataList.add(jsonObject);
                        continue;
                    }
                    double Z = Double.valueOf(zString);
                    double CTZ = Double.valueOf(ctzString);
                    if (Z > CTZ) {
                        jsonObject.put("alert", true);
                        dataList.add(0, jsonObject);
                    } else {
                        dataList.add(jsonObject);
                    }
                } else {
                    double Z = jsonObject.getDouble(zKey);
                    double CTZ = jsonObject.getDouble(alertKey);
                    if (Z > CTZ) {
                        jsonObject.put("alert", true);
                        dataList.add(0, jsonObject);
                    } else {
                        dataList.add(jsonObject);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray1 = new JSONArray();
        for (JSONObject jsonObject : dataList) {
            jsonArray1.put(jsonObject);
        }
        return jsonArray1;
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // restoreDefaultText();

            int index = 0;
            TextView arrow = null;
            if (v.getId() == R.id.layout1) {
                index = 0;
                arrow = arrow1;

                arrow2.setText("");
                arrow3.setText("");
                arrow4.setText("");
            } else if (v.getId() == R.id.layout2) {
                index = 1;
                arrow = arrow2;

                arrow1.setText("");
                arrow3.setText("");
                arrow4.setText("");
            } else if (v.getId() == R.id.layout3) {
                index = 2;
                arrow = arrow3;

                arrow1.setText("");
                arrow2.setText("");
                arrow4.setText("");
            } else if (v.getId() == R.id.layout4) {
                index = 3;
                arrow = arrow4;

                arrow1.setText("");
                arrow2.setText("");
                arrow3.setText("");
            }
            if (arrow.getText().toString().contains("↓")) {
                // 升序排列
                arrow.setText("↑");
                Collections.sort(adapter.getData(), new NewComparator(1, index));
            } else {
                // 降序排列
                arrow.setText("↓");
                Collections.sort(adapter.getData(), new NewComparator(0, index));
            }
            adapter.dataChanged();
        }
    };

    private final View.OnClickListener clickListener2 = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            String content = tv.getText().toString();

            tv2.setText("深度(cm)");

            if (content.contains("↓")) {
                // 升序排列
                tv.setText(content.substring(0, 6) + "↑");
                Collections.sort(adapter.getData(), new MyComparator(1, "VALUE"));
            } else {
                // 降序排列
                tv.setText(content.substring(0, 6) + "↓");
                Collections.sort(adapter.getData(), new MyComparator(0, "VALUE"));
            }
            adapter.dataChanged();
        }
    };

    class MyAdapter extends BaseAdapter {
        private final Context context;
        private JSONArray jsonArray = new JSONArray();
        private List<JSONObject> list = new ArrayList<>();
        private int black = getResources().getColor(R.color.text_black);

        public MyAdapter(Context context) {
            super();
            this.context = context;
        }

        public void setData(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            notifyDataSetChanged();
        }

        public List<JSONObject> getData() {
            list.clear();
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        private void dataChanged() {
            jsonArray = new JSONArray();
            for (JSONObject jsonObject : list) {
                jsonArray.put(jsonObject);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (null == convertView) {
                holder = new Holder();
                convertView = View.inflate(context, R.layout.listitem_syq, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);
                holder.tv6 = (TextView) convertView.findViewById(R.id.tv6);
                holder.tv7 = (TextView) convertView.findViewById(R.id.tv7);
                holder.favorite = (RelativeLayout) convertView.findViewById(R.id.favorite);
                holder.favoriteIcon = (ImageView) convertView.findViewById(R.id.favoriteIcon);

                switch (MyApplication.CONSTANS_WATER_TYPE) {
                    case Constants.CONSTANS_TIDE:
                        holder.tv5.setVisibility(View.GONE);
                        holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_GATE_WATER:
                        holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_PUMP_WATER:
                        holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_FLOOD:
                        holder.tv4.setVisibility(View.GONE);
                        holder.tv5.setVisibility(View.GONE);
                        holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        holder.favorite.setVisibility(View.GONE);

                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.tv1.getLayoutParams();
                        layoutParams.width = (int) (MyApplication.density * 160);
                        break;
                    case Constants.CONSTANS_RAIN:
                        //holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_RES_WATER:
                        holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_RIVER_WATER:
                        holder.tv6.setVisibility(View.GONE);
                        holder.tv7.setVisibility(View.GONE);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            try {
                final JSONObject jsonObject = jsonArray.getJSONObject(position);
                switch (MyApplication.CONSTANS_WATER_TYPE) {
                    case Constants.CONSTANS_RIVER_WATER:
                        holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                        holder.tv2.setText(jsonObject.getString("Z"));
                        holder.tv3.setText(jsonObject.getString("WRZ"));
                        holder.tv4.setText(jsonObject.getString("TREND"));
                        holder.tv5.setText(jsonObject.getString("HNNM"));
                        if (jsonObject.has("alert")) {
                            holder.tv2.setTextColor(Color.RED);
                        } else {
                            holder.tv2.setTextColor(black);
                        }
                        break;
                    case Constants.CONSTANS_TIDE:
                        holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                        holder.tv2.setText(jsonObject.getString("Z"));
                        holder.tv3.setText(jsonObject.getString("RIVER"));
                        holder.tv4.setText(jsonObject.getString("CITY"));
                        break;
                    case Constants.CONSTANS_RES_WATER:
                        holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                        // holder.tv2.setText(jsonObject.getString("RES_STTP"));
                        holder.tv3.setText(jsonObject.getString("Z"));
                        // holder.tv4.setText(jsonObject.getString("ZKR"));
//                        holder.tv5.setText(jsonObject.getString("FHGSW"));
//                        holder.tv6.setText(jsonObject.getString("TYPE"));
                        holder.tv4.setText(jsonObject.getString("CTZ"));
                        // holder.tv5.setText(jsonObject.getString("FHGSW"));
                        holder.tv5.setText(jsonObject.getString("CITY"));
                        String TYPE = jsonObject.getString("RES_STTP");
                        switch (TYPE) {
                            case "0201":
                                holder.tv2.setText("大型");
                                break;
                            case "0202":
                                holder.tv2.setText("中型");
                                break;
                            case "0203":
                                holder.tv2.setText("小一型");
                                break;
                            case "0204":
                                holder.tv2.setText("小二型");
                                break;
                            default:
                                holder.tv2.setText("—");
                        }
                        if (jsonObject.has("alert")) {
                            holder.tv3.setTextColor(Color.RED);
                        } else {
                            holder.tv3.setTextColor(black);
                        }
                        break;
                    case Constants.CONSTANS_GATE_WATER:
                        holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                        String UPZ = jsonObject.getString("UPZ");
                        if (UPZ.isEmpty()) {
                            holder.tv2.setText("—");
                        } else {
                            holder.tv2.setText(UPZ);
                        }
                        String DWZ = jsonObject.getString("DWZ");
                        if (DWZ.isEmpty()) {
                            holder.tv3.setText("—");
                        } else {
                            holder.tv3.setText(DWZ);
                        }
                        holder.tv4.setText(jsonObject.getString("HNNM"));
                        holder.tv5.setText(jsonObject.getString("CITY"));
                        break;
                    case Constants.CONSTANS_PUMP_WATER:
                        holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                        String PPDWZ = jsonObject.getString("PPDWZ");
                        if (PPDWZ.isEmpty()) {
                            holder.tv2.setText("—");
                        } else {
                            holder.tv2.setText(PPDWZ);
                        }
                        String Z = jsonObject.getString("Z");
                        if (Z.isEmpty()) {
                            holder.tv3.setText("—");
                        } else {
                            holder.tv3.setText(Z);
                        }
                        holder.tv4.setText(jsonObject.getString("ZUIGAOSHUIWEI"));
                        holder.tv5.setText(jsonObject.getString("CITYNM"));
                        if (jsonObject.has("alert")) {
                            holder.tv2.setTextColor(Color.RED);
                        } else {
                            holder.tv2.setTextColor(black);
                        }
                        break;
                    case Constants.CONSTANS_FLOOD:
                        holder.tv1.setText(jsonObject.getString("DLMC") );//+ jsonObject.getString("STNM")
                        // holder.tv2.setText(jsonObject.getString("DLMC"));
                        holder.tv2.setText(jsonObject.getString("DEPTH"));
                        if (jsonObject.isNull("TM")){
                            holder.tv3.setText(DateUtil.parseStringTime(jsonObject.getString("TM")));
                        } else {
                            holder.tv3.setText(DateUtil.parseStringTime(jsonObject.getString("TM")));
                        }


//                      积淹点深度调整小数后一位(2017-05-21)
                        if ("—".equals(jsonObject.getString("DEPTH"))) {
                            holder.tv2.setTextColor(black);
                            break;
                        }
                        double VALUE = Double.valueOf(jsonObject.getString("DEPTH"));
                        if (VALUE >= 20) {
                            holder.tv2.setTextColor(Color.RED);
                        } else {
                            holder.tv2.setTextColor(black);
                        }
                        break;
                    case Constants.CONSTANS_RAIN:
                        holder.tv1.setText(Html.fromHtml("<u>" + jsonObject.getString("STNM") + "</u>"));
                        holder.tv2.setText(CommonUtil.format2One(jsonObject.getString("TVALUE")));
                        holder.tv3.setText(CommonUtil.format2One(jsonObject.getString("YVALUE")));
                        holder.tv4.setText(CommonUtil.format2One(jsonObject.getString("BYVALUE")));
                        holder.tv5.setText(CommonUtil.format2One(jsonObject.getString("TTVALUE")));
                        holder.tv6.setText(jsonObject.getString("STLC"));
                        break;
                }
                if (activity.favoritesList.contains(jsonObject.getString("STCD"))) {
                    holder.favoriteIcon.setBackgroundResource(R.mipmap.favorite1);
                    holder.favorite.setTag(1);
                } else {
                    holder.favoriteIcon.setBackgroundResource(R.mipmap.favorite0);
                    holder.favorite.setTag(0);
                }
                holder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int x = (int) view.getTag();
                            String stcd = jsonObject.getString("STCD");
                            if (x == 1) {
                                //取消收藏
                                holder.favoriteIcon.setBackgroundResource(R.mipmap.favorite0);
                                view.setTag(0);
                                Toast.makeText(activity, "取消收藏", Toast.LENGTH_SHORT).show();
                                int index = activity.favoritesList.indexOf(stcd);
                                if (index == -1) {
                                    return;
                                }
                                activity.favoritesList.remove(index);
                            } else {
                                //收藏
                                holder.favoriteIcon.setBackgroundResource(R.mipmap.favorite1);
                                view.setTag(1);
                                Toast.makeText(activity, "收藏成功", Toast.LENGTH_SHORT).show();
                                activity.favoritesList.add(stcd);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        /**
         * 日期去掉年和秒 例2017-05-05 11:02:00
         *
         * @param x
         * @return
         */
        private String handle(String x) {
            int index1 = x.indexOf("—");
            int index2 = x.lastIndexOf(":");
            return x.substring(index1 + 1, index2);
        }

        class Holder {
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
            TextView tv5;
            TextView tv6;
            TextView tv7;
            RelativeLayout favorite;
            ImageView favoriteIcon;
        }
    }


    class MyComparator implements Comparator<JSONObject> {
        private final int order;
        private final String key;

        /**
         * @param order 0表示降序，1表示升序
         * @param key   表示排序的key
         */
        public MyComparator(int order, String key) {
            super();
            this.order = order;
            this.key = key;
        }

        @Override
        public int compare(JSONObject lhs, JSONObject rhs) {
            try {
                float number1, number2;
                String numberString1 = "0", numberString2 = "0";
                numberString1 = lhs.getString(key);
                numberString2 = rhs.getString(key);
                if (numberString1.equals("—")) {
                    if (order == 0) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else if (numberString2.equals("—")) {
                    if (order == 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                number1 = Float.valueOf(numberString1);
                number2 = Float.valueOf(numberString2);
                if (order == 0) {
                    // 降序排列
                    if (number1 > number2) {
                        return -1;
                    } else if (number1 == number2) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    // 升序排列
                    if (number1 > number2) {
                        return 1;
                    } else if (number1 == number2) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}