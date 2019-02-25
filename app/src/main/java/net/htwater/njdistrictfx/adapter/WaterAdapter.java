package net.htwater.njdistrictfx.adapter;


import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.bean.WaterBean;
import net.htwater.njdistrictfx.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LZY on 2017/6/5.
 */

public class WaterAdapter extends BaseAdapter {
    private Context context;
    private List<WaterBean> list = new ArrayList<>();

    public WaterAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setData(List<WaterBean> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            holder = new Holder();
            convertView = View.inflate(context, R.layout.listitem_water, null);
            holder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.tv2);
            holder.tv3 = (TextView) convertView.findViewById(R.id.tv3);
            holder.tv4 = (TextView) convertView.findViewById(R.id.tv4);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        WaterBean bean = list.get(position);
        holder.tv1.setText(Html.fromHtml("<u>" + bean.getSTNM() + "</u>"));
        holder.tv2.setText(CommonUtil.format2Two(bean.getZ()));
        holder.tv3.setText(CommonUtil.format2Two(bean.getWRZ()));
        holder.tv4.setText(bean.getQ());

        if (Double.parseDouble(bean.getZ())>Double.parseDouble(bean.getWRZ())) {
            holder.tv2.setTextColor(Color.RED);
        } else {
            holder.tv2.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    private class Holder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tv4;
    }
}
