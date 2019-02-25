package net.htwater.njdistrictfx.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PumpHistoryAdapter extends BaseAdapter {
    private Context context;
    private JSONArray jsonArray = new JSONArray();

    public PumpHistoryAdapter(Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            holder = new Holder();
            convertView = View.inflate(context, R.layout.listitem_pump_history, null);
            holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            holder.tv1.setText(jsonObject.getString("TM"));
            holder.tv2.setText(jsonObject.getString("CNT"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class Holder {
        TextView tv1;
        TextView tv2;
    }
}
