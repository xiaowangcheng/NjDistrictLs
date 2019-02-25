package net.htwater.njdistrictfx.activity.GCXX;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.activity.BaseActivity;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;
import net.htwater.njdistrictfx.fragment.ListAndMap.GcxxListFragment;
import net.htwater.njdistrictfx.fragment.ListAndMap.GcxxMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by lzy on 2018/3/22.工程信息-泵站
 */
public class PumpActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private JSONArray jsonArray = new JSONArray();
    private TextView filterTv1;
    private TextView filterTv2;
    private TextView filterTv3;
    private int filterIndex1 = 0;//筛选条件1序号
    private int filterIndex2 = 0;//筛选条件2序号
    private int filterIndex3 = 0;//筛选条件2序号
    private String[] array1 = null;//Constants.districts;
    private String[] array2 = null; //Constants.pumpTypes;
    private String[] array3 = null;//new String[]{"全部", "是", "否"};
    private JSONArray curArray = new JSONArray();//当前界面显示的数据（按名称过滤之前）
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcxx);

        RelativeLayout back = (RelativeLayout) findViewById(R.id.back);
        final TextView switchButton = (TextView) findViewById(R.id.switchButton);
        TextView title = (TextView) findViewById(R.id.title);
        RelativeLayout filterLayout1 = (RelativeLayout) findViewById(R.id.filterLayout1);
        filterTv1 = (TextView) findViewById(R.id.filterTv1);
        name = (EditText) findViewById(R.id.name);
        RelativeLayout filterLayout2 = (RelativeLayout) findViewById(R.id.filterLayout2);
        RelativeLayout filterLayout3 = (RelativeLayout) findViewById(R.id.filterLayout3);
        filterTv2 = (TextView) findViewById(R.id.filterTv2);
        filterTv3 = (TextView) findViewById(R.id.filterTv3);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在加载");

        title.setText("泵站");
        filterTv1.setText("街道：全部");
        filterTv2.setText("类别：全部");
        filterTv3.setText("是否重点泵站：全部");
        filterLayout1.setOnClickListener(filterListener1);
        filterLayout2.setOnClickListener(filterListener2);
        filterLayout3.setOnClickListener(filterListener3);
        filterLayout2.setVisibility(View.VISIBLE);
        filterLayout3.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final FragmentManager manager = getFragmentManager();
        final Fragment listFragment = new GcxxListFragment();
        final Fragment mapFragment = new GcxxMapFragment();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listFragment.isHidden()) {
                    manager.beginTransaction().hide(mapFragment).show(listFragment).commit();
                    switchButton.setText("地图");
                } else {
                    manager.beginTransaction().hide(listFragment).show(mapFragment).commit();
                    switchButton.setText("列表");
                }
            }
        });

        manager.beginTransaction().add(R.id.contentLayout, mapFragment).hide(mapFragment)
                .add(R.id.contentLayout, listFragment).commit();
        requestTownship();
        requestStationcategory();
        requestBoolkeystation();
        query();
    }

    // http://218.2.110.162:8080/njfx/Queryres!SLGC
    private void query() {
        progressDialog.show();
         final  String s ="[{\"GQBM\":\"140\",\"GQMC\":\"大桥四处泵站\",\"WATERFLOW\":\"1.04\",\"PUMPCOUNT\":\"0.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"泰山街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.116566\",\"LGTD\":\"118.730399\"},{\"GQBM\":\"2\",\"GQMC\":\"葛关路（宁启铁路）泵站\",\"WATERFLOW\":\"0.6\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"4.0\",\"MINWATERLEVEL\":\"2.8\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.248997\",\"LGTD\":\"118.738423\"},{\"GQBM\":\"3\",\"GQMC\":\"郑云（宁启铁路）泵站\",\"WATERFLOW\":\"0.4\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"3.5\",\"MINWATERLEVEL\":\"2.2\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.234633\",\"LGTD\":\"118.724137\"},{\"GQBM\":\"1\",\"GQMC\":\"1号沟雨水泵站\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"9.0\",\"MINWATERLEVEL\":\"8.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"大厂街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.202822\",\"LGTD\":\"118.754855\"},{\"GQBM\":\"4\",\"GQMC\":\"园西大道（宁启铁路）泵站\",\"WATERFLOW\":\"0.6\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"3.5\",\"MINWATERLEVEL\":\"2.5\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.240746\",\"LGTD\":\"118.728331\"},{\"GQBM\":\"5\",\"GQMC\":\"园西大道（宁淮高速）泵站\",\"WATERFLOW\":\"0.6\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"3.5\",\"MINWATERLEVEL\":\"2.5\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.245057\",\"LGTD\":\"118.721013\"},{\"GQBM\":\"6\",\"GQMC\":\"葛关路污水提升泵站\",\"WATERFLOW\":\"3.5\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"4.0\",\"MINWATERLEVEL\":\"2.8\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"大厂街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.250152\",\"LGTD\":\"118.737709\"},{\"GQBM\":\"7\",\"GQMC\":\"1号沟污水提升泵站\",\"WATERFLOW\":\"0.7\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"3.2\",\"MINWATERLEVEL\":\"2.0\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"大厂街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.204226\",\"LGTD\":\"118.75589\"},{\"GQBM\":\"8\",\"GQMC\":\"扬子1号污水提升泵站\",\"WATERFLOW\":\"0.7\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"3.0\",\"MINWATERLEVEL\":\"1.5\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"大厂街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大厂市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.20349\",\"LGTD\":\"118.754469\"},{\"GQBM\":\"9\",\"GQMC\":\"学府路泵站\",\"WATERFLOW\":\"0.61\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"3.0\",\"MINWATERLEVEL\":\"1.5\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"全新中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.175562\",\"LGTD\":\"118.681139\"},{\"GQBM\":\"10\",\"GQMC\":\"新锦湖路泵站\",\"WATERFLOW\":\"0.38\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"2.5\",\"MINWATERLEVEL\":\"1.2\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"全新中心\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"11\",\"GQMC\":\"火炬南路泵站\",\"WATERFLOW\":\"0.38\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"3.0\",\"MINWATERLEVEL\":\"1.8\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"全新中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.141109\",\"LGTD\":\"118.711202\"},{\"GQBM\":\"12\",\"GQMC\":\"星火南路泵站\",\"WATERFLOW\":\"0.58\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"4.0\",\"MINWATERLEVEL\":\"1.5\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"全新中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.133445\",\"LGTD\":\"118.705246\"},{\"GQBM\":\"13\",\"GQMC\":\"星火北路泵站\",\"WATERFLOW\":\"1.66\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"3.5\",\"MINWATERLEVEL\":\"1.5\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"全新中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.197567\",\"LGTD\":\"118.689049\"},{\"GQBM\":\"14\",\"GQMC\":\"1号污水提升泵站\n" +
                "高科一路与新科四路交汇处\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.16682\",\"LGTD\":\"118.709224\"},{\"GQBM\":\"15\",\"GQMC\":\"2号污水提升泵站\n" +
                "学府路与永锦路交汇处\",\"WATERFLOW\":\"0.1\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.176311\",\"LGTD\":\"118.683651\"},{\"GQBM\":\"16\",\"GQMC\":\"3号污水提升泵站\n" +
                "高新区成贤学院站3号出口\",\"WATERFLOW\":\"0.4\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.159077\",\"LGTD\":\"118.7033\"},{\"GQBM\":\"17\",\"GQMC\":\"南大污水提升泵站\n" +
                "南京大学金陵学院内翠坪路\",\"WATERFLOW\":\"0.1\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.176513\",\"LGTD\":\"118.702947\"},{\"GQBM\":\"18\",\"GQMC\":\"绿苑路污水提升泵站\n" +
                "陆军指挥学院前龙蟠路上\",\"WATERFLOW\":\"0.1\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.172715\",\"LGTD\":\"118.665239\"},{\"GQBM\":\"19\",\"GQMC\":\"解放路污水提升泵站\n" +
                "永泰路\",\"WATERFLOW\":\"0.05\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"20\",\"GQMC\":\"永锦路污水提升泵站\n" +
                "永锦路海源药业对面\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.18796\",\"LGTD\":\"118.686785\"},{\"GQBM\":\"21\",\"GQMC\":\"旭日学府污水提升泵站\n" +
                "学府渠与江北大道交叉口\",\"WATERFLOW\":\"0.6\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.176274\",\"LGTD\":\"118.712856\"},{\"GQBM\":\"22\",\"GQMC\":\"星火路提升泵站1\n" +
                "星火路与东大路交叉口\",\"WATERFLOW\":\"0.1\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.152036\",\"LGTD\":\"118.692514\"},{\"GQBM\":\"23\",\"GQMC\":\"星火路提升泵站2\n" +
                "星火路星火E立方\",\"WATERFLOW\":\"0.48\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.196054\",\"LGTD\":\"118.690808\"},{\"GQBM\":\"24\",\"GQMC\":\"朱家山河沿线污水截流提升泵站1\n" +
                "浦泗路桥南侧\",\"WATERFLOW\":\"0.036\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.167028\",\"LGTD\":\"118.681697\"},{\"GQBM\":\"25\",\"GQMC\":\"朱家山河沿线污水截流提升泵站2\n" +
                "龙泰路高科大桥东侧\",\"WATERFLOW\":\"0.096\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.170256\",\"LGTD\":\"118.676739\"},{\"GQBM\":\"26\",\"GQMC\":\"朱家山河沿线污水截流提升泵站3\n" +
                "绿苑路\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"27\",\"GQMC\":\"高新污水处理厂中水回用提升泵站\n" +
                "惠达路\",\"WATERFLOW\":\"0.119\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.165439\",\"LGTD\":\"118.683416\"},{\"GQBM\":\"28\",\"GQMC\":\"高新区北部污水处理厂一号提升泵站\n" +
                "高科十二路与新科十三路交叉口\",\"WATERFLOW\":\"4.0\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.20121\",\"LGTD\":\"118.683668\"},{\"GQBM\":\"29\",\"GQMC\":\"万家坝路污水提升泵站\n" +
                "万家坝路与永锦路交叉口\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"未知\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"污水泵站\",\"STATIONTYPE\":\"污水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.212442\",\"LGTD\":\"118.710245\"},{\"GQBM\":\"30\",\"GQMC\":\"高新区三河泵站\",\"WATERFLOW\":\"3.1\",\"PUMPCOUNT\":\"8.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"高欣水务\",\"REMARKS\":\"null\",\"LTTD\":\"32.118291\",\"LGTD\":\"118.704099\"},{\"GQBM\":\"31\",\"GQMC\":\"金汤河泵站\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"7.8\",\"MINWATERLEVEL\":\"6.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.121289\",\"LGTD\":\"118.690527\"},{\"GQBM\":\"32\",\"GQMC\":\"西河泵站（泰山街道）\",\"WATERFLOW\":\"7.5\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.8\",\"MINWATERLEVEL\":\"4.3\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.116672\",\"LGTD\":\"118.710821\"},{\"GQBM\":\"33\",\"GQMC\":\"东门泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"8.75\",\"MINWATERLEVEL\":\"7.3\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.139542\",\"LGTD\":\"118.703521\"},{\"GQBM\":\"34\",\"GQMC\":\"东河泵站\",\"WATERFLOW\":\"4.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.77\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.1125\",\"LGTD\":\"118.719506\"},{\"GQBM\":\"35\",\"GQMC\":\"东河泵站\",\"WATERFLOW\":\"5.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.8\",\"MINWATERLEVEL\":\"4.3\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.1125\",\"LGTD\":\"118.719506\"},{\"GQBM\":\"36\",\"GQMC\":\"桥北泵站\",\"WATERFLOW\":\"0.75\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.9\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.145138\",\"LGTD\":\"118.714906\"}," +
                "{\"GQBM\":\"37\",\"GQMC\":\"临江泵站（泰山街道）\",\"WATERFLOW\":\"6.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.8\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.115225\",\"LGTD\":\"118.724435\"},{\"GQBM\":\"38\",\"GQMC\":\"金庄泵站\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"7.25\",\"MINWATERLEVEL\":\"5.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"沿江街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.16752\",\"LGTD\":\"118.719758\"},{\"GQBM\":\"39\",\"GQMC\":\"复兴泵站\",\"WATERFLOW\":\"12.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"2.5\",\"MINWATERLEVEL\":\"2.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"沿江街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.167711\",\"LGTD\":\"118.728591\"},{\"GQBM\":\"40\",\"GQMC\":\"引水河泵站\",\"WATERFLOW\":\"24.0\",\"PUMPCOUNT\":\"6.0\",\"MAXWATERLEVEL\":\"7.5\",\"MINWATERLEVEL\":\"5.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.137062\",\"LGTD\":\"118.746618\"},{\"GQBM\":\"41\",\"GQMC\":\"联合泵站\",\"WATERFLOW\":\"12.0\",\"PUMPCOUNT\":\"5.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"江浦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.048219\",\"LGTD\":\"118.643736\"},{\"GQBM\":\"42\",\"GQMC\":\"双涵泵站\",\"WATERFLOW\":\"12.0\",\"PUMPCOUNT\":\"5.0\",\"MAXWATERLEVEL\":\"5.9\",\"MINWATERLEVEL\":\"4.9\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.083991\",\"LGTD\":\"118.663754\"},{\"GQBM\":\"43\",\"GQMC\":\"丰子河泵站\",\"WATERFLOW\":\"1.08\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"2.7\",\"MINWATERLEVEL\":\"1.5\",\"BOOLAUTOOPEN\":\"是\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"雨水泵站\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"华明市政公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.062125\",\"LGTD\":\"118.654456\"},{\"GQBM\":\"44\",\"GQMC\":\"大桥泵站\",\"WATERFLOW\":\"1.5\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"未知\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"泰山街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.131004\",\"LGTD\":\"118.69778\"},{\"GQBM\":\"45\",\"GQMC\":\"大桥社区泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"未知\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"大桥社区\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"46\",\"GQMC\":\"东二泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"2.5\",\"MINWATERLEVEL\":\"1.5\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"花旗村\",\"REMARKS\":\"null\",\"LTTD\":\"32.185427\",\"LGTD\":\"118.645669\"},{\"GQBM\":\"47\",\"GQMC\":\"板桥泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"花旗村\",\"REMARKS\":\"null\",\"LTTD\":\"32.173213\",\"LGTD\":\"118.643862\"},{\"GQBM\":\"48\",\"GQMC\":\"港桥泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"泰山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"花旗村\",\"REMARKS\":\"null\",\"LTTD\":\"32.166685\",\"LGTD\":\"118.636333\"},{\"GQBM\":\"49\",\"GQMC\":\"大外江泵站\",\"WATERFLOW\":\"3.3\",\"PUMPCOUNT\":\"6.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"沿江街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\"," +
                "\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"沿江街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.158551\",\"LGTD\":\"118.756369\"},{\"GQBM\":\"50\",\"GQMC\":\"下坝泵站\",\"WATERFLOW\":\"4.5\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"沿江街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"沿江街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.182934\",\"LGTD\":\"118.746727\"},{\"GQBM\":\"51\",\"GQMC\":\"农场泵站\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.192535\",\"LGTD\":\"118.945764\"},{\"GQBM\":\"52\",\"GQMC\":\"东坝头泵站\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.196938\",\"LGTD\":\"118.938607\"},{\"GQBM\":\"53\",\"GQMC\":\"临江泵站（长芦街道）\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.187022\",\"LGTD\":\"118.904651\"},{\"GQBM\":\"54\",\"GQMC\":\"三教泵站\",\"WATERFLOW\":\"2.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.210055\",\"LGTD\":\"118.926291\"},{\"GQBM\":\"55\",\"GQMC\":\"红庙泵站\",\"WATERFLOW\":\"0.5\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.223812\",\"LGTD\":\"118.920552\"},{\"GQBM\":\"56\",\"GQMC\":\"团洲泵站\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.238319\"," +
                "\"LGTD\":\"118.91612\"},{\"GQBM\":\"57\",\"GQMC\":\"团洲新泵站\",\"WATERFLOW\":\"2.0\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.237143\",\"LGTD\":\"118.90643\"},{\"GQBM\":\"58\",\"GQMC\":\"潘渡泵站\",\"WATERFLOW\":\"6.03\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.228775\",\"LGTD\":\"118.903134\"},{\"GQBM\":\"59\",\"GQMC\":\"刘摆渡泵站\",\"WATERFLOW\":\"3.43\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.235391\",\"LGTD\":\"118.88954\"},{\"GQBM\":\"60\",\"GQMC\":\"皇厂河老站\",\"WATERFLOW\":\"1.5\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.246115\",\"LGTD\":\"118.879152\"},{\"GQBM\":\"61\",\"GQMC\":\"皇厂河泵站\",\"WATERFLOW\":\"6.03\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.246789\",\"LGTD\":\"118.879106\"},{\"GQBM\":\"62\",\"GQMC\":\"刘云泵站\",\"WATERFLOW\":\"6.03\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.254056\",\"LGTD\":\"118.848687\"},{\"GQBM\":\"64\",\"GQMC\":\"郝家坝泵站\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"3.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.214146\",\"LGTD\":\"118.856618\"},{\"GQBM\":\"67\",\"GQMC\":\"长芦沿河泵站\",\"WATERFLOW\":\"1.1\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.259956\",\"LGTD\":\"118.854475\"},{\"GQBM\":\"68\",\"GQMC\":\"赵桥河西雨水泵站\",\"WATERFLOW\":\"15.0\",\"PUMPCOUNT\":\"7.0\",\"MAXWATERLEVEL\":\"4.8\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"化工园公用事业公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.281602\",\"LGTD\":\"118.80981\"},{\"GQBM\":\"69\",\"GQMC\":\"长丰河雨水泵站\",\"WATERFLOW\":\"25.0\",\"PUMPCOUNT\":\"8.0\",\"MAXWATERLEVEL\":\"4.8\",\"MINWATERLEVEL\":\"4.2\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"化工园公用事业公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.250949\",\"LGTD\":\"118.829252\"},{\"GQBM\":\"70\",\"GQMC\":\"中心河雨水泵站\"," +
                "\"WATERFLOW\":\"30.0\",\"PUMPCOUNT\":\"7.0\",\"MAXWATERLEVEL\":\"4.8\",\"MINWATERLEVEL\":\"4.2\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"化工园公用事业公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.270932\",\"LGTD\":\"118.844571\"},{\"GQBM\":\"71\",\"GQMC\":\"通江集雨水泵站\",\"WATERFLOW\":\"30.0\",\"PUMPCOUNT\":\"9.0\",\"MAXWATERLEVEL\":\"4.8\",\"MINWATERLEVEL\":\"3.8\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"化工园公用事业公司\",\"REMARKS\":\"null\",\"LTTD\":\"32.20579\",\"LGTD\":\"118.867754\"},{\"GQBM\":\"72\",\"GQMC\":\"坝子窑泵站\",\"WATERFLOW\":\"36.0\",\"PUMPCOUNT\":\"7.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.085933\",\"LGTD\":\"118.700039\"},{\"GQBM\":\"73\",\"GQMC\":\"石佛农场泵站\",\"WATERFLOW\":\"26.0\",\"PUMPCOUNT\":\"7.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.079173\",\"LGTD\":\"118.67224\"},{\"GQBM\":\"74\",\"GQMC\":\"五里泵站\",\"WATERFLOW\":\"30.0\",\"PUMPCOUNT\":\"7.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"江浦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"新区公建中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.016299\",\"LGTD\":\"118.64117\"},{\"GQBM\":\"75\",\"GQMC\":\"黄狼泵站\",\"WATERFLOW\":\"20.0\",\"PUMPCOUNT\":\"5.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.08228\",\"LGTD\":\"118.668288\"},{\"GQBM\":\"76\",\"GQMC\":\"新闸口泵站\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.051874\",\"LGTD\":\"118.664236\"},{\"GQBM\":\"77\",\"GQMC\":\"胜利泵站\",\"WATERFLOW\":\"6.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.058519\",\"LGTD\":\"118.673301\"},{\"GQBM\":\"78\",\"GQMC\":\"四方沟泵站\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.057229\",\"LGTD\":\"118.671819\"},{\"GQBM\":\"79\",\"GQMC\":\"横江大道（原临江路）隧道泵房\",\"WATERFLOW\":\"1.0\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"5.5\",\"MINWATERLEVEL\":\"4.5\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"省水建\",\"REMARKS\":\"null\",\"LTTD\":\"32.064203\",\"LGTD\":\"118.656322\"},{\"GQBM\":\"80\",\"GQMC\":\"新民泵站（老站）\",\"WATERFLOW\":\"2.5\",\"PUMPCOUNT\":\"5.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.192026\"," +
                "\"LGTD\":\"118.643882\"},{\"GQBM\":\"81\",\"GQMC\":\"黑扎营泵站\",\"WATERFLOW\":\"6.0\",\"PUMPCOUNT\":\"9.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.211932\",\"LGTD\":\"118.640573\"},{\"GQBM\":\"82\",\"GQMC\":\"邵家斗门泵站\",\"WATERFLOW\":\"8.4\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.226495\",\"LGTD\":\"118.653824\"},{\"GQBM\":\"84\",\"GQMC\":\"老幼岗泵站\",\"WATERFLOW\":\"5.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.203571\",\"LGTD\":\"118.687757\"},{\"GQBM\":\"85\",\"GQMC\":\"北站\",\"WATERFLOW\":\"2.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌排\",\"BOOLKEYSTATION\":\"骨干灌排两用站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.228882\",\"LGTD\":\"118.699433\"},{\"GQBM\":\"83\",\"GQMC\":\"南站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"6.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.226271\",\"LGTD\":\"118.700073\"},{\"GQBM\":\"86\",\"GQMC\":\"祝庄站\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌排\",\"BOOLKEYSTATION\":\"骨干灌排两用站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.250548\",\"LGTD\":\"118.693585\"},{\"GQBM\":\"87\",\"GQMC\":\"马汊河泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.251815\",\"LGTD\":\"118.674463\"},{\"GQBM\":\"88\",\"GQMC\":\"滁河站\",\"WATERFLOW\":\"6.3\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"盘城街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"盘城街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.240862\",\"LGTD\":\"118.665653\"},{\"GQBM\":\"89\",\"GQMC\":\"一级站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"大厂街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"城市泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"未知\",\"MAINTENANCEUNIT\":\"和平社区/建设局\",\"REMARKS\":\"null\",\"LTTD\":\"32.230808\",\"LGTD\":\"118.782524\"},{\"GQBM\":\"90\",\"GQMC\":\"西庄泵站\",\"WATERFLOW\":\"2.1\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"顶山街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.108967\",\"LGTD\":\"118.668369\"},{\"GQBM\":\"91\",\"GQMC\":\"北圩泵站\",\"WATERFLOW\":\"1.8\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"顶山街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.114649\",\"LGTD\":\"118.681434\"},{\"GQBM\":\"92\",\"GQMC\":\"西河泵站（顶山街道）\",\"WATERFLOW\":\"0.8\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"顶山街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.114682\",\"LGTD\":\"118.680546\"},{\"GQBM\":\"93\",\"GQMC\":\"王楼泵站\",\"WATERFLOW\":\"2.3\",\"PUMPCOUNT\":\"5.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"顶山街道\",\"REMARKS\":\"null\",\"LTTD\":\"32.089527\",\"LGTD\":\"118.651724\"},{\"GQBM\":\"94\",\"GQMC\":\"南站泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"6.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":" +
                "\"顶山街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"顶山街道\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"95\",\"GQMC\":\"西埂站\",\"WATERFLOW\":\"4.4\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"6.0\",\"MINWATERLEVEL\":\"5.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.272688\",\"LGTD\":\"118.658341\"},{\"GQBM\":\"96\",\"GQMC\":\"万庄站\",\"WATERFLOW\":\"6.0\",\"PUMPCOUNT\":\"6.0\",\"MAXWATERLEVEL\":\"6.0\",\"MINWATERLEVEL\":\"5.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.254036\",\"LGTD\":\"118.676104\"},{\"GQBM\":\"97\",\"GQMC\":\"大横沟站\",\"WATERFLOW\":\"2.5\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"6.0\",\"MINWATERLEVEL\":\"5.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.252522\",\"LGTD\":\"118.693064\"},{\"GQBM\":\"98\",\"GQMC\":\"大横沟站（新）\",\"WATERFLOW\":\"8.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"6.0\",\"MINWATERLEVEL\":\"5.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.25251\",\"LGTD\":\"118.692731\"},{\"GQBM\":\"99\",\"GQMC\":\"前程站\",\"WATERFLOW\":\"4.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"8.0\",\"MINWATERLEVEL\":\"6.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.253144\",\"LGTD\":\"118.713675\"},{\"GQBM\":\"100\",\"GQMC\":\"北站\",\"WATERFLOW\":\"2.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"8.0\",\"MINWATERLEVEL\":\"6.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.228882\",\"LGTD\":\"118.699433\"},{\"GQBM\":\"101\",\"GQMC\":\"大史站\",\"WATERFLOW\":\"6.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"8.0\",\"MINWATERLEVEL\":\"6.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝站\",\"MAINTENANCEUNIT\":\"葛塘街道电灌站\",\"REMARKS\":\"null\",\"LTTD\":\"32.260117\",\"LGTD\":\"118.734651\"},{\"GQBM\":\"102\",\"GQMC\":\"黄马站\",\"WATERFLOW\":\"0.4\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/中山社区\",\"REMARKS\":\"null\",\"LTTD\":\"32.258144\",\"LGTD\":\"118.737131\"},{\"GQBM\":\"103\",\"GQMC\":\"大贾站\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/中山社区\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"104\",\"GQMC\":\"郑云站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"32.2353\",\"LGTD\":\"118.723705\"},{\"GQBM\":\"105\",\"GQMC\":\"七里站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"106\",\"GQMC\":\"张庄站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"107\",\"GQMC\":\"西埂站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"32.272688\",\"LGTD\":\"118.658341\"},{\"GQBM\":\"108\",\"GQMC\":\"王桥站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"109\",\"GQMC\":\"黄庄站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"110\",\"GQMC\":\"张庄站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"}," +
                "{\"GQBM\":\"111\",\"GQMC\":\"李云站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"112\",\"GQMC\":\"王西站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"113\",\"GQMC\":\"大桥站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"32.13491\",\"LGTD\":\"118.700337\"},{\"GQBM\":\"114\",\"GQMC\":\"大路站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"32.261285\",\"LGTD\":\"118.671736\"},{\"GQBM\":\"115\",\"GQMC\":\"高庄站\",\"WATERFLOW\":\"0.13\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"116\",\"GQMC\":\"仇庄站\",\"WATERFLOW\":\"0.13\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"}," +
                "{\"GQBM\":\"117\",\"GQMC\":\"余庄站\",\"WATERFLOW\":\"0.13\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"118\",\"GQMC\":\"谢庄站\",\"WATERFLOW\":\"0.13\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/前程村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"119\",\"GQMC\":\"殷圩站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":" +
                "\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/前程村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"120\",\"GQMC\":\"袁庄站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/前程村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"121\",\"GQMC\":\"万庄站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/前程村\",\"REMARKS\":\"null\",\"LTTD\":\"32.254036\",\"LGTD\":\"118.676104\"},{\"GQBM\":\"122\",\"GQMC\":\"大蒋站\",\"WATERFLOW\":\"2.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"骨干灌溉泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/接待寺社区\",\"REMARKS\":\"null\",\"LTTD\":\"32.277414\",\"LGTD\":\"118.70332\"},{\"GQBM\":\"123\",\"GQMC\":\"刘伏站\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\"," +
                "\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/接待寺社区\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"124\",\"GQMC\":\"大蒋二级站\",\"WATERFLOW\":\"0.6\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"骨干灌溉泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/接待寺社区\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"125\",\"GQMC\":\"小史站\",\"WATERFLOW\":\"0.7\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/接待寺社区\",\"REMARKS\":\"null\",\"LTTD\":\"32.270502\",\"LGTD\":\"118.73609\"},{\"GQBM\":\"126\",\"GQMC\":\"金黄站\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/接待寺社区\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"127\",\"GQMC\":\"张门站\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/接待寺社区\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"128\",\"GQMC\":\"童祝站\",\"WATERFLOW\":\"0.6\",\"PUMPCOUNT\":\"2.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"骨干灌溉泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/官塘河村\",\"REMARKS\":\"null\",\"LTTD\":\"32.290738\",\"LGTD\":\"118.713717\"},{\"GQBM\":\"129\",\"GQMC\":\"姜袁下站\",\"WATERFLOW\":\"0.5\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/官塘河村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"130\",\"GQMC\":\"姜袁站\",\"WATERFLOW\":\"0.3\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/官塘河村\",\"REMARKS\":\"null\",\"LTTD\":\"32.273725\",\"LGTD\":\"118.737526\"},{\"GQBM\":\"131\",\"GQMC\":\"潘庄站\",\"WATERFLOW\":\"0.13\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/官塘河村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"132\",\"GQMC\":\"小余站\",\"WATERFLOW\":\"0.13\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"葛塘街道/官塘河村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"133\",\"GQMC\":\"九里埂泵站\",\"WATERFLOW\":\"3.0\",\"PUMPCOUNT\":\"3.0\",\"MAXWATERLEVEL\":\"5.0\",\"MINWATERLEVEL\":\"4.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"32.248619\",\"LGTD\":\"118.826968\"},{\"GQBM\":\"134\",\"GQMC\":\"长滨泵站\",\"WATERFLOW\":\"0.0\",\"PUMPCOUNT\":\"4.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"null\",\"TOWNSHIP\":\"长芦街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"排涝\",\"BOOLKEYSTATION\":\"骨干排涝泵站\",\"MAINTENANCEUNIT\":\"长芦街道/水利管理服务中心\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"135\",\"GQMC\":\"武庄站\",\"WATERFLOW\":\"0.2\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"136\",\"GQMC\":\"后云站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"137\",\"GQMC\":\"史李站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"前程村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"138\",\"GQMC\":\"白庙站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"前程村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"},{\"GQBM\":\"139\",\"GQMC\":\"梁庄站\",\"WATERFLOW\":\"0.15\",\"PUMPCOUNT\":\"1.0\",\"MAXWATERLEVEL\":\"0.0\",\"MINWATERLEVEL\":\"0.0\",\"BOOLAUTOOPEN\":\"否\",\"TOWNSHIP\":\"葛塘街道\",\"RIVERWAY\":\"null\",\"STATIONCATEGORY\":\"农村泵站\",\"STATIONTYPE\":\"灌溉\",\"BOOLKEYSTATION\":\"非骨干泵站\",\"MAINTENANCEUNIT\":\"长城村\",\"REMARKS\":\"null\",\"LTTD\":\"null\",\"LGTD\":\"null\"}]";
        String township ="";
        String stationcategory ="";
        String boolkeystation ="";
        if(filterIndex1 !=0){
            township = array1[filterIndex1];
        }
        if (filterIndex2 !=0){
            stationcategory = array2[filterIndex2];
        }
        if (filterIndex3 !=0){
            boolkeystation = array3[filterIndex3];
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    EventBus.getDefault().post(jsonArray);
                    curArray = jsonArray;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒

       /* String url = Constants.ServerURL + "apiPump?township="+township+"&stationcategory="+stationcategory+"&boolkeystation="+boolkeystation+"";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    jsonArray = new JSONArray(s.replace("null", "—"));
                    EventBus.getDefault().post(jsonArray);
                    curArray = jsonArray;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);*/
    }

    /**
     * 请求街道接口
     */
    private void requestTownship(){
       final  String s ="[{\"TOWNSHIP\":\"葛塘街道\"},{\"TOWNSHIP\":\"未知\"},{\"TOWNSHIP\":\"大厂街道\"},{\"TOWNSHIP\":\"盘城街道\"},{\"TOWNSHIP\":\"江浦街道\"},{\"TOWNSHIP\":\"泰山街道\"},{\"TOWNSHIP\":\"顶山街道\"},{\"TOWNSHIP\":\"长芦街道\"},{\"TOWNSHIP\":\"沿江街道\"}]";
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    array1 = new String[jsonArray.length()+1];
                    for (int i=0;i<jsonArray.length()+1;i++){
                        if(i==0){
                            array1[i]="全部";
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i-1);
                            array1[i]=jsonObject.getString("TOWNSHIP");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒

        /*String url = Constants.ServerURL + "/apiTownship/";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.show();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    array1 = new String[jsonArray.length()+1];
                    for (int i=0;i<jsonArray.length()+1;i++){
                        if(i==0){
                            array1[i]="全部";
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i-1);
                            array1[i]=jsonObject.getString("TOWNSHIP");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);*/
    }

    private void requestStationcategory(){
        final   String s ="[{\"STATIONCATEGORY\":\"未知\"},{\"STATIONCATEGORY\":\"农村泵站\"},{\"STATIONCATEGORY\":\"污水泵站\"},{\"STATIONCATEGORY\":\"城市泵站\"}]";
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    array2 = new String[jsonArray.length()+1];
                    for (int i=0;i<jsonArray.length()+1;i++){
                        if(i==0){
                            array2[i]="全部";
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i-1);
                            array2[i]=jsonObject.getString("STATIONCATEGORY");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },1000); // 延时1秒
        /*
        String url = Constants.ServerURL + "/apiStationcategory/";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.show();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    array2 = new String[jsonArray.length()+1];
                    for (int i=0;i<jsonArray.length()+1;i++){
                        if(i==0){
                            array2[i]="全部";
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i-1);
                            array2[i]=jsonObject.getString("STATIONCATEGORY");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);*/
    }

    private void  requestBoolkeystation(){
        final String s ="[{\"Boolkeystation\":\"未知\"},{\"Boolkeystation\":\"骨干灌溉泵站\"},{\"Boolkeystation\":\"非骨干泵站\"},{\"Boolkeystation\":\"骨干排涝\"},{\"Boolkeystation\":\"骨干排涝站\"},{\"Boolkeystation\":\"骨干灌排两用站\"},{\"Boolkeystation\":\"骨干排涝泵站\"}]";
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    array3 = new String[jsonArray.length()+1];
                    for (int i=0;i<jsonArray.length()+1;i++){
                        if(i==0){
                            array3[i]="全部";
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i-1);
                            array3[i]=jsonObject.getString("Boolkeystation");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },1000); // 延时1秒

        /*
        String url = Constants.ServerURL + "/apiBoolkeystation/";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.show();
                if (null == s || s.equals("")) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(s.replace("null", "—"));
                    array3 = new String[jsonArray.length()+1];
                    for (int i=0;i<jsonArray.length()+1;i++){
                        if(i==0){
                            array3[i]="全部";
                        } else {
                            JSONObject jsonObject = jsonArray.getJSONObject(i-1);
                            array3[i]=jsonObject.getString("Boolkeystation");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        MyApplication.mQueue.add(request);*/
    }

    private void filter() throws JSONException {
        if (filterIndex1 == 0 && filterIndex2 == 0 && filterIndex3 == 0) {
            EventBus.getDefault().post(jsonArray);
            return;
        }


        JSONArray filterArray = new JSONArray();
        if (filterIndex1 != 0) {
            String key = "COUNTY";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(key).contains(array1[filterIndex1])) {
                    filterArray.put(jsonObject);
                }
            }
        } else {
            filterArray = jsonArray;
        }
        JSONArray filterArray2 = new JSONArray();
        if (filterIndex2 != 0) {
            String key = "ENCL";
            for (int i = 0; i < filterArray.length(); i++) {
                JSONObject jsonObject = filterArray.getJSONObject(i);
                String typeValue = jsonObject.getString(key);
                if (typeValue.contains(array2[filterIndex2].substring(0, 1))) {
                    filterArray2.put(jsonObject);
                }
            }
        } else {
            filterArray2 = filterArray;
        }
        EventBus.getDefault().post(filterArray2);
        curArray = filterArray2;

        name.setText("");
    }

    private void search(String x) {
        if (x.equals("")) {
            EventBus.getDefault().post(curArray);
            return;
        }
        try {
            JSONArray filterArray = new JSONArray();
            for (int i = 0; i < curArray.length(); i++) {
                JSONObject jsonObject = curArray.getJSONObject(i);
                if (jsonObject.getString("GQMC").contains(x)) {
                    filterArray.put(jsonObject);
                }
            }
            EventBus.getDefault().post(filterArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener filterListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PumpActivity.this);
            builder.setItems(array1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv1.setText("街道：" + array1[i]);
                    filterIndex1 = i;
                    query();
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener filterListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PumpActivity.this);
            builder.setItems(array2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv2.setText("类别：" + array2[i]);
                    filterIndex2 = i;
                    query();
                }
            });
            builder.show();
        }
    };

    private View.OnClickListener filterListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PumpActivity.this);
            builder.setItems(array3, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv3.setText("是否重点泵站：" + array3[i]);
                    filterIndex3 = i;
                    query();
                }
            });
            builder.show();
        }
    };

}
