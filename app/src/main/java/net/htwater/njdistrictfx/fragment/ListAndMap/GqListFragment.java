package net.htwater.njdistrictfx.fragment.ListAndMap;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by lzy on 2017/4/1
 */
public class GqListFragment extends Fragment {
    private MyAdapter adapter;
    // private MyScrollView scrollView;
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
        View view = View.inflate(getActivity(), R.layout.fragment_gq_list, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        // scrollView = (MyScrollView) view.findViewById(R.id.scrollView);
        FrameLayout topBar = (FrameLayout) view.findViewById(R.id.topBar);

        adapter = new MyAdapter(getActivity());

        int id = 0;
        type = MyApplication.CONSTANS_ENGINEERING_TYPE;
        switch (type) {
            case Constants.CONSTANS_PUMP:
                id = R.layout.topbar_pump_gq;
                break;
            case Constants.CONSTANS_GATE:
                id = R.layout.topbar_gate_gq;
                break;
        }
        View topBarView = View.inflate(getActivity(), id, null);
        topBar.addView(topBarView);

        listView.setAdapter(adapter);
//        listView.setOnTouchListener(new OnTouchListener() {
//            float x = 0;
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                scrollView.onTouchEvent(event);
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        x = event.getX();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        isClick = Math.abs(event.getX() - x) <= 10;//? true : false;
//                        break;
//                }
//                return false;
//            }
//        });
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (isClick) {
//                    EngineeringBean bean = (EngineeringBean) adapter
//                            .getItem(position);
//                    Intent intent = new Intent(getActivity(),
//                            DetailActivity.class);
//                    intent.putExtra("value", bean);
//                    startActivity(intent);
//                }
            }
        });
        // EventBus.getDefault().post(new Event());//通知activity加载完成

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(JSONArray jsonArray) {
        if (jsonArray.length() == 0) {
            adapter.setData(jsonArray);
            Toast.makeText(getActivity().getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.setData(handle(jsonArray));
        Toast.makeText(getActivity().getApplicationContext(), "共有" + jsonArray.length() + "条记录", Toast.LENGTH_SHORT).show();
    }

    private JSONArray handle(JSONArray jsonArray) {
        List<JSONObject> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //if (jsonObject.getString("CN").startsWith("0")) {
                //    list.add(jsonObject);
               // } else {
                    list.add(0, jsonObject);
               // }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray resultArray = new JSONArray();
        for (JSONObject jsonObject : list) {
            resultArray.put(jsonObject);
        }
        return resultArray;
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
                convertView = View.inflate(context, R.layout.listitem_gq, null);
                holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);
                holder.tv5 = (TextView) convertView.findViewById(R.id.tv5);

                if (type == Constants.CONSTANS_PUMP) {
                    holder.tv5.setVisibility(View.GONE);
                }

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);

                switch (type) {
                    case Constants.CONSTANS_PUMP:
                        holder.tv1.setText(jsonObject.getString("PUMPNAME"));
                        holder.tv2.setText(jsonObject.getString("FLOW"));
                        holder.tv3.setText(jsonObject.getString("CURRENTNUMBER"));
                        holder.tv4.setText(jsonObject.getString("OPENNUM"));

                        /*holder.tv2.setText(jsonObject.getString("CN") + "/" + jsonObject.getString("CNT"));
                        holder.tv3.setText(jsonObject.getString("RTFLOW") + "/" + jsonObject.getString("ZLL"));
                        holder.tv4.setText(jsonObject.getString("HNNM"));*/
                        if (jsonObject.getString("CN").startsWith("0")) {
                            holder.tv2.setTextColor(getResources().getColor(R.color.text_black));
                        } else {
                            holder.tv2.setTextColor(getResources().getColor(R.color.theme_blue));
                        }
                        break;
                    case Constants.CONSTANS_GATE:
                        String KDS = jsonObject.getString("KDS");
                        String ZMNM = jsonObject.getString("ZMNM");
                        holder.tv1.setText(ZMNM);
                        holder.tv2.setText(jsonObject.getString("ZHSW"));
                        holder.tv3.setText(jsonObject.getString("ZQSW"));
                        holder.tv4.setText(jsonObject.getString("CITYNM"));
                        if (ZMNM.contains("橡胶坝")) {
                            holder.tv5.setText("—");
                        } else {
                            holder.tv5.setText(handle(KDS));
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        // 例0/3
        private String handle(String KDS) {
            int index = KDS.indexOf("/");
            return KDS.substring(index + 1);
        }

//        private String handle(String kds) {
//            String[] array = kds.split(",");
//            String x1 = "";
//            String y1 = "";
//            for (int i = 0; i < array.length; i++) {
//                String value = array[i];
//                int j = i + 1;
//                if (value.equals("1")) {
//                    x1 = x1 + j + "、";
//                } else {
//                    y1 = y1 + j + "、";
//                }
//            }
//            if (x1.endsWith("、")) {
//                x1 = x1.substring(0, x1.length() - 1);
//            }
//            if (y1.endsWith("、")) {
//                y1 = y1.substring(0, y1.length() - 1);
//            }
//            String resultStr = "总孔" + array.length + "(开:孔" + x1 + ",关:孔" + y1 + ")";
//            resultStr = resultStr.replace("开:孔,", "");
//            if (y1.isEmpty()) {
//                resultStr = resultStr.replace(",关:孔", "");
//            }
//            return resultStr;
//        }

        class Holder {
            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;
            TextView tv5;
        }
    }
}
