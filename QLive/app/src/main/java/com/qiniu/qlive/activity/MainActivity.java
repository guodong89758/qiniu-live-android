package com.qiniu.qlive.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.config.Config;
import com.qiniu.qlive.fragment.LiveStreamListFragment;
import com.qiniu.qlive.fragment.LiveVideoListFragment;
import com.qiniu.qlive.fragment.MyChannelFragment;
import com.qiniu.qlive.service.UserService;
import com.qiniu.qlive.service.result.LoginResult;
import com.qiniu.qlive.utils.Tools;

import org.json.JSONObject;

import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity implements APICode,
        LiveStreamListFragment.OnFragmentInteractionListener,
        MyChannelFragment.OnFragmentInteractionListener,
        LiveVideoListFragment.OnFragmentInteractionListener {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private PagerTitleStrip mPagerTitleStrip;
    private MainActivityEventHandler eventHandler;
    private String TAG = "MainActivity";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        //check login
        this.autoLogin();

        setContentView(R.layout.activity_main);

        this.eventHandler = new MainActivityEventHandler(this);
        //pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //tab strip
        mPagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pagerTitleStrip);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionMenuItemView menuItem = (ActionMenuItemView) findViewById(R.id.action_refresh_video_list);
                switch (position) {
                    case 0:
                    case 1:
                        menuItem.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        menuItem.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

    }

    //try auto login
    private void autoLogin() {
        Log.d(TAG, "try auto login");

        try {
            FileInputStream fis = this.openFileInput(Config.accountFileName);
            byte[] buffer = new byte[4096];
            int num = fis.read(buffer);
            fis.close();
            String accountInfo = new String(buffer, 0, num);

            JSONObject jsonObject = new JSONObject(accountInfo);
            final String mobile = jsonObject.getString("mobile");
            final String password = jsonObject.getString("pwd");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoginResult loginResult = UserService.login(mobile, password);
                    if (loginResult != null && loginResult.getCode() == API_OK) {
                        Tools.writeSession(context, loginResult.getSessionId(), loginResult.getUserName());
                    } else {
                        Log.i(TAG, "auto login error");
                        //switch to login
                        switchToLogin();
                    }
                }
            }).start();
        } catch (Exception ex) {
            Log.e(TAG, "auto login fire error");
            this.switchToLogin();
        }
    }

    private void switchToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Tools.writeSession(context, "", "");
            this.deleteFile(Config.accountFileName);
            Intent intent = new Intent(this.context, LoginActivity.class);
            this.startActivity(intent);
            this.finish();
            return true;
        } else if (id == R.id.action_refresh_video_list) {
            int curIndex = mViewPager.getCurrentItem();
            switch (curIndex) {
                case 0:
                    final LiveStreamListFragment sf = (LiveStreamListFragment) mSectionsPagerAdapter.getItem(curIndex);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sf.populateStreamList(context, Tools.getSession(context).getId());
                        }
                    }).start();
                    break;
                case 1:
                    final LiveVideoListFragment vf = (LiveVideoListFragment) mSectionsPagerAdapter.getItem(curIndex);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            vf.populateVideoList(context, Tools.getSession(context).getId());
                        }
                    }).start();
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String taskId) {
        this.eventHandler.runTask(taskId);
    }


    ///////////////////
    class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final int INDEX_FIRST = 0;
        private final int INDEX_SECOND = 1;
        private final int INDEX_THIRD = 2;
        private final int TOTAL_COUNT = 3;
        private LiveStreamListFragment streamItemFragment;
        private LiveVideoListFragment videoItemFragment;
        private MyChannelFragment myChannelFragment;
        private Context context;

        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            this.streamItemFragment = new LiveStreamListFragment();
            this.videoItemFragment = new LiveVideoListFragment();
            this.myChannelFragment = new MyChannelFragment();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case INDEX_FIRST:
                    fragment = this.streamItemFragment;
                    break;
                case INDEX_SECOND:
                    fragment = this.videoItemFragment;
                    break;
                case INDEX_THIRD:
                    fragment = this.myChannelFragment;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TOTAL_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case INDEX_FIRST:
                    title = context.getString(R.string.title_section_stream);
                    break;
                case INDEX_SECOND:
                    title = context.getString(R.string.title_section_video);
                    break;
                case INDEX_THIRD:
                    title = context.getString(R.string.title_section_channel);
                    break;
            }
            return title;
        }
    }


}
