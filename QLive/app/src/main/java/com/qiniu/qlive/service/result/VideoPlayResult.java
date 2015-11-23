package com.qiniu.qlive.service.result;

import java.util.Map;

/**
 * Created by jemy on 11/8/15.
 */
public class VideoPlayResult extends ApiResult {
    private int orientation;
    private Map<String, String> playUrls;

    public VideoPlayResult(int code, String desc, int orientation, Map<String, String> playUrls) {
        super(code, desc);
        this.orientation = orientation;
        this.playUrls = playUrls;
    }

    public int getOrientation() {
        return orientation;
    }

    public Map<String, String> getPlayUrls() {
        return playUrls;
    }
}
