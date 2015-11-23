package com.qiniu.qlive.activity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.pili.pldroid.player.widget.VideoView;
import com.qiniu.qlive.activity.widget.MediaController;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoPlayActivity extends AppCompatActivity {
    private VideoView videoPlayView;
    private MediaController videoPlayController;
    private int mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int videoOrient = this.getIntent().getIntExtra("VideoOrientation", 0);
        this.mOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (videoOrient == 1) {
            this.mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        this.setRequestedOrientation(this.mOrientation);
        this.getSupportActionBar().hide();

        setContentView(R.layout.activity_video_play);
        this.initVideoPlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void initVideoPlay() {
        this.videoPlayController = new MediaController(this);
        this.videoPlayView = (VideoView) this
                .findViewById(R.id.pili_video_play_pldplayer);
        videoPlayView.setMediaController(videoPlayController);
        videoPlayController.setMediaPlayer(videoPlayView);
        videoPlayController.setAnchorView(videoPlayView);

        final String videoTitle = this.getIntent().getStringExtra("VideoTitle");
        final String videoUrl = this.getIntent().getStringExtra("VideoUrl");
        this.setTitle(videoTitle);

        //common settings
        videoPlayView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        videoPlayView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        //video to play
        videoPlayView.setVideoURI(Uri.parse(videoUrl));
        videoPlayView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mp.start();
            }
        });
        videoPlayView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                mp.stop();
            }
        });
    }
}
