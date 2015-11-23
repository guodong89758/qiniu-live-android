package com.qiniu.qlive.service.result;


import com.qiniu.qlive.model.LiveVideo;

import java.util.List;

public class VideoListResult extends ApiResult {
    private List<LiveVideo> videoList;

    public VideoListResult(int code, String desc, List<LiveVideo> videoList) {
        super(code, desc);
        this.videoList = videoList;
    }

    public List<LiveVideo> getVideoList() {
        return videoList;
    }
}
