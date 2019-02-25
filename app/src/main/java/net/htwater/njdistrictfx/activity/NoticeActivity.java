package net.htwater.njdistrictfx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.adapter.NoticeAdapter;
import net.htwater.njdistrictfx.bean.NoticeBean;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

import java.util.List;

public class NoticeActivity extends BaseActivity {
    private NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        RelativeLayout leftButton = (RelativeLayout) findViewById(R.id.leftButton);
        TextView title = (TextView) findViewById(R.id.title);
        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new NoticeAdapter(this);
        listView.setAdapter(adapter);

        title.setText("公告栏");
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        request();
    }

    private void request() {
        String url = Constants.ServerIP + "/njfx/queryFORNotice_List_APP!Fxyw";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (null == s || s.isEmpty()) {
                    return;
                }
                List<NoticeBean> list = new Gson().fromJson(s, new TypeToken<List<NoticeBean>>() {
                }.getType());
                adapter.setData(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        MyApplication.mQueue.add(request);
    }
}
