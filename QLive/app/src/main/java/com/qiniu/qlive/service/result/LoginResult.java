package com.qiniu.qlive.service.result;


public class LoginResult extends ApiResult {
    private String sessionId;
    private String userName;

    public LoginResult(int code, String desc) {
        super(code, desc);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
