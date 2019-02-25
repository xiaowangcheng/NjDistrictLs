package net.htwater.njdistrictfx.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zty on 2016/4/28.
 */
public class VideoExListAdapter extends BaseAdapter {
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

    public VideoExListAdapter(Context context, List<Map<String, Object>> data) {
        super();
        this.m_items = data;
        this.m_content = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 添加主数据
     *
     * @param obj 主数据
     */
    public void addItem(Map<String, Object> obj) {
        m_items.add(obj);
    }

    /**
     * 添加主数据
     *
     * @param data 主数据列表
     */
    public void addItems(List<Map<String, Object>> data) {
        m_items = data;
    }

    /**
     * 清空数据
     */
    public void clearItems() {
        m_items.clear();
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
        if (null != inflater) {
            view = inflater.inflate(R.layout.video_grid_item, null);
            RelativeLayout rv_photo = (RelativeLayout) view.findViewById(R.id.rv_photo);
            RelativeLayout rv_cross = (RelativeLayout) view.findViewById(R.id.rv_cross);
            RelativeLayout rv_crossImg = (RelativeLayout) view.findViewById(R.id.rv_crossImg);
            ImageView iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
            final ImageView iv_photo_delete = (ImageView) view.findViewById(R.id.iv_photo_delete);

            File file = new File((String) m_items.get(position).get("filepath"));

            iv_photo_delete.setVisibility(View.VISIBLE);

            if (file.exists()) {
                Bitmap bitmap = getVideoThumbnail(file.getPath());
                if (null != bitmap) {
                    iv_photo.setImageBitmap(bitmap);
                }
            }

            iv_photo_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(m_content,"delete", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new DeleteAttachemntEvent("video",position));
                }
            });
        }
        return view;
    }

    private Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
