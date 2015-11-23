package com.qiniu.qlive.activity.live;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingManager.EncodingType;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.FrameCapturedCallback;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;
import com.qiniu.qlive.activity.R;
import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.config.StreamQuality;
import com.qiniu.qlive.service.LiveStreamService;
import com.qiniu.qlive.service.result.StopPublishResult;
import com.qiniu.qlive.utils.AsyncRun;
import com.qiniu.qlive.utils.Tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SWCodecCameraStreamingActivity extends StreamingBaseActivity implements View.OnLayoutChangeListener, StreamQuality {
    private static final String TAG = "SWCodecCameraStreaming";
    private ImageButton mTorchBtn;
    private boolean mIsTorchOn = false;
    private ImageButton mCameraSwitchBtn;
    private ImageButton mCaptureFrameBtn;
    private StreamingProfile mProfile;
    private Context mContext;
    private View mRootView;
    private int mOrientation;
    private Switcher mSwitcher = new Switcher();
    private Screenshooter mScreenshooter = new Screenshooter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        mContext = this;

        int streamOrientation = this.getIntent().getIntExtra("stream_orientation", 0);
        switch (streamOrientation) {
            case 0:
                this.mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case 1:
                this.mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
        }

        //set orientation
        this.setRequestedOrientation(this.mOrientation);
        setContentView(R.layout.activity_camera_streaming);

        mRootView = findViewById(R.id.content);
        mRootView.addOnLayoutChangeListener(this);

        AspectFrameLayout afl = (AspectFrameLayout) findViewById(R.id.cameraPreview_afl);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);

        mShutterButton = (Button) findViewById(R.id.toggleRecording_button);

        mSatusTextView = (TextView) findViewById(R.id.streamingStatus);
        mTorchBtn = (ImageButton) findViewById(R.id.torch_btn);
        mCameraSwitchBtn = (ImageButton) findViewById(R.id.camera_switch_btn);
        mCaptureFrameBtn = (ImageButton) findViewById(R.id.capture_btn);

        StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
        mProfile = new StreamingProfile();

        int streamQuality = this.getIntent().getIntExtra("stream_quality", StreamQuality.LOW_QUALITY);

        int videoQuality = 0;
        int audioQuality = 0;
        int encodingLevel = 0;

        switch (streamQuality) {
            case LOW_QUALITY:
                videoQuality = StreamingProfile.VIDEO_QUALITY_LOW3;
                audioQuality = StreamingProfile.AUDIO_QUALITY_LOW1;
                encodingLevel = StreamingProfile.VIDEO_ENCODING_SIZE_QVGA;
                break;
            case STANDARD_QUALITY:
                videoQuality = StreamingProfile.VIDEO_QUALITY_MEDIUM3;
                audioQuality = StreamingProfile.AUDIO_QUALITY_MEDIUM2;
                encodingLevel = StreamingProfile.VIDEO_ENCODING_SIZE_VGA;
                break;
            case HIGH_QUALITY:
                videoQuality = StreamingProfile.VIDEO_QUALITY_HIGH1;
                audioQuality = StreamingProfile.AUDIO_QUALITY_HIGH1;
                encodingLevel = StreamingProfile.VIDEO_ENCODING_SIZE_HD;
                break;
            case SUPER_QUALITY:
                videoQuality = StreamingProfile.VIDEO_QUALITY_HIGH3;
                audioQuality = StreamingProfile.AUDIO_QUALITY_HIGH2;
                encodingLevel = StreamingProfile.VIDEO_ENCODING_SIZE_FHD;
                break;
        }

        //stream quality
        mProfile.setVideoQuality(videoQuality)
                .setAudioQuality(audioQuality)
                .setEncodingSizeLevel(encodingLevel)
                .setStream(stream)
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));

        CameraStreamingSetting setting = new CameraStreamingSetting();
        setting.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setContinuousFocusModeEnabled(true)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);

        mCameraStreamingManager = new CameraStreamingManager(this, afl, glSurfaceView, EncodingType.SW_VIDEO_WITH_HW_AUDIO_CODEC);  // soft codec
        mCameraStreamingManager.onPrepare(setting, mProfile);
        mCameraStreamingManager.setStreamingStateListener(this);
        mCameraStreamingManager.setNativeLoggingEnabled(true);

        mShutterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShutterButtonClick();
            }
        });

        mTorchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsTorchOn) {
                            mIsTorchOn = true;
                            mCameraStreamingManager.turnLightOn();
                        } else {
                            mIsTorchOn = false;
                            mCameraStreamingManager.turnLightOff();
                        }
                        setTorchEnabled(mIsTorchOn);
                    }
                }).start();
            }
        });

        mCameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.removeCallbacks(mSwitcher);
                mHandler.postDelayed(mSwitcher, 100);
            }
        });

        mCaptureFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.removeCallbacks(mScreenshooter);
                mHandler.postDelayed(mScreenshooter, 100);
            }
        });
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(this.mOrientation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        mHandler.removeCallbacksAndMessages(null);
        mSwitcher = null;
        mScreenshooter = null;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Log.i(TAG, "view!!!!:" + v);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("退出确认")
                .setMessage("直播推流中，确认退出？")
                .setPositiveButton("是", new QuitPublishHandler())
                .setNegativeButton("否", null).show();
    }

    public void saveToSDCard(String filename, Bitmap bmp) throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bmp.compress(Bitmap.CompressFormat.PNG, 90, bos);
                bmp.recycle();
                bmp = null;
            } finally {
                if (bos != null) bos.close();
            }

            final String info = "Save frame to:" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setTorchEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enabled) {
                    mTorchBtn.setImageResource(R.mipmap.ic_action_flash_off);
                } else {
                    mTorchBtn.setImageResource(R.mipmap.ic_action_flash_on);
                }
            }
        });
    }

    @Override
    public void onStateChanged(final int state, Object extra) {
        super.onStateChanged(state, extra);
        switch (state) {
            case CameraStreamingManager.STATE.CAMERA_SWITCHED:
                if (extra != null) {
                    Log.i(TAG, "current camera id:" + (Integer) extra);
                }
                Log.i(TAG, "camera switched");
                break;
            case CameraStreamingManager.STATE.TORCH_INFO:
                if (extra != null) {
                    boolean isSupportedTorch = (Boolean) extra;
                    Log.i(TAG, "isSupportedTorch=" + isSupportedTorch);
                    if (isSupportedTorch) {
                        mTorchBtn.setVisibility(View.VISIBLE);
                    } else {
                        mTorchBtn.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onStateHandled(final int state, Object extra) {
        super.onStateHandled(state, extra);
        switch (state) {
            case CameraStreamingManager.STATE.SENDING_BUFFER_HAS_FEW_ITEMS:
                return false;
            case CameraStreamingManager.STATE.SENDING_BUFFER_HAS_MANY_ITEMS:
                return false;
        }
        return false;
    }

    class QuitPublishHandler implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //notify the end of stream
                    StopPublishResult stopResult = LiveStreamService.stopPublish(getSessionId(), getPublishId());
                    if (stopResult.getCode() == APICode.API_OK) {
                        Tools.showToast(mContext, "保存直播节目成功！");
                    } else {
                        //@TODO if notify failed, should record and notify next time
                        Tools.showToast(mContext, "保存直播节目失败！");
                    }
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }).start();
        }
    }

    private class Switcher implements Runnable {
        @Override
        public void run() {
            mCameraStreamingManager.switchCamera();
        }
    }

    private class Screenshooter implements Runnable {
        @Override
        public void run() {
            final String fileName = "PLStreaming_" + System.currentTimeMillis() + ".jpg";
            mCameraStreamingManager.captureFrame(272, 480, new FrameCapturedCallback() {
                private Bitmap bitmap;

                @Override
                public void onFrameCaptured(Bitmap bmp) {
                    bitmap = bmp;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                saveToSDCard(fileName, bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (bitmap != null) {
                                    bitmap.recycle();
                                    bitmap = null;
                                }
                            }
                        }
                    }).start();
                }
            });
        }
    }
}
