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
 * Created by lzy on 2018/3/22.工程信息-河道
 */
public class RiverActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    private JSONArray jsonArray = new JSONArray();
    private TextView filterTv1;
    private TextView filterTv2;
    private TextView filterTv3;
    private int filterIndex1 = 0;//筛选条件1序号
    private int filterIndex2 = 0;//筛选条件2序号
    private int filterIndex3 = 0;//筛选条件2序号
    private String[] array1 =null;// Constants.government;
    private String[] array2 =null;// Constants.riverTypes;
    private String[] array3 =null; //Constants.riverTypes;
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

        title.setText("河道");
        filterTv1.setText("街道：全部");
        filterTv2.setText("评价：全部");
        filterTv3.setText("分类：全部");
        filterLayout3.setVisibility(View.VISIBLE);
        filterLayout2.setVisibility(View.VISIBLE);
        filterLayout1.setOnClickListener(filterListener1);
        filterLayout2.setOnClickListener(filterListener2);
        filterLayout3.setOnClickListener(filterListener3);
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
        queryAdministra();
        queryRiverevalu();
        queryRiveruse();
        query();
    }

    // http://218.2.110.162:8080/njfx/Queryres!SLGC
    private void query() {
        final String s ="[{\"GQBM\":\"P139\",\"GQMC\":\"润玉河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"戴庄路至潘渡河\",\"RIVERLENGT\":\"0.5\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划拓长至0.9km\"},{\"GQBM\":\"P157\",\"GQMC\":\"关子沟\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"润玉小区至戴庄\",\"RIVERLENGT\":\"1.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P156\",\"GQMC\":\"天沟河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"关子沟至黄庄\",\"RIVERLENGT\":\"1.69\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P72\",\"GQMC\":\"友谊河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"引水河至朝阳河\",\"RIVERLENGT\":\"2.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P8\",\"GQMC\":\"南农河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"南农泵站至五合路\",\"RIVERLENGT\":\"6.6\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P4\",\"GQMC\":\"城南河\",\"ADMINISTRA\":\"顶山街道、研创园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"浦滨路至入江口\",\"RIVERLENGT\":\"2.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P48\",\"GQMC\":\"下坝塘\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"大坝头北侧—河北街\",\"RIVERLENGT\":\"0.48\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P47\",\"GQMC\":\"中坝塘\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"阳沟街北侧—大坝头西侧\",\"RIVERLENGT\":\"0.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P46\",\"GQMC\":\"上坝塘\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"调节闸—阳沟街西侧\",\"RIVERLENGT\":\"1.24\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P3\",\"GQMC\":\"朱家山河\",\"ADMINISTRA\":\"泰山街道、盘城街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"张堡至入江口\",\"RIVERLENGT\":\"17.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"}," +
                "{\"GQBM\":\"P82\",\"GQMC\":\"石头河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"铁路桥至入江口\",\"RIVERLENGT\":\"4.9\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P79\",\"GQMC\":\"金庄河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"冯墙桥至铁路桥\",\"RIVERLENGT\":\"1.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P77\",\"GQMC\":\"千斤河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"新化10组至下坝泵站\",\"RIVERLENGT\":\"3.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"河道现状都有，实际为一条河，规划分为2条河\"},{\"GQBM\":\"P45\",\"GQMC\":\"柳南河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"0.9\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P23\",\"GQMC\":\"定向河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦乌路至长江\",\"RIVERLENGT\":\"3.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P27\",\"GQMC\":\"七里河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"七里桥上游250米至入江口\",\"RIVERLENGT\":\"4.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P5\",\"GQMC\":\"马汊河\",\"ADMINISTRA\":\"盘城街道、葛塘街道、长芦街道、大厂街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"小头李至长江\",\"RIVERLENGT\":\"13.6\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P155\",\"GQMC\":\"居民点河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"东坝头东埂至南埂\",\"RIVERLENGT\":\"1.82\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P144\",\"GQMC\":\"潘渡河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"东坝头河头至潘渡泵站\",\"RIVERLENGT\":\"4.41\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P142\",\"GQMC\":\"三板跳河\",\"ADMINISTRA\":\"长芦街道\"," +
                "\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"耿庄至六玉线\",\"RIVERLENGT\":\"2.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P163\",\"GQMC\":\"红庙河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"红庙桥至红庙沿高\",\"RIVERLENGT\":\"2.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P153\",\"GQMC\":\"三教河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"三教泵站至沿高\",\"RIVERLENGT\":\"1.6\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P165\",\"GQMC\":\"永兴河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"沿高至潘渡河接头\",\"RIVERLENGT\":\"3.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P154\",\"GQMC\":\"小摆渡河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"太平桥闸至叶庄桥\",\"RIVERLENGT\":\"3.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P143\",\"GQMC\":\"通江集河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"滁堤至通江集闸\",\"RIVERLENGT\":\"3.27\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P145\",\"GQMC\":\"中心河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"永兴桥至刘营泵站\",\"RIVERLENGT\":\"6.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划河道\"},{\"GQBM\":\"P161\",\"GQMC\":\"九姜河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"沿高至九里埂泵站\",\"RIVERLENGT\":\"1.12\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P162\",\"GQMC\":\"刘摆渡河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"刘渡泵站至中心河\",\"RIVERLENGT\":\"3.6\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P133\",\"GQMC\":\"长丰河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"窑基电灌站（长丰河雨水泵站）至幸福庄电灌站\",\"RIVERLENGT\":\"5.37\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P131\",\"GQMC\":\"小营河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"长芦中学至沿河电灌站(园中路)\",\"RIVERLENGT\":\"3.23\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P130\",\"GQMC\":\"中心河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"湾北电灌站至许家窑（中心河泵站）\",\"RIVERLENGT\":\"2.84\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"}," +
                "{\"GQBM\":\"P129\",\"GQMC\":\"赵桥河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"赵桥雨水泵站至小河口（东环路）\",\"RIVERLENGT\":\"2.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P137\",\"GQMC\":\"岳子河\"," +
                "\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"滁河头至沿高路\",\"RIVERLENGT\":\"5.25\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P11\",\"GQMC\":\"五里村河（园杰河）\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦乌路至芝麻河\",\"RIVERLENGT\":\"1.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P10\",\"GQMC\":\"毛庄河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦乌路至芝麻河\",\"RIVERLENGT\":\"1.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P13\",\"GQMC\":\"胜利河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦乌路至芝麻河\",\"RIVERLENGT\":\"1.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P15\",\"GQMC\":\"中心河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"慧谷路至华富路\",\"RIVERLENGT\":\"0.28\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P93\",\"GQMC\":\"群英河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"万家坝路至百步桥\",\"RIVERLENGT\":\"4.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P121\",\"GQMC\":\"武庄撇洪河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"园西路至团结河\",\"RIVERLENGT\":\"0.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划大部分填埋，余下部分改称纵一路沟\"},{\"GQBM\":\"P122\",\"GQMC\":\"干字河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"原新桥中心路至北站\",\"RIVERLENGT\":\"0.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P117\",\"GQMC\":\"葛塘河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦六路至马汊河\",\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P97\",\"GQMC\":\"龙天河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"南信大至东方天河\",\"RIVERLENGT\":\"1.68\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P52\",\"GQMC\":\"石砌沟\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦新路至浦东北路\",\"RIVERLENGT\":\"1.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划改称石桥河，规划局部新建河道未建\"},{\"GQBM\":\"P50\",\"GQMC\":\"小柳河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦欣家园至浦东北路\",\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"}," +
                "{\"GQBM\":\"P62\",\"GQMC\":\"柳西河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"恒辉翡翠花园至浦东北路\",\"RIVERLENGT\":\"0.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P7\",\"GQMC\":\"芝麻河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"虎桥路至庙东路\",\"RIVERLENGT\":\"3.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P28\",\"GQMC\":\"丰子河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"七里河至城南河\",\"RIVERLENGT\":\"4.9\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P43\",\"GQMC\":\"中心河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"2.4\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划河道，现状有\"},{\"GQBM\":\"P31\",\"GQMC\":\"安置房南圩十字河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"兴浦路至定向河\",\"RIVERLENGT\":\"1.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P40\",\"GQMC\":\"北圩机沟\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"0.9\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P26\",\"GQMC\":\"北圩十字河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"定向河至兴浦路\",\"RIVERLENGT\":\"1.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"}," +
                "{\"GQBM\":\"P30\",\"GQMC\":\"商城西河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"丁家山河至定向河\",\"RIVERLENGT\":\"1.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P166\",\"GQMC\":\"东坝头\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"东坝头泵站至东坝头河头\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P141\",\"GQMC\":\"农场河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"潘庄路西南930米至西北209米至划子河\",\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P95\",\"GQMC\":\"天河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"天河桥至渡桥卫生院\",\"RIVERLENGT\":\"3.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划大部分填埋\"},{\"GQBM\":\"P14\",\"GQMC\":\"团结河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦滨路至横江大道\",\"RIVERLENGT\":\"1.18\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P6\",\"GQMC\":\"十里长河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"新合三组至芝麻河\",\"RIVERLENGT\":\"4.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P12\",\"GQMC\":\"五里河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"芝麻河至五里泵站\",\"RIVERLENGT\":\"2.46\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划未建河道\"},{\"GQBM\":\"P172\",\"GQMC\":\"大顶山泄洪沟\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"大顶山水库到朱家山河\",\"RIVERLENGT\":\"4.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P70\",\"GQMC\":\"吨粮河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"威尼斯1街区至新化14组\",\"RIVERLENGT\":\"2.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P68\",\"GQMC\":\"界河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"新化农科组-新化11组\",\"RIVERLENGT\":\"1.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P160\",\"GQMC\":\"刘营河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"沿高至中心河\",\"RIVERLENGT\":\"1.76\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P164\",\"GQMC\":\"岳姜河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"沿高至岳子河桥\",\"RIVERLENGT\":\"3.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P152\",\"GQMC\":\"皇厂河\",\"ADMINISTRA\":\"长芦街道\"," +
                "\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"皇厂河至沿高\",\"RIVERLENGT\":\"3.54\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P159\",\"GQMC\":\"夹华河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"红旗组至红卫红组\",\"RIVERLENGT\":\"0.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P158\",\"GQMC\":\"新犁河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"戴庄桥至郝家坝涵\",\"RIVERLENGT\":\"1.83\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P135\",\"GQMC\":\"山圩撇洪河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"槽坊河与四柳河交叉口至中心河\",\"RIVERLENGT\":\"2.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P118\",\"GQMC\":\"妯娌河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"马汊河至妯娌河       泵站\",\"RIVERLENGT\":\"5.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P120\",\"GQMC\":\"引水河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"赵家斗门至大蒋电灌站；黄庄至许山电站\",\"RIVERLENGT\":\"4.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P119\",\"GQMC\":\"大官塘河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"姜袁至马汊河\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P86\",\"GQMC\":\"华宝河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"万家坝路至朱家山河\",\"RIVERLENGT\":\"5.456\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划河道，未全部建成\"},{\"GQBM\":\"P102\",\"GQMC\":\"新民河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"3.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P59\",\"GQMC\":\"后河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"林庄桥至朱家山河\",\"RIVERLENGT\":\"3.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P88\",\"GQMC\":\"团结河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"马汊河农场至路陶涵洞\",\"RIVERLENGT\":\"3.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P2\",\"GQMC\":\"滁河\",\"ADMINISTRA\":\"盘城街道、葛塘街道、长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"朱家山河至头桥涵\n" +
                "四柳河头至黄庄涵\",\"RIVERLENGT\":\"29.45\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P137\",\"GQMC\":\"岳子河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"滁河头至沿高路\",\"RIVERLENGT\":\"5.25\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P34\",\"GQMC\":\"金汤河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"1.33\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P25\",\"GQMC\":\"丁家山河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"珍珠泉至浦厂门前\",\"RIVERLENGT\":\"1.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P71\",\"GQMC\":\"前进河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未监测\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"新华农科组至下坝泵站\",\"RIVERLENGT\":\"2.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P116\",\"GQMC\":\"井子河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"保桥至大横沟\",\"RIVERLENGT\":\"4.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P174\",\"GQMC\":\"天河\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"井子河到头桥站\",\"RIVERLENGT\":\"2.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P136\",\"GQMC\":\"划子口河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"黄庄涵至船闸\",\"RIVERLENGT\":\"7.75\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P90\",\"GQMC\":\"为民河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"东边圩桥至祝庄泵站\",\"RIVERLENGT\":\"1.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P66\",\"GQMC\":\"秃尾巴河\",\"ADMINISTRA\":\"泰山街道、沿江街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"中心路至上城路\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P57\",\"GQMC\":\"西河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":" +
                "\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"朱家山河泵站至浦东路\",\"RIVERLENGT\":\"1.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P61\",\"GQMC\":\"北河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"大华临江苑至大华香邑美颂\",\"RIVERLENGT\":\"1.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P56\",\"GQMC\":\"东河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"东河泵站至浦东路\",\"RIVERLENGT\":\"0.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P21\",\"GQMC\":\"镇南河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦珠路至东方红河\",\"RIVERLENGT\":\"1.23\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P32\",\"GQMC\":\"大新十字河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"跃进组至定向河\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P134\",\"GQMC\":\"南河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"通江集雨水泵站至金陵亨斯曼（疏港大道）\",\"RIVERLENGT\":\"5.07\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划改称高庄河，原有河道拓长至划子口河堤6.86km\"},{\"GQBM\":\"P173\",\"GQMC\":\"上横河\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"通江集河到南河\",\"RIVERLENGT\":\"1.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P113\",\"GQMC\":\"湛水河\",\"ADMINISTRA\":\"大厂街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"姚姜线能达速递西50米至长江\",\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P112\",\"GQMC\":\"焦洼河\",\"ADMINISTRA\":\"大厂街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"唐湛路与姚姜线交叉口至马汊河\"," +
                "\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P76\",\"GQMC\":\"朝阳河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"复兴6组至复兴泵站\",\"RIVERLENGT\":\"1.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P58\",\"GQMC\":\"猪市河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"金津浦铁路涵洞至东门泵站\",\"RIVERLENGT\":\"1.6\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P22\",\"GQMC\":\"东方红河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"七里河至浦口大道\",\"RIVERLENGT\":\"2.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P9\",\"GQMC\":\"巩固民主河\",\"ADMINISTRA\":\"研创园\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"立新路至芝麻河\",\"RIVERLENGT\":\"1.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P91\",\"GQMC\":\"胜天河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"胜天河桥至马汊河泵站\"," +
                "\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P92\",\"GQMC\":\"七一河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"五西组断头路至滁河泵站\",\"RIVERLENGT\":\"2.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P87\",\"GQMC\":\"老滁河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"卞家湾老坝至渡桥社区小庄组\",\"RIVERLENGT\":\"4.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P89\",\"GQMC\":\"东方天河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"张虎山路口至北站\",\"RIVERLENGT\":\"3.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P94\",\"GQMC\":\"跃进河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"落桥社区买庄组至板桥社区村部\",\"RIVERLENGT\":\"6.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P98\",\"GQMC\":\"永丰河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"老幼岗泵站至渡桥社区五组\",\"RIVERLENGT\":\"3.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P99\",\"GQMC\":\"永丰北河\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"1.4\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划现状河道\"},{\"GQBM\":\"P55\",\"GQMC\":\"双垄河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"创业河至奥迪4S店\",\"RIVERLENGT\":\"1.9\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P18\",\"GQMC\":\"临泉泄洪沟\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"临泉村部至西庄\",\"RIVERLENGT\":\"2.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P140\",\"GQMC\":\"东干沟\",\"ADMINISTRA\":\"长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"徐庄涵至皇厂河接头\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P17\",\"GQMC\":\"北十字河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"北起中心大道，南至规划临江路\",\"RIVERLENGT\":\"1.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P20\",\"GQMC\":\"镇北河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"丰子河至新闸口泵站\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P49\",\"GQMC\":\"引水河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"旭日上城至引水泵站\",\"RIVERLENGT\":\"2.6\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P19\",\"GQMC\":\"朱西河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"工大至七里河\",\"RIVERLENGT\":\"2.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P29\",\"GQMC\":\"珍珠河\"," +
                "\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"临泉村部至七里河\",\"RIVERLENGT\":\"4.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P83\",\"GQMC\":\"龙南河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"永新路至学府渠\",\"RIVERLENGT\":\"1.67\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P81\",\"GQMC\":\"学府渠\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"南大至江北大道\",\"RIVERLENGT\":\"1.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P85\",\"GQMC\":\"侨谊南河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"1.45\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划河道，现状为管涵\"},{\"GQBM\":\"P84\",\"GQMC\":\"侨谊河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"新锦湖路至江北大道\",\"RIVERLENGT\":\"0.885\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P80\",\"GQMC\":\"丰收河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"起重机厂至金庄站\",\"RIVERLENGT\":\"1.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P69\",\"GQMC\":\"京新河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"重度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"引水河至柳州东路\",\"RIVERLENGT\":\"0.4\",\"RIVERTYPE\":\"规划\",\"REMARKS\":\"规划河道尚有大部分未建\"},{\"GQBM\":\"P169\",\"GQMC\":\"京新河（规划）\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"柳州东路至朝阳河\",\"RIVERLENGT\":\"2.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P67\",\"GQMC\":\"狭子河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"京新1组-复兴4组\",\"RIVERLENGT\":\"2.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划填埋\"},{\"GQBM\":\"P54\",\"GQMC\":\"大众河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"东大路至朱家山河交汇处\",\"RIVERLENGT\":\"1.7\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P53\",\"GQMC\":\"创业河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"高新三河泵站至创业桥\",\"RIVERLENGT\":\"2.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P65\",\"GQMC\":\"铁路西沟\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"火炬南路至规划铁路西沟泵站\",\"RIVERLENGT\":\"2.2\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P51\",\"GQMC\":\"二阳沟\"," +
                "\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"双垄河至创业河\",\"RIVERLENGT\":\"1.4\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P170\",\"GQMC\":\"二阳沟（暗沟）\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"柳州路至创业河\",\"RIVERLENGT\":\"0.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P64\",\"GQMC\":\"五一河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"浦口河道泵站管理所至彩虹雅苑\",\"RIVERLENGT\":\"1.1\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P132\",\"GQMC\":\"窑基河\",\"ADMINISTRA\":\"化工园\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"全程物流至岳子河北岸刘营泵站北面（园中路）\",\"RIVERLENGT\":\"2.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P44\",\"GQMC\":\"胜利河\",\"ADMINISTRA\":\"顶山街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\" \",\"RIVERLENGT\":\"1.34\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划河道，现状有\"},{\"GQBM\":\"P1\",\"GQMC\":\"长江\",\"ADMINISTRA\":\"江浦街道、顶山街道、泰山街道、沿江街道、大厂街道、长芦街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"防洪河道\",\"RIVERLOCAT\":\"长江五桥至划子口闸\",\"RIVERLENGT\":\"48.17\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P108\",\"GQMC\":\"三号沟\",\"ADMINISTRA\":\"大厂街道\",\"RIVEREVALU\":\"黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"东方帝斯曼物流门对门沟处\",\"RIVERLENGT\":\"0.45\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P106\",\"GQMC\":\"一号沟\",\"ADMINISTRA\":\"大厂街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"凤凰南路与钢焦东路交叉口旁公厕处至长江\",\"RIVERLENGT\":\"0.82\"," +
                "\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P109\",\"GQMC\":\"四号沟\",\"ADMINISTRA\":\"大厂街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"高新公建中心\",\"RIVERLENGT\":\"1.08\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P114\",\"GQMC\":\"姜桥河\",\"ADMINISTRA\":\"大厂街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"入江口至兴达路\",\"RIVERLENGT\":\"0.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P100\",\"GQMC\":\"撇洪沟\",\"ADMINISTRA\":\"盘城街道\",\"RIVEREVALU\":\"非黑臭\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"陆黑路至朱家山河\",\"RIVERLENGT\":\"1.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P171\",\"GQMC\":\"葛塘河（暗沟）\",\"ADMINISTRA\":\"葛塘街道\",\"RIVEREVALU\":\"未知\",\"RIVERUSE\":\"未知\",\"RIVERLOCAT\":\"浦六路至欣乐路\",\"RIVERLENGT\":\"0.8\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P73\",\"GQMC\":\"民兵河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"天华东路至朝阳河\",\"RIVERLENGT\":\"1.5\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P74\",\"GQMC\":\"中心河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"未监测\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"天华北路至吨粮河\",\"RIVERLENGT\":\"1.3\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\"规划改称中心北河\"},{\"GQBM\":\"P63\",\"GQMC\":\"安业河\",\"ADMINISTRA\":\"泰山街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"桥北五组桥至八组泵站\",\"RIVERLENGT\":\"1.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"},{\"GQBM\":\"P75\",\"GQMC\":\"小外江河\",\"ADMINISTRA\":\"沿江街道\",\"RIVEREVALU\":\"轻度黑臭\",\"RIVERUSE\":\"排水河道\",\"RIVERLOCAT\":\"小外江泵站至工业园\",\"RIVERLENGT\":\"3.0\",\"RIVERTYPE\":\"现状\",\"REMARKS\":\" \"}]";
        progressDialog.show();
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
        /*progressDialog.show();
        String administra ="";
        String riverevalu ="";
        String riveruse ="";
        if(filterIndex1 !=0){
            administra = array1[filterIndex1];
        }
        if (filterIndex2 !=0){
            riverevalu = array2[filterIndex2];
        }
        if (filterIndex3 !=0){
            riveruse = array3[filterIndex3];
        }
         String url = Constants.ServerURL + "apiRiver?administra="+administra+"&riverevalu="+riverevalu+"&riveruse="+riveruse+"";
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
    private void queryAdministra(){
        final String s ="[{\"ADMINISTRA\":\"泰山街道、盘城街道\"},{\"ADMINISTRA\":\"盘城街道\"},{\"ADMINISTRA\":\"大厂街道\"},{\"ADMINISTRA\":\"江浦街道、顶山街道、泰山街道、沿江街道、大厂街道、长芦街道\"},{\"ADMINISTRA\":\"盘城街道、葛塘街道、长芦街道\"},{\"ADMINISTRA\":\"沿江街道\"},{\"ADMINISTRA\":\"盘城街道、葛塘街道、长芦街道、大厂街道\"},{\"ADMINISTRA\":\"泰山街道、沿江街道\"},{\"ADMINISTRA\":\"长芦街道\"},{\"ADMINISTRA\":\"化工园\"},{\"ADMINISTRA\":\"研创园\"},{\"ADMINISTRA\":\"顶山街道、研创园\"},{\"ADMINISTRA\":\"泰山街道\"},{\"ADMINISTRA\":\"顶山街道\"},{\"ADMINISTRA\":\"葛塘街道\"}]";

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
                            array1[i]=jsonObject.getString("ADMINISTRA");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },1000); // 延时1秒

        /*String url = Constants.ServerURL + "apiAdministra";
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
                            array1[i]=jsonObject.getString("ADMINISTRA");
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
    private void queryRiverevalu(){
        final String s ="[{\"RIVEREVALU\":\"非黑臭\"},{\"RIVEREVALU\":\"未监测\"},{\"RIVEREVALU\":\"重度黑臭\"},{\"RIVEREVALU\":\"黑臭\"},{\"RIVEREVALU\":\"未知\"},{\"RIVEREVALU\":\"轻度黑臭\"}]";

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
                            array2[i]=jsonObject.getString("RIVEREVALU");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        },1000); // 延时1秒

        String url = Constants.ServerURL + "apiRiverevalu";
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
                            array2[i]=jsonObject.getString("RIVEREVALU");
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
        MyApplication.mQueue.add(request);
    }
    private void queryRiveruse(){
        final String s ="[{\"RIVERUSE\":\"防洪河道\"},{\"RIVERUSE\":\"排水河道\"},{\"RIVERUSE\":\"未知\"}]";
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
                            array3[i]=jsonObject.getString("RIVERUSE");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },1000); // 延时1秒
        /*
        String url = Constants.ServerURL + "apiRiveruse";
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
                            array3[i]=jsonObject.getString("RIVERUSE");
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
        if (filterIndex1 == 0 && filterIndex2 == 0&& filterIndex3 == 0) {
            EventBus.getDefault().post(jsonArray);
            return;
        }
        JSONArray filterArray = new JSONArray();
        if (filterIndex1 != 0) {
            String key = "ADMINISTRA";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(key).contains(array1[filterIndex1])) {
                    filterArray.put(jsonObject);
                }
            }
        } else {
            filterArray = jsonArray;
        }
       /* JSONArray filterArray2 = new JSONArray();
        if (filterIndex2 != 0) {
            String key = "ENTYNM";
            for (int i = 0; i < filterArray.length(); i++) {
                JSONObject jsonObject = filterArray.getJSONObject(i);
                String typeValue = jsonObject.getString(key);
                if (typeValue.contains(array2[filterIndex2])) {
                    filterArray2.put(jsonObject);
                }
            }
        } else {
            filterArray2 = filterArray;
        }
        EventBus.getDefault().post(filterArray2);*/

        EventBus.getDefault().post(filterArray);
        curArray = filterArray;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RiverActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RiverActivity.this);
            builder.setItems(array2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv2.setText("评价：" + array2[i]);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(RiverActivity.this);
            builder.setItems(array3, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    filterTv3.setText("分类：" + array3[i]);
                    filterIndex3 = i;
                    query();

                }
            });
            builder.show();
        }
    };

}
