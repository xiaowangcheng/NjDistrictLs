package net.htwater.njdistrictfx.fragment.ListAndMap;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.Util;
import net.htwater.njdistrictfx.view.MyScrollView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lzy on 2017/4/17
 */
public class GcxxListFragment extends Fragment {
    private MyAdapter adapter;
    private MyScrollView scrollView;
    // private boolean isClick;// 记录是点击还是滑动
    private int type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_gcxx_list, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
        FrameLayout topBar = (FrameLayout) view.findViewById(R.id.topBar);

        adapter = new MyAdapter(getActivity());

        int id = 0;
        type = MyApplication.CONSTANS_ENGINEERING_TYPE;
        switch (type) {
            case Constants.CONSTANS_RES:
                id = R.layout.topbar_gcxx_res;
                break;
            case Constants.CONSTANS_PUMP:
                id = R.layout.topbar_gcxx_pump;
                break;
            case Constants.CONSTANS_GATE:
                id = R.layout.topbar_gcxx_gate;
                break;
            case Constants.CONSTANS_DIKE:
                id = R.layout.topbar_gcxx_dike;
                break;
            case Constants.CONSTANS_RIVER:
                id = R.layout.topbar_gcxx_river;
                break;
        }
        View topBarView = View.inflate(getActivity(), id, null);
        topBar.addView(topBarView);

        listView.setAdapter(adapter);
        listView.setOnTouchListener(new OnTouchListener() {
            float x = 0;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        // isClick = Math.abs(event.getX() - x) <= 10;//? true : false;
                        break;
                }
                return false;
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
        if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_RIVER) {
            adapter.setData(handleData1(jsonArray));
        } else if (MyApplication.CONSTANS_ENGINEERING_TYPE == Constants.CONSTANS_DIKE) {
            adapter.setData(handleData2(jsonArray));
        } else {
            adapter.setData(jsonArray);
        }

        Toast.makeText(getActivity().getApplicationContext(), "共有" + jsonArray.length() + "条记录", Toast.LENGTH_SHORT).show();
    }

    private JSONArray handleData1(JSONArray jsonArray) {
        List<JSONObject> list = new ArrayList<>();
        List<JSONObject> list1 = new ArrayList<>();
        List<JSONObject> list2 = new ArrayList<>();
        List<JSONObject> list3 = new ArrayList<>();
        List<JSONObject> list4 = new ArrayList<>();
        List<JSONObject> list5 = new ArrayList<>();
        JSONArray newArray = new JSONArray();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String GQMC = jsonObject.getString("GQMC");
                list.add(jsonObject);
                /*String ENTYNM = jsonObject.getString("ENTYNM");
                if (ENTYNM.contains("一")) {
                    list1.add(jsonObject);
                } else if (ENTYNM.contains("二")) {
                    list2.add(jsonObject);
                } else if (ENTYNM.contains("三")) {
                    list3.add(jsonObject);
                } else if (ENTYNM.contains("四")) {
                    list4.add(jsonObject);
                } else if (ENTYNM.contains("五")) {
                    list5.add(jsonObject);
                }*/
            }
            for (JSONObject x : list) {
                newArray.put(x);
            }
            for (JSONObject x : list1) {
                newArray.put(x);
            }
            for (JSONObject x : list2) {
                newArray.put(x);
            }
            for (JSONObject x : list3) {
                newArray.put(x);
            }
            for (JSONObject x : list4) {
                newArray.put(x);
            }
            for (JSONObject x : list5) {
                newArray.put(x);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newArray;
    }

    private JSONArray handleData2(JSONArray jsonArray) {
        List<JSONObject> list = new ArrayList<>();
        List<JSONObject> otherList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String DSFLST = jsonObject.getString("DSFLST");
                if (DSFLST.equals("—")) {
                    otherList.add(jsonObject);
                } else {
                    list.add(jsonObject);
                }
            }
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = 1; j < list.size() - i; j++) {
                    Integer a = Integer.parseInt(list.get(j - 1).getString("DSFLST"));
                    Integer b = Integer.parseInt(list.get(j).getString("DSFLST"));

                    if (a.compareTo(b) < 0) { // 比较两个整数的大小
                        JSONObject temp = list.get(j - 1);
                        list.set(j - 1, list.get(j));
                        list.set(j, temp);
                    }
                }
            }
            list.addAll(otherList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray newArray = new JSONArray();
        for (JSONObject jsonObject : list) {
            newArray.put(jsonObject);
        }
        return newArray;
    }

    class MyAdapter extends BaseAdapter {
        private final Context context;
        private JSONArray jsonArray = new JSONArray();

        public MyAdapter(Context context) {
            super();
            this.context = context;
        }

        public void setData(JSONArray jsonArray) {
            this.jsonArray = jsonArray;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (null == convertView) {
                holder = new Holder();
                convertView = View.inflate(context, R.layout.listitem_gcxx, null);
                holder.scrollView = (HorizontalScrollView) convertView.findViewById(R.id.scrollView);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);
                holder.tv6 = (TextView) convertView.findViewById(R.id.tv6);
                holder.tv7 = (TextView) convertView.findViewById(R.id.tv7);
                holder.tv8 = (TextView) convertView.findViewById(R.id.tv8);
                holder.tv9 = (TextView) convertView.findViewById(R.id.tv9);
                holder.tv10 = (TextView) convertView.findViewById(R.id.tv10);
                holder.tv11 = (TextView) convertView.findViewById(R.id.tv11);
                holder.tv12 = (TextView) convertView.findViewById(R.id.tv12);

                holder.tv13 = (TextView) convertView.findViewById(R.id.tv13); holder.tv13.setVisibility(View.GONE);
                holder.tv14 = (TextView) convertView.findViewById(R.id.tv14); holder.tv14.setVisibility(View.GONE);
                holder.tv15 = (TextView) convertView.findViewById(R.id.tv15); holder.tv15.setVisibility(View.GONE);
                holder.tv16 = (TextView) convertView.findViewById(R.id.tv16); holder.tv16.setVisibility(View.GONE);
                holder.tv17 = (TextView) convertView.findViewById(R.id.tv17); holder.tv17.setVisibility(View.GONE);
                holder.tv18 = (TextView) convertView.findViewById(R.id.tv18); holder.tv18.setVisibility(View.GONE);
                scrollView.addToList(holder.scrollView);
                switch (type) {
                    case Constants.CONSTANS_RES:
                        holder.tv13.setVisibility(View.VISIBLE);
                        holder.tv14.setVisibility(View.VISIBLE);
                        holder.tv15.setVisibility(View.VISIBLE);
                        holder.tv16.setVisibility(View.VISIBLE);
                        holder.tv17.setVisibility(View.VISIBLE);
                        holder.tv18.setVisibility(View.VISIBLE);
                        break;
                    case Constants.CONSTANS_PUMP:
                        break;
                    case Constants.CONSTANS_GATE:
                        holder.tv7.setVisibility(View.GONE);
                        holder.tv8.setVisibility(View.GONE);
                        holder.tv9.setVisibility(View.GONE);
                        holder.tv10.setVisibility(View.GONE);
                        holder.tv11.setVisibility(View.GONE);
                        holder.tv12.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_DIKE:
                        holder.tv10.setVisibility(View.GONE);
                        holder.tv11.setVisibility(View.GONE);
                        holder.tv12.setVisibility(View.GONE);
                        break;
                    case Constants.CONSTANS_RIVER:

                        holder.tv9.setVisibility(View.GONE);
                        holder.tv10.setVisibility(View.GONE);
                        holder.tv11.setVisibility(View.GONE);
                        holder.tv12.setVisibility(View.GONE);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                switch (type) {
                    case Constants.CONSTANS_RES:
                        holder.tv1.setText(jsonObject.getString("GQMC"));
                        holder.tv2.setText(jsonObject.getString("SPMC"));
                        holder.tv3.setText(jsonObject.getString("LV"));
                        holder.tv4.setText( jsonObject.getString("STREET"));
                        holder.tv5.setText(jsonObject.getString("DRBSAR"));
                        holder.tv6.setText(jsonObject.getString("BLDT"));
                        holder.tv7.setText(jsonObject.getString("DSFLST"));
                        holder.tv8.setText(jsonObject.getString("CHFLST"));
                        holder.tv9.setText(jsonObject.getString("TTST"));
                        // holder.tv10.setText(jsonObject.getString("DESIGN_FLOOD_Z"));
                        holder.tv10.setText(jsonObject.getString("EFST"));
                        holder.tv11.setText(jsonObject.getString("CHFLLV"));

                        holder.tv12.setText(jsonObject.getString("NRWTLV"));
                        holder.tv13.setText(jsonObject.getString("XUNXLV"));
                        holder.tv14.setText(jsonObject.getString("DMTPWD"));
                        holder.tv15.setText(jsonObject.getString("MXDMHG"));
                        holder.tv16.setText(jsonObject.getString("DMTPLN"));
                        holder.tv17.setText(jsonObject.getString("BDHG"));
                        holder.tv18.setText(jsonObject.getString("YHDWD"));
                        break;
                    case Constants.CONSTANS_PUMP:
                        holder.tv1.setText(jsonObject.getString("GQMC"));
                        holder.tv2.setText(jsonObject.getString("TOWNSHIP")); //所属乡镇
                        holder.tv3.setText(jsonObject.getString("STATIONCATEGORY"));//类别
                        holder.tv4.setText(jsonObject.getString("BOOLKEYSTATION"));//是否重点泵站

                        holder.tv5.setText(jsonObject.getString("WATERFLOW")); //流量
                        holder.tv6.setText(jsonObject.getString("PUMPCOUNT"));//泵机台套
                        holder.tv7.setText(jsonObject.getString("MAXWATERLEVEL"));//最高水位
                        holder.tv8.setText(jsonObject.getString("MINWATERLEVEL"));//最低水位
                        holder.tv9.setText(jsonObject.getString("BOOLAUTOOPEN")); //是否自动开启

                        holder.tv10.setText(jsonObject.getString("RIVERWAY")); //所属河道
                        holder.tv11.setText(jsonObject.getString("STATIONTYPE")); //泵站类型
                        holder.tv12.setText(jsonObject.getString("MAINTENANCEUNIT"));//养护单位
                        holder.tv13.setText(jsonObject.getString("REMARKS"));//备注
                        break;
                    case Constants.CONSTANS_GATE:
                        holder.tv1.setText(jsonObject.getString("ENNM"));
                        holder.tv2.setText(jsonObject.getString("ENTYNM"));
                        holder.tv3.setText(jsonObject.getString("HLNM"));
                        // holder.tv4.setText(jsonObject.getString("SIZE1"));
                        holder.tv4.setText(jsonObject.getString("GCDB"));
                        holder.tv5.setText(jsonObject.getString("ADUNNM"));
                        holder.tv6.setText(jsonObject.getString("COUNTY"));
                        // holder.tv8.setText(jsonObject.getString("MNCTRGD"));
                        break;
                    case Constants.CONSTANS_DIKE:
                        holder.tv1.setText(jsonObject.getString("ENNM"));
                        holder.tv2.setText(jsonObject.getString("DSFLST"));
                        holder.tv3.setText(jsonObject.getString("BNSCLN"));
                        holder.tv4.setText(jsonObject.getString("DDKDMAX"));
                        holder.tv5.setText(jsonObject.getString("DKCL"));
                        holder.tv6.setText(jsonObject.getString("BNSCTP"));
                        holder.tv7.setText(jsonObject.getString("EGCTRST"));
                        holder.tv8.setText(jsonObject.getString("GCRW"));
                        holder.tv9.setText(jsonObject.getString("ADUNNM"));
                        // holder.tv10.setText(jsonObject.getString("ADUNNM"));
                        break;
                    case Constants.CONSTANS_RIVER:
                        holder.tv1.setText(jsonObject.getString("GQMC"));

                        holder.tv2.setText(jsonObject.getString("ADMINISTRA"));//所属街道
                        holder.tv3.setText(jsonObject.getString("RIVEREVALU"));//河道评价
                        holder.tv4.setText(jsonObject.getString("RIVERUSE"));//河道分类
//                        holder.tv5.setText(jsonObject.getString("AVPDP"));
//                        holder.tv6.setText(jsonObject.getString("SITE"));
                        holder.tv5.setText(jsonObject.getString("RIVERLOCAT"));//起始位置
                        holder.tv6.setText(jsonObject.getString("RIVERLENGT"));//河流成都
                        holder.tv7.setText(jsonObject.getString("RIVERTYPE ")); //河道状态
               /*         ViewGroup.LayoutParams params = holder.tv8.getLayoutParams();
                        params.width=120;
                        holder.tv8.setLayoutParams(params);*/
                        holder.tv8.setText(jsonObject.getString("REMARKS "));//备注
                        /*holder.tv1.setText(jsonObject.getString("ENNM"));
                        holder.tv2.setText(jsonObject.getString("LENGTH"));
                        holder.tv3.setText(jsonObject.getString("ENTYNM"));
                        holder.tv4.setText(jsonObject.getString("TTDRBSAR"));
//                        holder.tv5.setText(jsonObject.getString("AVPDP"));
//                        holder.tv6.setText(jsonObject.getString("SITE"));
                        holder.tv5.setText(jsonObject.getString("ADUNNM"));*/
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        private String handle(String x) {
            if (x.equals("—")) {
                return x;
            }
            double y = Double.valueOf(x);
            y = Util.get2Double(y);
            return "" + y;
        }

        class Holder {
            HorizontalScrollView scrollView;
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
            TextView tv5;
            TextView tv6;
            TextView tv7;
            TextView tv8;
            TextView tv9;
            TextView tv10;
            TextView tv11;
            TextView tv12;
            TextView tv13;
            TextView tv14;
            TextView tv15;
            TextView tv16;
            TextView tv17;
            TextView tv18;
        }
    }
}
