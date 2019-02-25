package net.htwater.njdistrictfx.fragment.ListAndMap;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.SYQ.WaterLevelDetailActivity;
import net.htwater.njdistrictfx.adapter.WaterAdapter;
import net.htwater.njdistrictfx.bean.WaterBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lzy on 2017/6/5
 */
public class WaterListFragment extends Fragment {
    private WaterAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_water_list, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);

        adapter = new WaterAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WaterBean waterBean = (WaterBean) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), WaterLevelDetailActivity.class);
                intent.putExtra("stnm", waterBean.getSTNM());
                intent.putExtra("stcd", waterBean.getSTCD());
                intent.putExtra("wrz", waterBean.getWRZ());
//                intent.putExtra("sttp", waterBean.getSttp());
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<WaterBean> list) {
        if (list.size() == 0) {
            adapter.setData(list);
            Toast.makeText(getActivity().getApplicationContext(), "暂无数据", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.setData(handle(list));
        Toast.makeText(getActivity().getApplicationContext(), "共有" + list.size() + "条记录", Toast.LENGTH_SHORT).show();
    }

    /**
     * 把超警戒站点排到最前面
     *
     * @param list
     * @return
     */
    private List<WaterBean> handle(List<WaterBean> list) {
        List<WaterBean> resultList = new ArrayList<>();
        for (WaterBean waterBean : list) {
            String s = waterBean.getZ();
            String wrz = waterBean.getWRZ();
            if(s!=null && !s.equals("") && !s.equals("null")){
                if(wrz!=null && !wrz.equals("") && !wrz.equals("null")){
                    if (Double.parseDouble(waterBean.getZ())>Double.parseDouble(waterBean.getWRZ())) {
                        resultList.add(0, waterBean);
                    } else {
                        resultList.add(waterBean);
                    }
                }
            }
        }
        return resultList;
    }
}