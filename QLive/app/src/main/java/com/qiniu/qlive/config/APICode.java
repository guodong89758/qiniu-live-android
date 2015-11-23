package com.qiniu.qlive.config;

/**
 * Created by jemy on 11/1/15.
 */
public interface APICode {
    public final int API_OK = 1000;

    //shared error
    public final int API_SERVER_ERROR = 1001;
    public final int API_PARAM_ERROR = 1002;
    public final int API_UNAUTHORIZED_ERROR = 1003;

    //login error
    public final int API_USER_NOT_FOUND_ERROR = 1100;
    public final int API_USER_PWD_ERROR = 1101;

    //signup error
    public final int API_PHONE_EXISTS_ERROR = 1201;
    public final int API_NAME_EXISTS_ERROR = 1202;
    public final int API_EMAIL_EXISTS_ERROR = 1203;

    //stream
    public final int API_STREAM_IS_TAKEN_ERROR = 1401;
    public final int API_NO_VIDEO_FOUND_ERROR = 1501;
}
