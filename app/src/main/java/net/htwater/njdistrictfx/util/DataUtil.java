package net.htwater.njdistrictfx.util;

import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.core.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LZY on 2016/7/5.
 */
public class DataUtil {

    public static String weatherforecast ="weatherforecast";//天气预报
    public static String productsforecast ="productsforecast";//专业预报
    public static String radarenlarge ="radarenlarge";//气象云图
    public static String waterdiagram ="waterdiagram";//水系图

    /**
     * 获取首页插件中文名
     *
     * @param name
     * @return
     */
    public static String getWidgetChineseName(String name) {
        String x;
        switch (name) {
            case "weather":
                x = "天气预报";
                break;
            /*case "air quality":
                x = "空气质量";
                break;*/
            case "rain":
                x = "雨情概况";
                break;
           /* case "water":
                x = "水情预警";
                break;*/
            case "flood":
                x = "防汛简报";
                break;
            case "typhoon":
                x = "气象云图";
                break;
            case "yuliang":
                x = "分区雨量统计";
                break;
            case "shuiwei":
                x = "周边站点";
                break;
           /* case "pump":
                x = "主要泵站运行情况";
                break;*/
            case "rota":
                x = "防汛值班";
                break;
            /* case "emergency":
                x = "应急响应";
                break;
           case "shuiweiduibi":
                x = "历史同期水位对比";
                break;
            case "liuliangduibi":
                x = "历史同期流量对比";
                break;*/
            default:
                x = "";
        }
        return x;
    }

    /**
     * 获取所有插件list
     *
     * @return
     */
    public static List<String> getAllWidgetList() {
        List<String> list = new ArrayList<>();
        list.add("weather");
        //list.add("air quality");
        //list.add("water");
        list.add("flood");
        list.add("typhoon");
        //list.add("yuliang");
        list.add("shuiwei");
        //list.add("pump");
        //list.add("emergency");
        //list.add("shuiweiduibi");
        return list;
    }

    /**
     * 功能列表，加空项是为了界面显示的一致
     *
     * @param index
     * @return
     */
    public static List<String> getFunctionList(String index) {
        HashMap<String, List<String>> functionMap = new HashMap<>();

        List<String> list1 = new ArrayList<>();
        list1.add("天气信息");
        list1.add("短临降雨");
        list1.add("卫星云图");
        list1.add("雷达图");
        list1.add("台风信息");
        list1.add("降水预报");
        list1.add("三天天气图");
        functionMap.put("2", list1);

        List<String> list2 = new ArrayList<>();
        list2.add("点雨量");
        list2.add("面雨量");
        list2.add("水情");
        // list2.add("河道水情");
        list2.add("水库水情");
        // list2.add("水闸水情");
        // list2.add("泵站水情");
        list2.add("积淹点水情");
        // list2.add("水闸工情");
        list2.add("泵站工情");
        list2.add("周边站点");
        functionMap.put("3", list2);

        List<String> list3 = new ArrayList<>();
        list3.add("河道");
        list3.add("水库");
        list3.add("堤防");
        list3.add("水闸");
        list3.add("泵站");
        functionMap.put("4", list3);

        List<String> list4 = new ArrayList<>();
        list4.add("防汛预案");
        list4.add("防汛简报");
        // if (SharedPreferencesUtil.gettxl_qx()) {
        list4.add("通讯录");
        // }
        list4.add("公告栏");
        list4.add("防汛值班");
        list4.add("水系图");
        list4.add("调查评价成果");
        list4.add("险工险段上报");
        functionMap.put("5", list4);

        List<String> list5 = new ArrayList<>();
        list5.add("重要视频点");
        list5.add("流域视频点");
        list5.add("区域视频点");
        list5.add("来源视频点");
        list5.add("工程视频点");
        functionMap.put("6", list5);

        List<String> list6 = new ArrayList<>();
        list6.add("联动会商");
        functionMap.put("7", list6);

        return functionMap.get(index);
    }

    /**
     * 获取功能对应的图标id
     *
     * @param name
     * @return
     */
    public static Integer getIconId(String name) {
        int id;
        switch (name) {
            case "天气预报":
                id = R.mipmap.njfx_tqxx;
                break;
            case "专业预报":
                id = R.mipmap.njfx_tflj;
                break;
            case "气象云图":
                id = R.mipmap.njfx_wx;
                break;
            case "天气信息":
                id = R.mipmap.tqxx;
                break;
            case "降水预报":
                id = R.mipmap.jsyb;
                break;
            case "短临降雨":
                id = R.mipmap.dljy;
                break;
            case "卫星云图":
                id = R.mipmap.wxyt;
                break;
            case "雷达图":
                id = R.mipmap.ldt;
                break;
            case "台风信息":
                id = R.mipmap.tfxx;
                break;
            case "点雨量":
            case "三天天气图":
                id = R.mipmap.njfx_dyl;
                break;
            case "面雨量":
                id = R.mipmap.myl;
                break;
            case "水情":
            case "河道水情":
                id = R.mipmap.njfx_hdsq;
                break;
            case "水库水情":
                id = R.mipmap.sksq;
                break;
            case "泵站水情":
                id = R.mipmap.bzsq;
                break;
            case "积淹点水情":
                id = R.mipmap.njfx_skgq;
                break;
            case "水闸水情":
            case "水闸工情":
                id = R.mipmap.zmgq;
                break;
            case "泵站工情":
                id = R.mipmap.njfx_bzgq;
                break;
            case "防汛预案":
                id = R.mipmap.njfx_fxya;
                break;
            case "防汛简报":
                id = R.mipmap.njfx_fxjb;
                break;
            case "周边站点":
            case "河道":
                id = R.mipmap.njfx_hd;
                break;
            case "水库":
                id = R.mipmap.njfx_sk;
                break;
            case "堤防":
                id = R.mipmap.df;
                break;
            case "水闸":
                id = R.mipmap.sz;
                break;
            case "泵站":
            case "历史机泵":
                id = R.mipmap.njfx_bz;
                break;
            case "重要视频点":
                id = R.mipmap.zyspd;
                break;
            case "流域视频点":
                id = R.mipmap.lyspd;
                break;
            case "区域视频点":
                id = R.mipmap.qyspd;
                break;
            case "来源视频点":
                id = R.mipmap.lyspd2;
                break;
            case "工程视频点":
                id = R.mipmap.gcspd;
                break;
            case "联动会商":
                id = R.mipmap.ldhs;
                break;
            case "通讯录":
                id = R.mipmap.njfx_txl;
                break;
            case "公告栏":
                id = R.mipmap.ggl;
                break;
            case "防汛值班":
                id = R.mipmap.njfx_fxzb;
                break;
            case "水系图":
                id = R.mipmap.njfx_sxt;
                break;
            case "调查评价成果":
                id = R.mipmap.fxzb;
                break;
            case "险工险段上报":
                id = R.mipmap.func_xqsb;
                break;
            case "险工险段查询":
                id = R.mipmap.xq_query;
                break;
            default:
                id = R.mipmap.zhsl_empty;
        }
        return id;
    }

    /**
     * 获取url
     *
     * @param name
     * @return
     */
    public static String getURL(String name) {
        String url;
        switch (name) {
            case "flood":
            case "防汛简报":
                url = Constants.ServerIP + "/njscreen/report_phone.html";
                break;
            case "天气预报":
                url =SharedPreferencesUtil.getUrlVlaue(weatherforecast);
                // url = "http://wx.weather.com.cn/mweather/101190101.shtml#1";
                break;
            case "专业预报":
                url =SharedPreferencesUtil.getUrlVlaue(productsforecast);
                // url = "http://wx.weather.com.cn/mweather/101190101.shtml#1";
                break;
            case "气象云图":
                url =SharedPreferencesUtil.getUrlVlaue(radarenlarge);
                break;
            case "降水预报":
                url = "http://wx.weather.com.cn/jsyb/";
                break;
            case "typhoon":
                url =SharedPreferencesUtil.getUrlVlaue(radarenlarge);
                break;
            case "台风信息":
                url =  SharedPreferencesUtil.getUrlVlaue(weatherforecast);
                //url = "http://www.htwater.net:8080/typhoon/mobile/?location=%E5%8D%97%E4%BA%AC&latlng=32.068604,118.765057";
                //  url = "http://typhoon.weather.com.cn/gis/typhoon_full.shtml";
                break;

            case "雷达图":
                url = "http://218.2.110.162/njfxphone_page/htmls/NJleidatu.html";
                break;
            case "短临降雨":
                url = "http://218.2.110.162/njfxphone_page/htmls/duanlin.html";
                break;
            case "通讯录":
                url = "http://218.2.110.162:80/tongxunlu/index.html";
                break;
            case "防汛值班":
                url = "http://218.2.110.162/njfxphone_page/htmls/zhiban.html";
                break;
            case "duibi":
                url = "http://218.2.110.162/njfxphone_page/htmls/shouye.html";
                break;
            case "水位对比":
                url = "http://218.2.110.162/njfxphone_page/htmls/yearLine.html";
                break;
            case "三天天气图":
                url = "http://218.2.110.162/njfxphone_page/htmls/threeWeather.html";
                break;
            case "流量对比":
                url = "http://218.2.110.162/njfxphone_page/htmls/datonglstq.html";
                break;
            case "公告栏":
                url = "http://218.2.110.162/njfxphone_page/htmls/tzgg/list.html";
                break;
            case "水系图":
                url =SharedPreferencesUtil.getUrlVlaue(waterdiagram);
                //url = "http://218.2.110.162/njfx/modules/fengxian/fengxian.html";
                break;
            case "调查评价成果":
                url = "http://218.2.110.162/njfx/modules/fengxian/fengxian.html";
                break;
            case "险工险段上报":
                url = "http://218.2.110.162/njfx/modules/fengxian/fengxian.html";
                break;
            default:
                url = "http://about:blank";
        }
        return url;
    }

    public static List<String> getSettingList() {
        List<String> list = new ArrayList<>();
        list.add("更新日志");
        list.add("检查更新" + "(当前1)");
        list.add("二维码分享");
        return list;
    }

}
