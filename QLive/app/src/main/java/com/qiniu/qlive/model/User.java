package com.qiniu.qlive.model;

/**
 * Created by jemy on 11/1/15.
 */
public class User {
    private int id;

    private String sessionId;

    public User(int id, String sessionId) {
        this.id = id;
        this.sessionId = sessionId;
    }

    public int getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }
}
