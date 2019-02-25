package net.htwater.njdistrictfx.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.util.DataUtil;

import org.greenrobot.eventbus.EventBus;


public class Widget {
    private final Context context;
    private final String name;
    public View contentView;

    public Widget(Context context, String name) {
        super();
        this.context = context;
        this.name = name;
    }

    public View getView() {
        View view = View.inflate(context, R.layout.layout_widget, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout);
        RelativeLayout close = (RelativeLayout) view.findViewById(R.id.close);
        RelativeLayout toTop = (RelativeLayout) view.findViewById(R.id.totop);
        RelativeLayout refresh = (RelativeLayout) view.findViewById(R.id.refresh);
        ImageView widgetIcon = (ImageView) view.findViewById(R.id.widgetIcon);
        View divider = view.findViewById(R.id.view);
        RelativeLayout topBar = (RelativeLayout) view.findViewById(R.id.topBar);

        switch (name) {
            case "weather":
                divider.setBackgroundColor(Color.parseColor("#5FE061"));
                break;
            /*case "air quality":
                widgetIcon.setBackgroundResource(R.mipmap.kqzl);
                divider.setBackgroundColor(Color.parseColor("#F7620B"));
                break;*/
            case "reservoir capacity":
                widgetIcon.setBackgroundResource(R.mipmap.skkr);
                break;
            case "rain":
                widgetIcon.setBackgroundResource(R.mipmap.yqgk);
                divider.setBackgroundColor(Color.parseColor("#CE6426"));
                break;
            case "yuliang":
                widgetIcon.setBackgroundResource(R.mipmap.yqgk);
                divider.setBackgroundColor(Color.parseColor("#F5A143"));
                break;
            /*case "water":
                widgetIcon.setBackgroundResource(R.mipmap.sqgk);
                divider.setBackgroundColor(Color.parseColor("#A458E9"));
                break;*/
            case "shuiwei":
                widgetIcon.setBackgroundResource(R.mipmap.sqgk);
                divider.setBackgroundColor(Color.parseColor("#5FE061"));
                break;
            case "drainage":
                widgetIcon.setBackgroundResource(R.mipmap.psgk);
                break;
            case "reservoir":
                widgetIcon.setBackgroundResource(R.mipmap.skgq);
                break;
            case "flood":
                widgetIcon.setBackgroundResource(R.mipmap.xqjb);
                divider.setBackgroundColor(Color.parseColor("#F7620B"));
                break;
            /*case "typhoon":
                widgetIcon.setBackgroundResource(R.mipmap.tfxx);
                divider.setBackgroundColor(Color.parseColor("#CE6426"));
                break;*/
            /*case "pump":
                widgetIcon.setBackgroundResource(R.mipmap.cwyb);
                divider.setBackgroundColor(Color.parseColor("#F5A143"));
                break;*/
            case "rota":
                divider.setBackgroundColor(Color.parseColor("#42AFB6"));
                break;
        }
        title.setText(DataUtil.getWidgetChineseName(name));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new String[]{"1", name});
            }
        });
        toTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new String[]{"0", name});
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });

        int margin = (int) (MyApplication.density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        if (null != contentView) {
            frameLayout.addView(contentView);
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) contentView
                    .getLayoutParams();
            params2.setMargins(margin, 0, margin, margin);
        }

        return view;
    }

    protected void refresh() {

    }

    protected void jump() {

    }
}
