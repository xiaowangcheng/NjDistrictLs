package net.htwater.njdistrictfx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.bean.DeleteAttachemntEvent;

import org.greenrobot.eventbus.EventBus;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zty on 2016/4/28.
 */
public class ImageGridAdapter extends BaseAdapter {
    /**
     * ImageOptions对象
     */
    ImageOptions options;
    /**
     * 当前Content
     */
    private Context m_content;
    /**
     * 加载数据
     */
    private List<Map<String, Object>> m_items = new ArrayList<Map<String, Object>>();
    /**
     * 布局加载器
     */
    private LayoutInflater inflater = null;

    public ImageGridAdapter(Context context, List<Map<String, Object>> data) {
        super();
        this.m_items = data;
        this.m_content = context;
        inflater = LayoutInflater.from(context);
        options = new ImageOptions.Builder()
                .setFailureDrawable(m_content.getResources().getDrawable(R.mipmap.ic))
                .build();
    }

    /**
     * 添加数据
     *
     * @param obj 主数据
     */
    public void addItem(Map<String, Object> obj) {
        m_items.add(obj);
    }

    /**
     * 添加数据列表
     *
     * @param data 数据列表
     */
    public void addItems(List<Map<String, Object>> data) {
        m_items = data;
    }

    /**
     * 清空数据
     */
    public void clearItems() {
        for (Map<String, Object> item : m_items) {
            if ("photo".equals(item.get("flag")+"")) {
                m_items.remove(item);
            }
        }
        for (int i = m_items.size() - 1; i >= 0; i--) {
            if ("photo".equals(m_items.get(i).get("flag")+"")) {
                m_items.remove(i);
            }
        }
    }

    @Override
    public int getCount() {
        return m_items.size();
    }

    @Override
    public Object getItem(int position) {
        return m_items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = null;
        if (inflater != null) {
            view = inflater.inflate(R.layout.photo_grid_item, null);
            RelativeLayout rv_photo = (RelativeLayout) view.findViewById(R.id.rv_photo);
            RelativeLayout rv_cross = (RelativeLayout) view.findViewById(R.id.rv_cross);
            RelativeLayout rv_crossImg = (RelativeLayout) view.findViewById(R.id.rv_crossImg);
            ImageView iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
            final ImageView iv_photo_delete = (ImageView) view.findViewById(R.id.iv_photo_delete);
            // 图片时候显示图片，其他画十字图
//            if ("photo".equals(m_items.get(position).get("flag")+"")) {
                rv_cross.setVisibility(View.GONE);
                rv_photo.setVisibility(View.VISIBLE);
                iv_photo_delete.setVisibility(View.VISIBLE);
//                if (m_items.get(position).containsKey("filepath")) {
                    x.image().bind(iv_photo, (String) m_items.get(position).get("filepath"), options);
//                }
//            } else {
//                rv_photo.setVisibility(View.GONE);
//                rv_cross.setVisibility(View.VISIBLE);
//                CrossView crossView = new CrossView(m_content, m_content.getResources().getColor(R.color.seperate_line), 3.0f);
//                rv_crossImg.addView(crossView);
//            }
//            iv_photo_delete.setOnClickListener(new ImgClickListener(position));
            iv_photo_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(m_content,"delete", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new DeleteAttachemntEvent("photo",position));
                }
            });
            //rv_crossImg.setOnClickListener(new ImgClickListener(position));
        }
        return view;
    }

}
