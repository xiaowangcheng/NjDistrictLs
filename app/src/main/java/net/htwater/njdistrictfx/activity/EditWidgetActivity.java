package net.htwater.njdistrictfx.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.util.DataUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;
import net.htwater.njdistrictfx.view.SwitchView;
import net.htwater.njdistrictfx.view.SwitchView.OnStateChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EditWidgetActivity extends BaseActivity {
    private List<String> curList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_widget);

        ListView listView = (ListView) findViewById(R.id.listView);
        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);

        curList = getIntent().getStringArrayListExtra("list");

        listView.setAdapter(new MyAdapter(this));

        title.setText("编辑插件");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        saveData();
        super.onBackPressed();
    }

    private void saveData() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("list", (ArrayList<String>) curList);
        setResult(1, intent);
        SharedPreferencesUtil.setWidgetList(curList);
    }

    class MyAdapter extends BaseAdapter {
        private final Context context;
        private final List<String> allList;

        public MyAdapter(Context context) {
            this.context = context;
            allList = handle(DataUtil.getAllWidgetList());
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
               // map.put("1.2", "air quality");
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
            return resultList;
        }

        @Override
        public int getCount() {
            return allList.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(context, R.layout.listitem_edit_widget, null);
            RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            final SwitchView mySwitch = (SwitchView) convertView.findViewById(R.id.mySwitch);

            final String widgetName = allList.get(position);
            name.setText(DataUtil.getWidgetChineseName(widgetName));

            if (curList.contains(widgetName)) {
                mySwitch.setOpened(true);
            }
            mySwitch.setOnStateChangedListener(new OnStateChangedListener() {

                @Override
                public void toggleToOn(View view) {
                    curList.add(widgetName);
                }

                @Override
                public void toggleToOff(View view) {
                    curList.remove(widgetName);
                }
            });

            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mySwitch.isOpened()) {
                        mySwitch.setOpened(false);
                        curList.remove(widgetName);
                    } else {
                        mySwitch.setOpened(true);
                        curList.add(widgetName);
                    }
                }
            });

            return convertView;
        }
    }
}
