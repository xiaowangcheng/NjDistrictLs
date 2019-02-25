package net.htwater.njdistrictfx.core;

/**
 * Created by LZY on 2016/11/22.
 */
public class Constants {
    // ht数据中心的key
    public static final String APPID = "njfx_android";
    public static final String VIDEOAPPID = "njfx_phone_video";
    public static final String LDHSAPPID = "ldhs_nj";
    public static final String VIDEO_PACKAGE_NAME = "com.demo.njvideo";//新版net.htwater.smartvideo 旧版net.htwater.phone.swvideo
    public static final String LDHS_PACKAGE_NAME = "com.htwater.conference";
    //IP，公司地址122.227.159.82:8280，南京地址218.2.110.162:8080
    public static final String ServerIP = "http://218.2.110.162:80";
    public static final String ServerIPNew = "http://59.83.223.39:8091";
    public static final String ServerURL = ServerIPNew+"/northriverApp/jiangbeiapi/"; //http://59.83.223.39:8091/northriverApp/jiangbeiapi/
    //工程信息
    public static final int CONSTANS_RES = 0;
    public static final int CONSTANS_GATE = 1;
    public static final int CONSTANS_PUMP = 2;
    public static final int CONSTANS_DIKE = 3;
    public static final int CONSTANS_RIVER = 4;
    //水雨情
    public static final int CONSTANS_RIVER_WATER = 1;
    public static final int CONSTANS_TIDE = 2;
    public static final int CONSTANS_RES_WATER = 3;
    public static final int CONSTANS_RAIN = 4;
    public static final int CONSTANS_GATE_WATER = 5;
    public static final int CONSTANS_PUMP_WATER = 6;
    public static final int CONSTANS_FLOOD = 7;
    public static final int CONSTANS_FLOW = 8;
    //保存水雨情收藏站点的key
    public static final String CONSTANS_RAIN_FAVORITES_KEY = "rainfavorites";
    public static final String CONSTANS_PUMP_WATER_FAVORITES_KEY = "pumpwaterfavorites";
    public static final String CONSTANS_RIVER_WATER_FAVORITES_KEY = "riverwaterfavorites";
    public static final String CONSTANS_RES_WATER_FAVORITES_KEY = "reswaterfavorites";
    //功能模块
    // public static String[] functions = {"气象", "汛情", "工程", "办公", "视频", "会商"};
    //筛选条件数组
    public static final String[] districts = new String[]{"全部", "玄武", "秦淮", "建邺", "鼓楼", "雨花台", "栖霞", "江宁", "浦口", "六合", "溧水", "高淳", "江北新区"};
    public static final String[] simpledistricts = new String[]{"玄武", "秦淮", "建邺", "鼓楼", "雨花台", "栖霞", "江宁", "浦口", "六合", "溧水", "高淳", "江北新区"};
    public static final String[] government = new String[]{"全部", "南京市水利局", "栖霞区水利局", "雨花台区水利局", "江宁区水利局", "浦口区水利局", "六合区水利局", "溧水县水务局", "高淳县水务局", "江北新区水利局"};
    public static final String[] riverTypes = new String[]{"全部", "一级", "二级", "三级", "四级", "五级"};
    public static final String[] resTypes = new String[]{"全部", "中型", "小一型", "小二型"};
    public static final String[] gateTypes = new String[]{"全部", "涵", "闸"};
    public static final String[] pumpTypes = new String[]{"全部", "Ⅰ级", "Ⅱ级", "Ⅲ级", "Ⅳ级", "Ⅴ级"};
    public static final String[] shuixiTypes = new String[]{"全部", "长江干流及沿江水系", "秦淮河水系", "滁河水系", "水阳江水系", "太湖水系", "淮河水系"};
    public static final String[] fanghong = new String[]{"全部", "200年", "80年", "60年", "50年", "40年", "30年", "10年"};
    public static final String[] gcjb = new String[]{"全部", "1级", "2级", "3级", "4级", "5级"};
    public static final String[] zk = new String[]{"全部", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};


    public static final int ACTIVITY_REQ_PICK_PHOTO = 503;

    /**
     * 拍照
     */
    public static final int ACTIVITY_REQ_IMAGE_CAPTURE = 504;

    /**
     * 视频录像对话框消息ID
     */
    public static final int ACTIVITY_REQ_VIDEORECORDER = 505;

    /**
     * APP临时文件根目录
     */
    public static final String FILE_TEMP_URL = "njdistrictfx";

    /**
     * 多媒体上报目录
     */
    public static final String SAVE_MEDIA_FILE_URL = ".media";

    // 录像目录
    public static final String SAVE_VIDEO_URL = "video";


}
