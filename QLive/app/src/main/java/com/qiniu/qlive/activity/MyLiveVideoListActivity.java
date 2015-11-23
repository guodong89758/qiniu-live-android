package com.qiniu.qlive.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.model.LiveVideo;
import com.qiniu.qlive.service.VideoListService;
import com.qiniu.qlive.service.VideoPlayService;
import com.qiniu.qlive.service.result.VideoListResult;
import com.qiniu.qlive.service.result.VideoPlayResult;
import com.qiniu.qlive.utils.AsyncRun;
import com.qiniu.qlive.utils.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyLiveVideoListActivity extends AppCompatActivity implements APICode, AdapterView.OnItemClickListener {

    private ListView myVideoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_live_video_list);
        this.myVideoListView = (ListView) this.findViewById(R.id.my_video_listview);
        this.myVideoListView.setOnItemClickListener(this);
        final String sessionId = Tools.getSession(this).getId();
        final Context ctx = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                populateVideoList(ctx, sessionId);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_live_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void populateVideoList(final Context ctx, final String sessionId) {
        VideoListResult videoListResult = VideoListService.getMyVideoList(sessionId);
        if (videoListResult != null) {
            if (videoListResult.getCode() == API_OK) {
                if (!videoListResult.getVideoList().isEmpty()) {
                    List<Map<String, String>> valuePairs = new ArrayList<Map<String, String>>();
                    DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    for (LiveVideo video : videoListResult.getVideoList()) {
                        Map<String, String> value = new HashMap<String, String>();
                        value.put("USER_NAME", video.getUser());
                        value.put("CREATE_TIME", fmt.format(video.getCreateTime()));
                        value.put("PUBLISH_ID", video.getPublishId());
                        value.put("VIDEO_TITLE", video.getTitle());

                        valuePairs.add(value);
                    }

                    final List<Map<String, String>> data = valuePairs;
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            final SimpleAdapter dataAdapter = new SimpleAdapter(ctx, data, R.layout.list_item_video,
                                    new String[]{"USER_NAME", "CREATE_TIME", "PUBLISH_ID", "VIDEO_TITLE"}, new int[]{
                                    R.id.list_item_user_textview, R.id.list_item_create_time_textview,
                                    R.id.list_item_publishId_textview, R.id.list_item_video_title_textview
                            });
                            myVideoListView.setAdapter(dataAdapter);
                        }
                    });
                }
            } else {
                Tools.showToast(ctx, "获取点播列表失败！");
            }
        } else {
            Tools.showToast(ctx, "请求失败，请检查网络状况！");
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        TextView publishIdTextView = (TextView) v.findViewById(R.id.list_item_publishId_textview);
        TextView titleTextView = (TextView) v.findViewById(R.id.list_item_video_title_textview);
        final Context ctx = this;
        final String videoTitle = titleTextView.getText().toString();
        final String publishId = publishIdTextView.getText().toString();
        final String sessionId = Tools.getSession(ctx).getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoPlayResult playResult = VideoPlayService.getVideoPlayResult(sessionId, publishId);
                if (playResult != null && playResult.getCode() == API_OK) {
                    final String originUrl = playResult.getPlayUrls().get("ORIGIN");
                    final int videoOrientation = playResult.getOrientation();
                    if (originUrl != null) {
                        AsyncRun.run(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ctx, VideoPlayActivity.class);
                                intent.putExtra("VideoOrientation", videoOrientation);
                                intent.putExtra("VideoTitle", videoTitle);
                                intent.putExtra("VideoUrl", originUrl);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Tools.showToast(ctx, "无法获取视频播放地址！");
                    }
                } else {
                    Tools.showToast(ctx, "请求失败，请检查网络状况！");
                }
            }
        }).start();
    }
}
