package com.qiniu.qlive.model;

import java.util.Date;

/**
 * Created by jemy on 11/6/15.
 */
public class LiveVideo {
    private String user;
    private String title;
    private String publishId;
    private Date createTime;

    public LiveVideo(String user, String title, String publishId, Date createTime) {
        this.user = user;
        this.title = title;
        this.publishId = publishId;
        this.createTime = createTime;
    }

    public String getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishId() {
        return publishId;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
