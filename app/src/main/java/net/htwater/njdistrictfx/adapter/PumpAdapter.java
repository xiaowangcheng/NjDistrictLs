package net.htwater.njdistrictfx.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htwater.njdistrictfx.core.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PumpAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> list = new ArrayList<>();

    public PumpAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<JSONObject> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(context);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, (int) (36 * MyApplication.density));
        tv.setLayoutParams(layoutParams);

        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextSize(16f);
        tv.setPadding((int) (12 * MyApplication.density), 0, 0, 0);
        tv.setBackgroundColor(Color.WHITE);

        try {
            JSONObject jsonObject = list.get(position);
            tv.setText(jsonObject.getString("STNM"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tv;
    }
}
