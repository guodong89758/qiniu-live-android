package com.qiniu.qlive.activity.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.pili.pldroid.streaming.CameraStreamingManager;
import com.qiniu.qlive.activity.MainActivity;
import com.qiniu.qlive.activity.R;
import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.service.LiveStreamService;
import com.qiniu.qlive.service.result.StopPublishResult;
import com.qiniu.qlive.utils.AsyncRun;
import com.qiniu.qlive.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;


public class StreamingBaseActivity extends Activity implements CameraStreamingManager.StreamingStateListener {

    protected static final int MSG_UPDATE_SHUTTER_BUTTON_STATE = 0;
    private static final String TAG = "StreamingBaseActivity";
    protected Button mShutterButton;
    protected boolean mShutterButtonPressed = false;
    protected String mStatusMsgContent;
    protected TextView mSatusTextView;
    protected CameraStreamingManager mCameraStreamingManager;
    protected JSONObject mJSONObject;
    private Context context;
    private String publishId;
    private String sessionId;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SHUTTER_BUTTON_STATE:
                    if (!mShutterButtonPressed) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // disable the shutter button before startStreaming
                                setShutterButtonEnabled(false);
                                boolean res = mCameraStreamingManager.startStreaming();
                                mShutterButtonPressed = true;
                                Log.i(TAG, "res:" + res);
                                if (!res) {
                                    mShutterButtonPressed = false;
                                    setShutterButtonEnabled(true);
                                }
                                setShutterButtonPressed(mShutterButtonPressed);
                            }
                        }).start();
                    } else {
                        // disable the shutter button before stopStreaming
                        setShutterButtonEnabled(false);
                        mCameraStreamingManager.stopStreaming();
                        setShutterButtonPressed(false);
                        Log.i(TAG, "fire the stream stop publishing");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                StopPublishResult stopResult = LiveStreamService.stopPublish(sessionId, publishId);
                                int code = 0;
                                String desc = "";
                                if (stopResult != null) {
                                    if (stopResult.getCode() == APICode.API_OK) {
                                        code = stopResult.getCode();
                                        desc = "停止推流请求成功";
                                    } else {
                                        code = 0;
                                        desc = String.format("%s:%s", stopResult.getDesc(), stopResult.getDesc());
                                    }
                                } else {
                                    code = 0;
                                    desc = "请求失败，停止推流请求失败";
                                }
                                AsyncRun.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        }).start();

                    }
                    break;
                default:
                    Log.e(TAG, "Invalid message");
            }
        }
    };

    public String getSessionId() {
        return sessionId;
    }

    public String getPublishId() {
        return publishId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.sessionId = Tools.getSession(this.context).getId();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String streamJsonStrFromServer = getIntent().getStringExtra("stream_json_str");
        this.publishId = getIntent().getStringExtra("publish_id");
        Log.i(TAG, "streamJsonStrFromServer:" + streamJsonStrFromServer);
        try {
            mJSONObject = new JSONObject(streamJsonStrFromServer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraStreamingManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mShutterButtonPressed = false;
        mCameraStreamingManager.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraStreamingManager.onDestroy();
    }

    @Override
    public void onStateChanged(final int state, Object extra) {
        Log.i(TAG, "onStateChanged state:" + state);
        switch (state) {
            case CameraStreamingManager.STATE.PREPARING:
                mStatusMsgContent = getString(R.string.string_state_preparing);
                break;
            case CameraStreamingManager.STATE.READY:
                mStatusMsgContent = getString(R.string.string_state_ready);
                // start streaming when READY
                onShutterButtonClick();
                break;
            case CameraStreamingManager.STATE.CONNECTING:
                mStatusMsgContent = getString(R.string.string_state_connecting);
                break;
            case CameraStreamingManager.STATE.STREAMING:
                mStatusMsgContent = getString(R.string.string_state_streaming);
                setShutterButtonEnabled(true);
                break;
            case CameraStreamingManager.STATE.SHUTDOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonEnabled(true);
                setShutterButtonPressed(false);
                break;
            case CameraStreamingManager.STATE.IOERROR:
                mStatusMsgContent = getString(R.string.string_state_ready);
                setShutterButtonEnabled(true);
                break;
            case CameraStreamingManager.STATE.NETBLOCKING:
                mStatusMsgContent = getString(R.string.string_state_netblocking);
                break;
            case CameraStreamingManager.STATE.CONNECTION_TIMEOUT:
                mStatusMsgContent = getString(R.string.string_state_con_timeout);
                break;
            case CameraStreamingManager.STATE.UNKNOWN:
                mStatusMsgContent = getString(R.string.string_state_ready);
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_EMPTY:
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_FULL:
                break;
            case CameraStreamingManager.STATE.AUDIO_RECORDING_FAIL:
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSatusTextView.setText(mStatusMsgContent);
            }
        });
    }

    @Override
    public boolean onStateHandled(final int state, Object extra) {
        Log.i(TAG, "onStateHandled state:" + state);
        return false;
    }

    protected void setShutterButtonPressed(final boolean pressed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButtonPressed = pressed;
                mShutterButton.setPressed(pressed);
            }
        });
    }

    protected void setShutterButtonEnabled(final boolean enable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShutterButton.setFocusable(enable);
                mShutterButton.setClickable(enable);
                mShutterButton.setEnabled(enable);
            }
        });
    }

    protected void onShutterButtonClick() {
        mHandler.removeMessages(MSG_UPDATE_SHUTTER_BUTTON_STATE);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE_SHUTTER_BUTTON_STATE), 50);
    }

}
