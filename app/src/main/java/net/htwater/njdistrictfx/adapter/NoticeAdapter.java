package net.htwater.njdistrictfx.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.bean.NoticeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LZY on 2017/6/23.
 */

public class NoticeAdapter extends BaseAdapter {
    private Context context;
    private List<NoticeBean> list = new ArrayList<>();

    public NoticeAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<NoticeBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
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
            convertView = View.inflate(context, R.layout.listitem_notice, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.content = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        NoticeBean bean = list.get(position);
        holder.title.setText(bean.getList_title());
        holder.author.setText("作者：" + bean.getAuthor());
        holder.time.setText(bean.getTime().replace("00:00:00", ""));
        holder.content.setText(Html.fromHtml(bean.getList_content()));
        return convertView;
    }

    class Holder {
        TextView title;
        TextView author;
        TextView time;
        TextView content;
    }
}
