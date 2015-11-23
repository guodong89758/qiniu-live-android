package com.qiniu.qlive.service.result;

/**
 * Created by jemy on 11/6/15.
 */
public class ApiResult {
    private int code;
    private String desc;

    public ApiResult(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
