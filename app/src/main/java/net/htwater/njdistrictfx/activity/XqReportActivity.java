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

public class XqReportActivity extends BaseActivity {

    private MapView mapView;
    private AMap aMap = null;
    AMapLocationClientOption mLocationOption = null;
    AMapLocationClient mLocationClient = null;

    /**
     * 上传文件隐藏目录
     */
    private Uri m_media_uploadpath = null;

    /**
     * 原文件Uri
     */
    private Uri m_upload_Uri = null;

    /**
     * 原文件压缩文件Uri
     */
    private Uri m_upload_Uri_ = null;

    /**
     * 源文件视频Uri
     */
    private Uri m_upload_video_Uri = null;

    /**
     * List对象 照片列表信息
     */
    private List<Map<String, Object>> m_photoitems = new ArrayList<Map<String, Object>>();

    /**
     * List对象 视频列表信息
     */
    private List<Map<String, Object>> m_videoitems = new ArrayList<Map<String, Object>>();

    /**
     * 照片
     */
    private ExpandedGridView ex_gv_photo;
    private ImageGridAdapter imageGridAdapter;

    /**
     * 视频
     */
    private ExpandedGridView ex_lv_video;
    private VideoExListAdapter videoExListAdapter;

    TextView tvLatLng;
    String taskid;
    String resid;

    TextView tvTime;
    Spinner spType;
    EditText etXqlb;
    EditText etAddress;
    EditText etPosition;
    EditText etSize;
    EditText etDescription;
    EditText etReason;
    EditText etPrediction;
    EditText etRange;
    EditText etContent;
    EditText etMethod;
    EditText etCommunication;
    EditText etMaterial;
    EditText etProgress;
    EditText etEffect;
    ImageView ivChoise;
    EditText etMems;

    RadioButton rbSsxq;
    RadioButton rbGcxq;

    String notices;

    TextView tvReport;
    String isEngineer = "0";

    String lat_now = "0";
    String lng_now = "0";
    String str_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_xq);

        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.back);
        tvLatLng = (TextView) findViewById(R.id.tv_latLng);
        LinearLayout llBtnPhoto = (LinearLayout) findViewById(R.id.ll_btn_photo);
        LinearLayout llBtnVideo = (LinearLayout) findViewById(R.id.ll_btn_video);
        tvReport = (TextView) findViewById(R.id.tv_report);
        final TextView tvTitle = (TextView) findViewById(R.id.title);
        final LinearLayout llXqKind = (LinearLayout) findViewById(R.id.ll_xqkind);

        tvTime = (TextView) findViewById(R.id.tv_time);
        spType = (Spinner) findViewById(R.id.sp_type);
        etXqlb = (EditText) findViewById(R.id.et_xqlb);
        etAddress = (EditText) findViewById(R.id.et_address);
        etPosition = (EditText) findViewById(R.id.et_position);
        etSize = (EditText) findViewById(R.id.et_size);
        etDescription = (EditText) findViewById(R.id.et_description);
        etReason = (EditText) findViewById(R.id.et_reason);
        etPrediction = (EditText) findViewById(R.id.et_prediction);
        etRange = (EditText) findViewById(R.id.et_range);
        etContent = (EditText) findViewById(R.id.et_content);
        etMethod = (EditText) findViewById(R.id.et_content);
        etCommunication = (EditText) findViewById(R.id.et_communication);
        etMaterial = (EditText) findViewById(R.id.et_material);
        etProgress = (EditText) findViewById(R.id.et_prosess);
        etEffect = (EditText) findViewById(R.id.et_effect);
        ivChoise = (ImageView) findViewById(R.id.iv_choise);
        etMems = (EditText) findViewById(R.id.et_mems);

        tvTitle.setText("险情上报");

//        long nowTime= System.currentTimeMillis()+ SharedPreferencesUtil.getDeltatime();
        long nowTime = System.currentTimeMillis();
        Date date = DateUtil.parseLong2Date(nowTime);
        tvTime.setText(DateUtil.parseDate2String(date, DateUtil.DATETIME_FORMAT2));

        Intent intent = getIntent();
        if (null != intent.getStringExtra("taskid")) {
            taskid = intent.getStringExtra("taskid");
            resid = intent.getStringExtra("resid");
        }

        ex_gv_photo = (ExpandedGridView) findViewById(R.id.ex_gv_photo);
        imageGridAdapter = new ImageGridAdapter(this, m_photoitems);
        ex_gv_photo.setAdapter(imageGridAdapter);
        ex_gv_photo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                if (m_photoitems.size() > position) {
                    String filepathTemp = "" + m_photoitems.get(position).get("imgsrc");
                    if (!filepathTemp.startsWith("file://")) {
                        filepathTemp = "file://" + filepathTemp;
                    }
                    Uri uri = Uri.parse(filepathTemp);//调用系统自带的播放器
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "image/*");
                    startActivity(intent);
                }
            }
        });

        ex_lv_video = (ExpandedGridView) findViewById(R.id.ex_lv_video);
        videoExListAdapter = new VideoExListAdapter(this, m_videoitems);
        ex_lv_video.setAdapter(videoExListAdapter);
        ex_lv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (m_videoitems.size() > position) {
                    String filepathTemp = "" + m_videoitems.get(position).get("filepath");
                    if (!filepathTemp.startsWith("file://")) {
                        filepathTemp = "file://" + filepathTemp;
                    }
                    Uri uri = Uri.parse(filepathTemp);//调用系统自带的播放器
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                }
            }
        });

        llBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoModel();
            }
        });

        llBtnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPickVideoOpt();
            }
        });

        getSaveMediaFileUri();

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(XqReportActivity.this);
                builder.setTitle("确认上报");
                builder.setMessage("上报前，请确认内容是否填写完毕");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "开始上报", Toast.LENGTH_LONG).show();
                        doReport();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        EventBus.getDefault().register(this);

        mapView = (MapView) findViewById(R.id.mv_map);

        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setLogoBottomMargin(-50);

        goMyPosition(new LatLng(32.060847, 118.769471));

        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && aMapLocation.getLatitude() > 0 && aMapLocation.getLongitude() > 0) {
                    aMap.clear();
                    aMapLocation.getLocationType();
                    double lat = aMapLocation.getLatitude();
                    double lng = aMapLocation.getLongitude();
                    lat_now = String.format("%.6f", lat);
                    lng_now = String.format("%.6f", lng);
                    tvLatLng.setText(String.format("%.6f", lat) + "," + String.format("%.6f", lng));
                    Log.i("myInfo", "lat=" + lat + ",lng=" + lng);
                    LatLng myLoc = new LatLng(lat, lng);
                    goMyPosition(myLoc);
                    Marker marker = aMap.addMarker(new MarkerOptions().position(myLoc).title("hello").snippet("DefaultMarker"));
                }
            }
        });
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(5000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();

    }

    public void choosePhotoModel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"拍照", "从相册选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        doPickPhotoOpt();
                        break;
                    case 1:
                        doPickPhotoOptFromAlbum();
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private int upload_photo = 0;
    private int upload_video = 0;

    private List<String> m_photo_filename_items = new ArrayList<>();
    private List<String> m_video_filename_items = new ArrayList<>();

    private void doReport() {

        /*if (etAddress.getText().toString().isEmpty() || etXqlb.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "险情类别，地点为必填项。本次上报未全部填写", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (m_photoitems.size() == 0 && m_videoitems.size() == 0) {
            Toast.makeText(getApplicationContext(), "提交数据...", Toast.LENGTH_SHORT).show();
            uploadInfo(getSubmitInfo());
        } else {
            if (m_photoitems.size() == 0) {
                upload_photo = 1;
            } else {
                uploadPhotos();
            }
            if (m_videoitems.size() == 0) {
                upload_video = 1;
            } else {
                uploadVideo();
            }
        }

    }

    /**
     * 上传视频
     */
    private void uploadVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String filtType = "video";
                int count = m_videoitems.size();
                String url = Constants.ServerIP + "/servlet/uploads_phone?type=video&mem_id=" + SharedPreferencesUtil.getUserName();
                String[] paths = new String[count];
                for (int i = 0; i < count; i++) {
                    Map<String, Object> map = m_videoitems.get(i);
                    paths[i] = (String) map.get("filepath");
                }
                try {
                    final String result = FileUtil.upLoadByCommonPost(url, paths);
//                    LogUtil.i("myInfo", result);
                    tvReport.post(new Runnable() {
                        @Override
                        public void run() {
                            handleUploadResponse(result, filtType);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    tvReport.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(false);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 上传照片
     */
    private void uploadPhotos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String filtType = "photo";
                int count = m_photoitems.size();
                String url = Constants.ServerURL + "apiDanimg?type=image&mem_id=" + SharedPreferencesUtil.getUserName();
                //String url = "http://192.168.15.205:8080/uploadFilesSpecifyPath!api";
                // String url =  "http://192.168.15.205:8080/servlet/uploads_phone?type=image&mem_id=" + SharedPreferencesUtil.getUserName();
                String[] paths = new String[count];
                for (int i = 0; i < count; i++) {
                    Map<String, Object> map = m_photoitems.get(i);
                    paths[i] = (String) map.get("filepath");
                }
                try {
                    final String result = FileUtil.upLoadByCommonPost(url, paths);
//                    LogUtil.i("myInfo", result);
                    tvReport.post(new Runnable() {
                        @Override
                        public void run() {
                            //handleUploadResponse(result, filtType);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    tvReport.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(false);
                        }
                    });
                }
            }
        }).start();
    }

    private void handleUploadResponse(String resultString, String filtType) {
        try {
            JSONObject jsonObject = new JSONObject(resultString);
            boolean success = jsonObject.getBoolean("result");
            if (success) {
                JSONArray piclist = jsonObject.getJSONArray("files");
                uploadFilename(piclist.toString(), filtType);
                if (upload_photo == 1 && upload_video == 1) {
                    uploadInfo(getSubmitInfo());
                }
            } else {
                showToast(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(false);
        }
    }

    private void showToast(boolean success) {
        if (success) {
            Toast.makeText(getApplicationContext(), "上传附件成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "上传附件失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFilename(String piclist, String filtType) {
        try {
            JSONArray jsonArray = new JSONArray(piclist);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String organme = jsonObject.getString("orgname");
                switch (filtType) {
                    case "photo":
                        for (int j = 0; j < m_photoitems.size(); j++) {
                            String m_orgname = m_photoitems.get(j).get("img") + "";
                            if ((m_orgname.substring(m_orgname.length() - organme.length())).equals(organme)) {
                                m_photo_filename_items.add(jsonObject.getString("filename"));
                            }
                        }
                        upload_photo = 1;
                        break;
                    case "video":
                        for (int j = 0; j < m_videoitems.size(); j++) {
                            String m_orgname = m_videoitems.get(j).get("filepath") + "";
                            if ((m_orgname.substring(m_orgname.length() - organme.length())).equals(organme)) {
                                m_video_filename_items.add(jsonObject.getString("filename"));
                            }
                        }
                        upload_video = 1;
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(false);
        }
    }

    private String getSubmitInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            String photostr = "";
            for (int i = 0; i < m_photo_filename_items.size(); i++) {
                photostr = photostr + m_photo_filename_items.get(i) + ",";

            }

            String videostr = "";
            for (int i = 0; i < m_video_filename_items.size(); i++) {
                videostr = videostr + m_video_filename_items.get(i) + ",";
            }

            if (photostr.length() != 0) {
                jsonObject.put("image", photostr);
            }
            if (videostr.length() != 0) {
                jsonObject.put("video", videostr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private void uploadInfo(final String result) {
        String url = Constants.ServerIP + "/reportXianqing!Public";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("mem_id", SharedPreferencesUtil.getUserName());
        params.addBodyParameter("task_id", taskid);
        params.addBodyParameter("res_id", resid);
        params.addBodyParameter("type", str_type);
        params.addBodyParameter("xqlb", etXqlb.getText() + "");
        params.addBodyParameter("address", etAddress.getText() + "");
        params.addBodyParameter("position", etPosition.getText() + "");
        params.addBodyParameter("size", etSize.getText() + "");
        params.addBodyParameter("description", etDescription.getText() + "");
        params.addBodyParameter("reason", etReason.getText() + "");
        params.addBodyParameter("prediction", etPrediction.getText() + "");
        params.addBodyParameter("range", etRange.getText() + "");
        params.addBodyParameter("content", etContent.getText() + "");
        params.addBodyParameter("method", etMethod.getText() + "");
        params.addBodyParameter("communication", etCommunication.getText() + "");
        params.addBodyParameter("material", etMaterial.getText() + "");
        params.addBodyParameter("progress", etProgress.getText() + "");
        params.addBodyParameter("effect", etEffect.getText() + "");
        params.addBodyParameter("isEngineer", isEngineer);
        params.addBodyParameter("coordinate", lng_now + "," + lat_now);
        params.addBodyParameter("notices", notices);
        params.addBodyParameter("file", result);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resultJsonObject = new JSONObject(result);
                    if (resultJsonObject.getBoolean("result")) {
                        Toast.makeText(getApplicationContext(), "上报成功", Toast.LENGTH_SHORT).show();
                        deleteAttachmentFiles();
                        finish();
                    } else {
                        m_photo_filename_items.clear();
                        m_video_filename_items.clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                m_photo_filename_items.clear();
                m_video_filename_items.clear();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void deleteAttachmentFiles() {
        for (int i = 0; i < m_photoitems.size(); i++) {
            CommonUtil.deleteFile(m_photoitems.get(i).get("img") + "");
            CommonUtil.deleteFile(m_photoitems.get(i).get("imgsrc") + "");
        }
        for (int i = 0; i < m_videoitems.size(); i++) {
            CommonUtil.deleteFile(m_videoitems.get(i).get("filepath") + "");
            CommonUtil.deleteFile(m_videoitems.get(i).get("srcfilepath") + "");
        }
    }


    private void goMyPosition(LatLng point) {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(point));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void doPickPhotoOptFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Constants.ACTIVITY_REQ_PICK_PHOTO);
    }

    protected void doPickVideoOpt() {
        Intent intent = new Intent(this, VideoRecorderActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_REQ_VIDEORECORDER);
    }

    private void doPickPhotoOpt() {
        if (generateUploadUri(".jpg")) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT >= 24) {
                File file_temp = new File(m_upload_Uri.getPath());
                Uri imageUri = FileProvider.getUriForFile(this, "net.htwater.njdistrictfx.fileProvider", file_temp);//这里进行替换uri的获得方式
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, m_upload_Uri);
            }
            startActivityForResult(intent, Constants.ACTIVITY_REQ_IMAGE_CAPTURE);
        }
    }

    /**
     * 产生新文件名
     */
    private boolean generateUploadUri(String dotExt) {
        boolean bRtn = false;
        if (!(null == m_media_uploadpath || m_media_uploadpath.getPath().length() == 0)) {
            String tempFileName = generateFileName();
            m_upload_Uri = Uri.withAppendedPath(m_media_uploadpath, tempFileName + dotExt);
            m_upload_Uri_ = Uri.withAppendedPath(m_media_uploadpath, "_" + tempFileName + dotExt);
            bRtn = true;
        }
        return bRtn;
    }

    /**
     * 产生随机文件名（日期+3位随机数）
     */
    private String generateFileName() {
        return DateUtil.parseDate2String(new Date(), "yyyyMMddHHmmss") + CommonUtil.getRandom(3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ACTIVITY_REQ_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    if (copyImageCapture()) {
                        addPhotoToGrid();
                    } else {
                        if (null != m_upload_Uri_) {
                            CommonUtil.deleteFile(m_upload_Uri_.getPath());
                        }
                        if (null != m_upload_Uri) {
                            CommonUtil.deleteFile(m_upload_Uri.getPath());
                        }
                        Toast.makeText(getApplicationContext(), "拍照失败", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Constants.ACTIVITY_REQ_PICK_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    if (generateUploadUri(".jpg")) {
                        copyPhotoSrc(data.getData());
                    } else {
                        if (null != m_upload_Uri_) {
                            CommonUtil.deleteFile(m_upload_Uri_.getPath());
                        }
                        if (null != m_upload_Uri) {
                            CommonUtil.deleteFile(m_upload_Uri.getPath());
                        }
                    }
                }
                break;
            case Constants.ACTIVITY_REQ_VIDEORECORDER:
                if (resultCode == Activity.RESULT_OK) {
                    FileExInfo info = (FileExInfo) data.getSerializableExtra("fileinfo");
                    doGetVideoFile(info.getPathName(), info.getName());
                }
                break;
            default:
                break;
        }
    }


    /**
     * 复制源文件到目标文件内，并进行压缩
     *
     * @param uri 源文件Url
     * @return boolean 是否成功
     */
    private boolean copyPhotoSrc(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String img_path = null;
        Cursor cursor = null;
        try {
            cursor = this.getContentResolver().query(uri, new String[]{
                    MediaStore.Audio.Media.DATA}, null, null, null);

            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToFirst();
                img_path = cursor.getString(0);
            }

            //小米手机将直接返回img_path,无法通过上面的代码获取cursor,需要特殊处理
            if (null == cursor && uri.toString().startsWith("file:")) {
                img_path = String.valueOf(uri).substring(6);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != cursor) {
                    cursor.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if (null != img_path) {
            File file = new File(img_path);
            try {
                CommonUtil.copyFile(file, new File(m_upload_Uri.getPath()));
                //文件压缩
                boolean bRtn = copyImageCapture();
                if (bRtn) {
                    addPhotoToGrid();
                }
                return bRtn;
            } catch (IOException e) {
                e.printStackTrace();
//                LogUtil.e("TaskPatrolActivity", e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取多媒体上传文件Url(隐藏文件夹)
     */
    private Uri getSaveMediaFileUri() {
        if (null == m_media_uploadpath || m_media_uploadpath.getPath().length() == 0) {
            init_Uri_media_uploadpath();
        }
        return null;
    }

    /**
     * 上传多媒体文件目录
     */
    private void init_Uri_media_uploadpath() {
        boolean bRtn = CommonUtil.hasSDCard();
        if (bRtn) {
            String savePath = Environment.getExternalStorageDirectory().toString();
            savePath = savePath
                    + File.separator
                    + Constants.FILE_TEMP_URL
                    + File.separator
                    + Constants.SAVE_MEDIA_FILE_URL
                    + File.separator;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            m_media_uploadpath = Uri.fromFile(file);
        }
    }

    /**
     * 视频文件
     */
    private void doGetVideoFile(String filepath, String filename) {
        if (!(StringUtil.isBlank(filepath) || StringUtil.isBlank(filename))) {
            if (isVideoFileExist(filename)) {
                Toast.makeText(getApplicationContext(), "文件已经存在,不能重复选择", Toast.LENGTH_LONG).show();
            } else {
                if (generateUploadVideoUri(filename)) {
                    boolean bRtn = false;
                    try {
                        bRtn = CommonUtil.copyFile(new File(filepath), new File(m_upload_video_Uri.getPath()));
                    } catch (IOException e) {
//                        LogUtil.e("TaskPatrolActivity", e.getMessage());
                    }
                    if (bRtn) {
                        Map<String, Object> mapVideo = new HashMap<String, Object>();
                        mapVideo.put("type", "video");
                        mapVideo.put("filepath", m_upload_video_Uri.getPath());//路径
                        mapVideo.put("filename", filename);//文件名
                        mapVideo.put("srcfilepath", filepath);//源路径
                        mapVideo.put("filesize", "" + FileSizeUtil.getFileOrFilesSize(m_upload_video_Uri.getPath(), FileSizeUtil.SIZETYPE_KB));
                        mapVideo.put("id", SharedPreferencesUtil.getUserName());
                        m_videoitems.add(mapVideo);
                        videoExListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    /**
     * 视频文件是否存在
     */
    private boolean isVideoFileExist(String filename) {
        boolean bRtn = false;
        for (Map<String, Object> map : m_videoitems) {
            if (filename.equals("" + map.get("filename"))) {
                bRtn = true;
                break;
            }
        }
        return bRtn;
    }

    /**
     * 产生新文件名(视频)
     */
    private boolean generateUploadVideoUri(String filename) {
        boolean bRtn = false;
        if (!(null == m_media_uploadpath || m_media_uploadpath.getPath().length() == 0)) {
            m_upload_video_Uri = Uri.withAppendedPath(m_media_uploadpath, filename);
            bRtn = true;
        }
        return bRtn;
    }

    /**
     * 图片
     */
    private boolean copyImageCapture() {
        if (null != m_upload_Uri) {
            File file = new File(m_upload_Uri.getPath());
            return file.exists() && CommonUtil.compressPhotoSrc(file, m_upload_Uri_.getPath());
        } else {
            return false;
        }
    }

    /**
     * 添加照片
     */
    private void addPhotoToGrid() {
//        Map<String, Object> mapPhoto = new HashMap<>();
//        mapPhoto.put("flag", "photo");
//        mapPhoto.put("img", m_upload_Uri_.getPath());//截图
//        mapPhoto.put("imgsrc", m_upload_Uri.getPath());//原图
//        mapPhoto.put("delflag", "1");//删除标志
//        mapPhoto.put("imgsize", "" + FileSizeUtil.getFileOrFilesSize(m_upload_Uri_.getPath(), FileSizeUtil.SIZETYPE_KB));
//        mapPhoto.put("imgsrcsize", "" + FileSizeUtil.getFileOrFilesSize(m_upload_Uri.getPath(), FileSizeUtil.SIZETYPE_KB));
//        mapPhoto.put("filesrcname", (new File(m_upload_Uri.getPath())).getName());
//        mapPhoto.put("id", SharedPreferencesUtil.getUserName());
//        m_photoitems.add(mapPhoto);
//        imageGridAdapter.notifyDataSetChanged();

        Map<String, Object> photoMap = new HashMap<>();
//        photoMap.put("type", "photo");
        photoMap.put("filepath", m_upload_Uri_.getPath());
        photoMap.put("srcfilepath", m_upload_Uri.getPath());
        photoMap.put("filename", (new File(m_upload_Uri.getPath())).getName());
//        photoMap.put("task_id", SharedpreferencesUtil.getNowTaskid());
        m_photoitems.add(photoMap);
        imageGridAdapter.notifyDataSetChanged();

    }

    @Subscribe
    public void onEvent(final DeleteAttachemntEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除?");
        builder.setTitle("提示");
        //添加AlertDialog.Builder对象的setPositiveButton()方法
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = event.getIndex();
                switch (event.getType()) {
                    case "photo":
                        m_photoitems.remove(index);
                        imageGridAdapter.notifyDataSetChanged();
                        break;
                    case "video":
                        m_videoitems.remove(index);
                        videoExListAdapter.notifyDataSetChanged();
                        break;
                }
                Toast.makeText(getApplicationContext(), "删除列表项", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回");
        builder.setMessage("未上报就返回，将丢失所有填报的内容。请确认是否返回");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
