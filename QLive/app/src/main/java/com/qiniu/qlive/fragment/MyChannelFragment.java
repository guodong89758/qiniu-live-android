package com.qiniu.qlive.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qiniu.qlive.activity.R;
import com.qiniu.qlive.config.ActionID;
import com.qiniu.qlive.utils.Tools;


public class MyChannelFragment extends Fragment implements View.OnClickListener, ActionID {
    private OnFragmentInteractionListener mListener;

    private TextView userNameTextView;
    private Button showMyRecordsButton;
    private Button startPublishVideoSWButton;
    private Button startPublishVideoHWButton;
    private Button startPublishAudioButton;


    public MyChannelFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = Tools.getSession(view.getContext()).getUserName();
        this.userNameTextView.setText(userName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_channel, container, false);
        this.showMyRecordsButton = (Button) v.findViewById(R.id.show_my_records_btn);
        // this.startPublishVideoHWButton = (Button) v.findViewById(R.id.start_publish_video_hw_btn);
        this.startPublishVideoSWButton = (Button) v.findViewById(R.id.start_publish_video_sw_btn);
        //this.startPublishAudioButton = (Button) v.findViewById(R.id.start_publish_audio_btn);

        this.showMyRecordsButton.setOnClickListener(this);
        //this.startPublishVideoHWButton.setOnClickListener(this);
        this.startPublishVideoSWButton.setOnClickListener(this);
        //this.startPublishVideoHWButton.setOnClickListener(this);

        this.userNameTextView = (TextView) v.findViewById(R.id.user_name_textview);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_my_records_btn:
                mListener.onFragmentInteraction(ACTION_LOAD_MY_VIDEO_LIST);
                break;
            case R.id.start_publish_video_sw_btn:
                mListener.onFragmentInteraction(ACTION_START_PUBLISH_VIDEO_SW);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String id);
    }

}
