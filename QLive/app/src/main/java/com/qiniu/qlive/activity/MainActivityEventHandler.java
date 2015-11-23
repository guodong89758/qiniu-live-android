package com.qiniu.qlive.activity;

import android.content.Context;
import android.content.Intent;

import com.qiniu.qlive.config.ActionID;
import com.qiniu.qlive.config.LiveTask;


public class MainActivityEventHandler implements ActionID, LiveTask {
    public Context context;

    public MainActivityEventHandler(Context context) {
        this.context = context;
    }

    public void runTask(String taskId) {
        switch (taskId) {
            case ACTION_START_PUBLISH_VIDEO_SW:
                this.startPublishVideoSW();
                break;
            case ACTION_LOAD_MY_VIDEO_LIST:
                this.switchToMyVideoListActivity();
                break;
        }
    }

    public void startPublishVideoSW() {
        Intent intent = new Intent(this.context, LiveInfoActivity.class);
        intent.putExtra("LiveTask", RECORD_VIDEO_SW);
        this.context.startActivity(intent);
    }

    public void switchToMyVideoListActivity() {
        Intent intent = new Intent(this.context, MyLiveVideoListActivity.class);
        this.context.startActivity(intent);
    }
}
