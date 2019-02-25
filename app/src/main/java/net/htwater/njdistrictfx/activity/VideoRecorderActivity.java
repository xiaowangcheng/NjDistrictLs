package net.htwater.njdistrictfx.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;


import net.htwater.njdistrictfx.R;
import net.htwater.njdistrictfx.bean.FileExInfo;
import net.htwater.njdistrictfx.core.Constants;
import net.htwater.njdistrictfx.util.CommonUtil;
import net.htwater.njdistrictfx.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2016/2/22.
 */
public class VideoRecorderActivity extends FragmentActivity implements SurfaceHolder.Callback {
    private Uri m_recVideoPath;
    private Uri m_RecVideoFile;
    private String m_tempFileName;

    private Button bt_start;// 开始录制按钮
    private Button bt_stop;// 停止录制按钮
    private Button bt_ok;//完成返回
    private Button bt_cancel;//取消返回
    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceView sv_video;// 显示视频的控件
    private SurfaceHolder surfaceHolder;
    /**
     * 是否正在录音中
     */
    private boolean bRecording = false;
    /**
     * 是否按确认退出
     */
    private boolean bOkBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_video_recorder_portrait);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        initView();
    }

    public void initView() {
        init_Uri_savepath();
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_stop = (Button) findViewById(R.id.bt_stop);
        bt_ok = (Button) findViewById(R.id.bt_ok);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        sv_video = (SurfaceView) findViewById(R.id.sv_video);
        SurfaceHolder holder = sv_video.getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRecording = true;
                if (null != m_tempFileName) {
                    CommonUtil.deleteFile(m_RecVideoFile.getPath());
                }
                startMediaRecorder();
                changeBtn();
            }
        });
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bRecording = false;
                if (null != mediarecorder) {
                    try {
                        if (null != m_RecVideoFile) {
                            mediarecorder.stop();
                            mediarecorder.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                changeBtn();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
                FileExInfo info = new FileExInfo();
                info.setPathName(m_RecVideoFile.getPath());
                info.setName(m_tempFileName + ".mp4");
                Bundle bundle = new Bundle();
                bundle.putSerializable("fileinfo", info);
                Intent resultIntent = new Intent();
                resultIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, resultIntent);
                bOkBack = true;
                finish();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
                finish();
            }
        });
    }

    private void stopRecord() {
        if (bRecording) {
            if (null != mediarecorder) {
                try {
                    if (null != m_RecVideoFile) {
                        mediarecorder.stop();
                        mediarecorder.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        bRecording = false;
    }

    private void changeBtn() {
        if (bRecording) {
            bt_start.setEnabled(false);
            bt_stop.setEnabled(true);
            bt_start.setText("录像中");
        } else {
            bt_start.setEnabled(true);
            bt_stop.setEnabled(false);
            bt_start.setText("开始");
        }
    }

    private void startMediaRecorder() {
        if (generateVideoFileUri(".mp4")) {
            mediarecorder = new MediaRecorder();// 创建mediarecorder对象
            Camera c = Camera.open();
            c.setDisplayOrientation(90);
            c.unlock();
            mediarecorder.setCamera(c);

            // 设置录制视频源为Camera(相机)
            mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
            mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置录制的视频编码h263 h264
            mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            // 获取相机参数配置类，设置输出分辨率
            CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            mediarecorder.setVideoSize(cProfile.videoFrameWidth, cProfile.videoFrameHeight);
            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            mediarecorder.setVideoFrameRate(cProfile.videoFrameRate);
            mediarecorder.setVideoEncodingBitRate(cProfile.videoBitRate);
            mediarecorder.setMaxDuration(20 * 1000);
            mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
            mediarecorder.setOrientationHint(90);
            // 设置视频文件输出的路径
            mediarecorder.setOutputFile(m_RecVideoFile.getPath());//输出文件



            try {
                // 准备录制
                mediarecorder.prepare();
                // 开始录制
                mediarecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceHolder = null;
    }

    /**
     * 产生随机视频文件名
     */
    private boolean generateVideoFileUri(String dotExt) {
        boolean bRtn = false;
        if (!(null == m_recVideoPath || m_recVideoPath.getPath().length() == 0)) {
            m_tempFileName = generateFileName();
            m_RecVideoFile = Uri.withAppendedPath(m_recVideoPath, m_tempFileName + dotExt);
            bRtn = true;
        }
        return bRtn;
    }

    /**
     * 产生随机文件名（日期+3位随机数）
     */
    private String generateFileName() {
//        return HtApplication.caseId + "_" + DateUtil.parseDate2String(new Date(), "yyyyMMddHHmmss");
        return DateUtil.parseDate2String(new Date(), "yyyyMMddHHmmss_") + CommonUtil.getRandom(3);
    }

    /**
     * 初始化保存目录
     */
    private void init_Uri_savepath() {
        boolean bRtn = CommonUtil.hasSDCard();
        if (bRtn) {
            String savePath = Environment.getExternalStorageDirectory().toString();
            savePath = savePath
                    + File.separator
                    + Constants.FILE_TEMP_URL
                    + File.separator + Constants.SAVE_VIDEO_URL + File.separator;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            m_recVideoPath = Uri.fromFile(file);
        }
    }

    @Override
    public void finish() {
        stopRecord();
        if (!bOkBack) {
            if (null != m_RecVideoFile) {
                CommonUtil.deleteFile(m_RecVideoFile.getPath());
            }
        }
        super.finish();
    }

    /**
     * 旋转数据
     *
     * @param dst
     *            目标数据
     * @param src
     *            源数据
     * @param srcWidth
     *            源数据宽
     * @param srcHeight
     *            源数据高
     */
    private void YV12RotateNegative90(byte[] dst, byte[] src, int srcWidth,
                                      int srcHeight) {
        int t = 0;
        int i, j;

        int wh = srcWidth * srcHeight;

        for (i = srcWidth - 1; i >= 0; i--) {
            for (j = srcHeight - 1; j >= 0; j--) {
                dst[t++] = src[j * srcWidth + i];
            }
        }

        for (i = srcWidth / 2 - 1; i >= 0; i--) {
            for (j = srcHeight / 2 - 1; j >= 0; j--) {
                dst[t++] = src[wh + j * srcWidth / 2 + i];
            }
        }

        for (i = srcWidth / 2 - 1; i >= 0; i--) {
            for (j = srcHeight / 2 - 1; j >= 0; j--) {
                dst[t++] = src[wh * 5 / 4 + j * srcWidth / 2 + i];
            }
        }

    }
}
