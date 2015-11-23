package com.qiniu.qlive.service;


import android.util.Log;

import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.config.Remote;
import com.qiniu.qlive.service.result.LoginResult;
import com.qiniu.qlive.service.result.SignupResult;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;


public class UserService implements APICode {
    private static String TAG = "UserSerivce";

    public static LoginResult login(String mobile, String pwd) {
        LoginResult result = null;
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder().add("mobile", mobile)
                    .add("pwd", pwd).build();
            Request req = new Request.Builder().url(Remote.url(Remote.LOGIN_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                resp.body().close();
                JSONObject jsonObject = new JSONObject(respBody);
                int code = jsonObject.getInt("code");
                String desc = jsonObject.getString("desc");
                result = new LoginResult(code, desc);
                if (code == API_OK) {
                    String sessionId = jsonObject.getString("sessionId");
                    String userName = jsonObject.getString("userName");
                    result.setSessionId(sessionId);
                    result.setUserName(userName);
                }
            } else {
                result = new LoginResult(resp.code(), resp.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "login request failed");
        }

        return result;
    }

    public static SignupResult signup(String mobile, String pwd, String name, String email) {
        SignupResult result = null;
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder().add("mobile", mobile)
                    .add("pwd", pwd).add("name", name).add("email", email).build();
            Request req = new Request.Builder().url(Remote.url(Remote.SIGNUP_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                resp.body().close();
                JSONObject jsonObject = new JSONObject(respBody);
                int code = jsonObject.getInt("code");
                String desc = jsonObject.getString("desc");
                result = new SignupResult(code, desc);
            } else {
                result = new SignupResult(resp.code(), resp.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "signup request failed");
        }

        return result;
    }
}


