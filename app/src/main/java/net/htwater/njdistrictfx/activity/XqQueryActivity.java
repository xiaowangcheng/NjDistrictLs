package net.htwater.njdistrictfx.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.adapter.ImageGridAdapter;
import net.htwater.njdistrictfx.adapter.VideoExListAdapter;
import net.htwater.njdistrictfx.bean.DeleteAttachemntEvent;
import net.htwater.njdistrictfx.bean.FileExInfo;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.DateUtil;
import net.htwater.njdistrictfx.util.FileSizeUtil;
import net.htwater.njdistrictfx.util.FileUtil;
import net.htwater.njdistrictfx.util.SharedPreferencesUtil;
import net.htwater.njdistrictfx.util.StringUtil;
import net.htwater.njdistrictfx.view.ExpandedGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XqQueryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_xq);
        RelativeLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XqQueryActivity.this.finish();
            }
        });

    }


}
