package net.htwater.njdistrictfx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.EditWidgetActivity;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;
import net.htwater.njdistrictfx.widget.AirQualityWidget;
import net.htwater.njdistrictfx.widget.AroundWidget;
import net.htwater.njdistrictfx.widget.EmergencyWidget;
import net.htwater.njdistrictfx.widget.FloodWidget;
import net.htwater.njdistrictfx.widget.LiuliangduibiWidget;
import net.htwater.njdistrictfx.widget.PumpWidget;
import net.htwater.njdistrictfx.widget.RainWidget;
import net.htwater.njdistrictfx.widget.RotaWidget;
import net.htwater.njdistrictfx.widget.ShuiweiduibiWidget;
import net.htwater.njdistrictfx.widget.TyphoonWidget;
import net.htwater.njdistrictfx.widget.WaterWidget;
import net.htwater.njdistrictfx.widget.WeatherWidget;
import net.htwater.njdistrictfx.widget.YuliangWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {
    private LinearLayout widgetLayout;
    private List<String> curList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        widgetLayout = (LinearLayout) view.findViewById(R.id.widgetLayout);
        LinearLayout editWidget = (LinearLayout) view.findViewById(R.id.editWidget);

        EventBus.getDefault().register(this);

        curList = handle(SharedPreferencesUtil.getWidgetList());
        addWidget();

        editWidget.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditWidgetActivity.class);
                intent.putStringArrayListExtra("list", (ArrayList<String>) curList);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<String> list = data.getStringArrayListExtra("list");
        if (list.size() != curList.size()) {
            refreshWidget(list);
            return;
        }
        for (String item : list) {
            if (!curList.contains(item)) {
                refreshWidget(list);
                return;
            }
        }
    }

    @Subscribe
    public void onEvent(String[] array) {
        if (null == array) {
            return;
        }
        if (array[0].equals("0")) {
            //置顶
            pinnedWidget(array[1]);
        } else {
            //删除
            deleteWidget(array[1]);
        }
    }

    private List<String> handle(List<String> list) {
        String menu = SharedPreferencesUtil.getMenu();
        if (menu.isEmpty()) {
            return list;
        }
        List<String> resultList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(menu);
            JSONArray jsonArray = jsonObject.getJSONArray("class_id#1");
            List<String> idList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String menuId = jsonObject1.getString("menu_id");
                idList.add(menuId);
            }
            Map<String, String> map = new HashMap<>();
            //map.put("1.1", "weather");
            //map.put("1.3", "water");
            map.put("1.4", "flood");
            map.put("1.5", "typhoon");
            map.put("1.7", "shuiwei");
            //map.put("1.8", "pump");
            //map.put("1.10", "emergency");
            //map.put("1.11", "shuiweiduibi");

            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (idList.contains(key)) {
                    resultList.add(map.get(key));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<String> usefulList = new ArrayList<>();
        for (String name : list) {
            if (resultList.contains(name)) {
                usefulList.add(name);
            }
        }
        return usefulList;
    }

    private void refreshWidget(List<String> list) {
        widgetLayout.removeAllViews();
        curList = list;
        addWidget();
    }

    private void addWidget() {
        for (String name : curList) {
            View view = null;
            switch (name) {
                case "weather":
                    view = new WeatherWidget(getActivity()).getView();
                    break;
                case "rain":
                    view = new RainWidget(getActivity()).getView();
                    break;
                case "air quality":
                    view = new AirQualityWidget(getActivity()).getView();
                    break;
                case "water":
                    view = new WaterWidget(getActivity()).getView();
                    break;
                case "typhoon":
                    view = new TyphoonWidget(getActivity()).getView();
                    break;
                case "pump":
                    view = new PumpWidget(getActivity()).getView();
                    break;
                case "yuliang":
                    view = new YuliangWidget(getActivity()).getView();
                    break;
                case "shuiwei":
                    view = new AroundWidget(getActivity()).getView();
                    break;
                case "rota":
                    view = new RotaWidget(getActivity()).getView();
                    break;
                case "emergency":
                    view = new EmergencyWidget(getActivity()).getView();
                    break;
                case "flood":
                    view = new FloodWidget(getActivity()).getView();
                    break;
                case "shuiweiduibi":
                    view = new ShuiweiduibiWidget(getActivity()).getView();
                    break;
                case "liuliangduibi":
                    view = new LiuliangduibiWidget(getActivity()).getView();
                    break;
            }
            widgetLayout.addView(view);
        }
    }

    private void pinnedWidget(String name) {
        int index = curList.indexOf(name);
        if (index <= 0) {
            return;
        }
        View view = widgetLayout.getChildAt(index);
        widgetLayout.removeViewAt(index);
        curList.remove(name);

        widgetLayout.addView(view, 0);
        curList.add(0, name);
        SharedPreferencesUtil.setWidgetList(curList);
    }

    private void deleteWidget(String name) {
        int index = curList.indexOf(name);
        if (index < 0) {
            return;
        }
        widgetLayout.removeViewAt(index);
        curList.remove(name);
        SharedPreferencesUtil.setWidgetList(curList);
    }
}
