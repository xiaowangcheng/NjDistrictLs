package net.htwater.njdistrictfx.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.bean.UpdateInfo;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import static net.htwater.njdistrictfx.R.id.date;
import static net.htwater.njdistrictfx.R.id.info;

public class UpdateLogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesUtil.isFloodSeason()) {
            setTheme(R.style.AppTheme_Orange);
        } else {
            setTheme(R.style.AppTheme_Blue);
        }
        setContentView(R.layout.activity_update_log);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        ListView listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(new MyAdapter());

        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        private List<UpdateInfo> infos = new ArrayList<>();

        private MyAdapter() {
            initData();
        }

        private void initData() {
           /* UpdateInfo info4 = new UpdateInfo();
            info4.setDate("2017/07/21");
            info4.setInfo("修改 水位超警戒判断条件");
            info4.setVersionName("版本 1.60");
            infos.add(info4);

            UpdateInfo info3 = new UpdateInfo();
            info3.setDate("2017/07/14");
            info3.setInfo("新增 自动清理缓存\n新增 根据用户权限显示通讯录内容\n修复 几个bug");
            info3.setVersionName("版本 1.59");
            infos.add(info3);

            UpdateInfo info2 = new UpdateInfo();
            info2.setDate("2017/07/10");
            info2.setInfo("新增 屏蔽运营商广告");
            info2.setVersionName("版本 1.58");
            infos.add(info2);*/

            UpdateInfo info1 = new UpdateInfo();
            info1.setDate("2019/1/5");
            info1.setInfo("初始版本");
            info1.setVersionName("版本 1");
            infos.add(info1);
        }

        @Override
        public int getCount() {
            return infos.size();
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
            Holder holder;
            if (null == convertView) {
                holder = new Holder();
                convertView = View.inflate(UpdateLogActivity.this, R.layout.listitem_update_log, null);
                holder.versionName = (TextView) convertView.findViewById(R.id.versionName);
                holder.date = (TextView) convertView.findViewById(date);
                holder.info = (TextView) convertView.findViewById(info);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            UpdateInfo updateInfo = infos.get(position);
            holder.versionName.setText(updateInfo.getVersionName());
            holder.date.setText(updateInfo.getDate());
            holder.info.setText(updateInfo.getInfo());

            return convertView;
        }

        class Holder {
            TextView versionName;
            TextView date;
            TextView info;
        }
    }
}
