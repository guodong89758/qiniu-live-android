package com.qiniu.qlive.service;

import com.qiniu.qlive.config.Remote;
import com.qiniu.qlive.model.LiveVideo;
import com.qiniu.qlive.service.result.VideoListResult;
import com.qiniu.qlive.utils.Tools;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jemy on 11/6/15.
 */
public class VideoListService {
    public static VideoListResult getVideoList(String sessionId) {
        VideoListResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken).build();
            Request req = new Request.Builder().url(Remote.url(Remote.VIDEO_LIST_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");

                List<LiveVideo> videoList = new ArrayList<LiveVideo>();
                ;
                try {
                    JSONArray apiVideoArray = jsonObject.getJSONArray("videoList");
                    if (apiVideoArray != null) {
                        int itemCount = apiVideoArray.length();
                        for (int i = 0; i < itemCount; i++) {
                            JSONObject item = apiVideoArray.getJSONObject(i);
                            String user = item.getString("user");
                            String title = item.getString("title");
                            String publishId = item.getString("publishId");
                            long createTimeTS = item.getLong("createTime");
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(createTimeTS * 1000);
                            Date createTime = c.getTime();
                            LiveVideo video = new LiveVideo(user, title, publishId, createTime);
                            videoList.add(video);
                        }
                    }
                } catch (JSONException ex) {

                }
                result = new VideoListResult(apiCode, apiDesc, videoList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public static VideoListResult getMyVideoList(String sessionId) {
        VideoListResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken).build();
            Request req = new Request.Builder().url(Remote.url(Remote.MY_VIDEO_LIST_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");

                List<LiveVideo> videoList = new ArrayList<LiveVideo>();
                ;
                try {
                    JSONArray apiVideoArray = jsonObject.getJSONArray("videoList");
                    if (apiVideoArray != null) {
                        int itemCount = apiVideoArray.length();
                        for (int i = 0; i < itemCount; i++) {
                            JSONObject item = apiVideoArray.getJSONObject(i);
                            String user = item.getString("user");
                            String title = item.getString("title");
                            String publishId = item.getString("publishId");
                            long createTimeTS = item.getLong("createTime");
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(createTimeTS * 1000);
                            Date createTime = c.getTime();
                            LiveVideo video = new LiveVideo(user, title, publishId, createTime);
                            videoList.add(video);
                        }
                    }
                } catch (JSONException ex) {

                }
                result = new VideoListResult(apiCode, apiDesc, videoList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static VideoListResult getStreamList(String sessionId) {
        VideoListResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken).build();
            Request req = new Request.Builder().url(Remote.url(Remote.STREAM_LIST_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");

                List<LiveVideo> videoList = new ArrayList<LiveVideo>();
                try {
                    JSONArray apiVideoArray = jsonObject.getJSONArray("videoList");
                    if (apiVideoArray != null) {
                        int itemCount = apiVideoArray.length();
                        for (int i = 0; i < itemCount; i++) {
                            JSONObject item = apiVideoArray.getJSONObject(i);
                            String user = item.getString("user");
                            String title = item.getString("title");
                            String publishId = item.getString("publishId");
                            long createTimeTS = item.getLong("createTime");
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(createTimeTS * 1000);
                            Date createTime = c.getTime();
                            LiveVideo video = new LiveVideo(user, title, publishId, createTime);
                            videoList.add(video);
                        }
                    }
                } catch (JSONException ex) {

                }
                result = new VideoListResult(apiCode, apiDesc, videoList);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
