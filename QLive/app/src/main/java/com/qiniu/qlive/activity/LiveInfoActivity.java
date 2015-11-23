package com.qiniu.qlive.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.qiniu.qlive.activity.live.SWCodecCameraStreamingActivity;
import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.service.LiveStreamService;
import com.qiniu.qlive.service.result.GetStreamResult;
import com.qiniu.qlive.service.result.StartPublishResult;
import com.qiniu.qlive.service.result.StreamStatus;
import com.qiniu.qlive.utils.AsyncRun;
import com.qiniu.qlive.utils.Tools;


public class LiveInfoActivity extends AppCompatActivity implements APICode, AdapterView.OnItemSelectedListener {
    private Context context;
    private EditText streamTitleEditText;
    private Spinner streamQualitySpinner;
    private Spinner streamOrientationSpinner;
    private String streamTitle;
    private int streamQuality;
    private int streamOrientation;
    private String sessionId;

    public LiveInfoActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_info);
        this.context = this;
        this.sessionId = Tools.getSession(this.context).getId();
        this.streamTitleEditText = (EditText) this.findViewById(R.id.stream_title_edittext);
        this.streamQualitySpinner = (Spinner) this.findViewById(R.id.stream_quality_spinner);
        this.streamOrientationSpinner = (Spinner) this.findViewById(R.id.stream_orientation_spinner);

        ArrayAdapter<CharSequence> qAdapter = ArrayAdapter.createFromResource(this, R.array.stream_quality_list,
                android.R.layout.simple_spinner_dropdown_item);
        this.streamQualitySpinner.setAdapter(qAdapter);

        ArrayAdapter<CharSequence> oAdapter = ArrayAdapter.createFromResource(this, R.array.stream_orientation_list,
                android.R.layout.simple_spinner_dropdown_item);
        this.streamOrientationSpinner.setAdapter(oAdapter);

        this.streamQualitySpinner.setOnItemSelectedListener(this);
        this.streamOrientationSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void startRecord(View view) {
        this.streamTitle = this.streamTitleEditText.getText().toString().trim();
        if (streamTitle == null || streamTitle.isEmpty()) {
            showToast("请输入节目标题！");
            return;
        }

        if (streamQuality < 0) {
            showToast("请选择节目录制品质！");
            return;
        }

        if (streamOrientation < 0) {
            showToast("请选择节目录制方向！");
            return;
        }


        //get stream
        new Thread(new Runnable() {
            @Override
            public void run() {
                final GetStreamResult streamResult = LiveStreamService.getStream(sessionId);
                if (streamResult != null) {
                    switch (streamResult.getCode()) {
                        case API_OK:
                            //check stream publishing status
                            final StreamStatus streamStatus = LiveStreamService.getStreamStatus(sessionId, streamResult.getStreamId());
                            if (streamStatus != null) {
                                if (streamStatus.getCode() == API_STREAM_IS_TAKEN_ERROR) {
                                    AsyncRun.run(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setTitle("直播确认")
                                                    .setMessage("该账号直播推流中，强制重新开始？")
                                                    .setPositiveButton("是", new ForceHandler(streamResult))
                                                    .setNegativeButton("否", null).show();
                                        }
                                    });
                                } else if (streamStatus.getCode() == API_OK) {
                                    startPublish(streamResult);
                                } else if (streamResult.getCode() == API_UNAUTHORIZED_ERROR) {
                                    showToast("请求凭证失败！");
                                } else if (streamResult.getCode() == API_SERVER_ERROR) {
                                    showToast("服务器内部错误，请稍后重试！");
                                }
                            } else {
                                showToast("请求失败，请检查网络状况！");
                                break;
                            }
                            break;
                        case API_UNAUTHORIZED_ERROR:
                            showToast("请求授权失败！");
                            break;
                        case API_SERVER_ERROR:
                            //server error
                            showToast("服务器内部错误，请稍后重试！");
                            break;
                    }
                } else {
                    showToast("请求失败，请检查网络状况！");
                }
            }
        }).start();

    }

    public void startPublish(final GetStreamResult streamResult) {
        //fire the start publish
        StartPublishResult pResult = LiveStreamService.startPublish(this.sessionId,
                streamResult.getStreamId(), streamTitle, streamQuality, streamOrientation);
        if (pResult != null) {
            switch (pResult.getCode()) {
                case API_OK:
                    final String publishId = pResult.getPublishId();
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, SWCodecCameraStreamingActivity.class);
                            intent.putExtra("publish_id", publishId);
                            intent.putExtra("stream_quality", streamQuality);
                            intent.putExtra("stream_orientation", streamOrientation);
                            intent.putExtra("stream_json_str", streamResult.getStream());
                            startActivity(intent);
                        }
                    });
                    break;
                case API_UNAUTHORIZED_ERROR:
                    showToast("请求凭证失败！");
                    break;
                case API_PARAM_ERROR:
                    showToast("请求参数错误，请检查代码！");
                    break;
                case API_SERVER_ERROR:
                    //server error
                    showToast("服务器内部错误，请稍后重试！");
                    break;
            }
        } else {
            showToast("请求失败，请检查网络状况！");
        }
    }

    public void showToast(final String msg) {
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.stream_quality_spinner:
                this.streamQuality = position;
                break;
            case R.id.stream_orientation_spinner:
                this.streamOrientation = position;
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        this.streamQuality = -1;
        this.streamOrientation = -1;
    }

    class ForceHandler implements DialogInterface.OnClickListener {
        private GetStreamResult streamResult;

        public ForceHandler(GetStreamResult streamResult) {
            this.streamResult = streamResult;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startPublish(streamResult);
                }
            }).start();
        }
    }
}
