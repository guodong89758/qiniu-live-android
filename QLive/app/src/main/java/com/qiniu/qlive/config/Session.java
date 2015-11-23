package com.qiniu.qlive.config;

/**
 * Created by jemy on 11/7/15.
 */
public class Session {
    private String id;
    private String userName;

    public Session(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getId() {
        return id;
    }
}
