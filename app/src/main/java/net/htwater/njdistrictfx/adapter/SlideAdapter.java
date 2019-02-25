package net.htwater.njdistrictfx.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;

import java.util.List;

/**
 * 工程信息中，侧滑筛选gridview的adapter
 *
 * @author Administrator
 */
public class SlideAdapter extends BaseAdapter {
    private final Context context;
    private final String[] array;
    private final boolean[] isSelect;

    public SlideAdapter(Context context, String[] array) {
        super();
        this.context = context;
        this.array = array;
        isSelect = new boolean[array.length];

        for (int i = 0; i < array.length; i++) {
            isSelect[i] = false;
        }
    }

    public SlideAdapter(Context context, String[] array, int defaultSelectIndex) {
        super();
        this.context = context;
        this.array = array;
        isSelect = new boolean[array.length];

        for (int i = 0; i < array.length; i++) {
//			if (i == defaultSelectIndex) {
//				isSelect[i] = true;
//			} else {
//				isSelect[i] = false;
//			}
            isSelect[i] = i == defaultSelectIndex;
        }
    }

    /**
     * 多选
     */
    public void setSelect(List<Integer> list) {
        for (int i = 0; i < isSelect.length; i++) {
//			if (list.contains(i)) {
//				isSelect[i] = true;
//			} else {
//				isSelect[i] = false;
//			}
            isSelect[i] = list.contains(i);
        }
        notifyDataSetChanged();
    }

    /**
     * 单选
     */
    public void setSingleSelect(int index) {
        for (int i = 0; i < isSelect.length; i++) {
//            if (i == index) {
//                isSelect[i] = true;
//            } else {
//                isSelect[i] = false;
//            }
            isSelect[i] = i == index;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return array.length;
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
        TextView tv = new TextView(context);
        tv.setHeight((int) (30 * MyApplication.density));
        tv.setText(array[position]);
        tv.setGravity(Gravity.CENTER);

        if (isSelect[position]) {
            tv.setTextColor(context.getResources().getColor(R.color.red));
            tv.setBackgroundResource(R.drawable.tv_round_press);
        } else {
            tv.setTextColor(context.getResources().getColor(R.color.text_black));
            tv.setBackgroundResource(R.drawable.tv_round_normal);
        }

        return tv;
    }

}
