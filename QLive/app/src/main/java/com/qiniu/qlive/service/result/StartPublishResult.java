package com.qiniu.qlive.service.result;


public class StartPublishResult extends ApiResult {
    private String publishId;

    public StartPublishResult(int code, String desc, String publishId) {
        super(code, desc);
        this.publishId = publishId;
    }

    public String getPublishId() {
        return publishId;
    }
}
