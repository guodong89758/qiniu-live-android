package com.qiniu.qlive.config;

/**
 * Created by jemy on 11/1/15.
 */
public class Remote {
    public static final String HOST = "http://115.231.183.102:8888";
    //public  static final String HOST="http://192.168.200.117:9090";
    public static final String LOGIN_SERVICE = "/login";
    public static final String SIGNUP_SERVICE = "/signup";
    public static final String GET_STREAM_SERVICE = "/get/stream";
    public static final String STREAM_PUBILSH_STATUS_SERVICE = "/status/stream";
    public static final String START_PUBLISH_SERVICE = "/start/publish";
    public static final String STOP_PUBLISH_SERVICE = "/stop/publish";
    public static final String VIDEO_LIST_SERVICE = "/live/video/list";
    public static final String STREAM_LIST_SERVICE = "/live/stream/list";
    public static final String MY_VIDEO_LIST_SERVICE = "/my/live/video/list";
    public static final String GET_PLAY_STREAM_SERVICE = "/get/play/stream";
    public static final String GET_PLAY_VIDEO_SERVICE = "/get/play/video";

    public static String url(String path) {
        return HOST + path;
    }

}
