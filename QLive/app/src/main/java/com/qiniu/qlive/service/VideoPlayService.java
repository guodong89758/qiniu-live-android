package com.qiniu.qlive.service;

import com.qiniu.qlive.config.Remote;
import com.qiniu.qlive.service.result.VideoPlayResult;
import com.qiniu.qlive.utils.Tools;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jemy on 11/7/15.
 */
public class VideoPlayService {
    public static VideoPlayResult getVideoPlayResult(String sessionId, String publishId) {
        VideoPlayResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken)
                    .add("publishId", publishId).build();
            Request req = new Request.Builder().url(Remote.url(Remote.GET_PLAY_VIDEO_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");
                int apiOrientation = jsonObject.getInt("orientation");

                Map<String, String> playUrls = new HashMap<String, String>();
                try {
                    JSONObject apiPlayUrls = jsonObject.getJSONObject("playUrls");
                    if (apiPlayUrls != null) {
                        playUrls.put("ORIGIN", apiPlayUrls.getString("ORIGIN"));
                    }
                } catch (JSONException ex) {

                }
                result = new VideoPlayResult(apiCode, apiDesc, apiOrientation, playUrls);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public static VideoPlayResult getStreamPlayResult(String sessionId, String publishId) {
        VideoPlayResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken)
                    .add("publishId", publishId).build();
            Request req = new Request.Builder().url(Remote.url(Remote.GET_PLAY_STREAM_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");
                int apiOrientation = jsonObject.getInt("orientation");

                Map<String, String> playUrls = new HashMap<String, String>();
                try {
                    JSONObject apiPlayUrls = jsonObject.getJSONObject("playUrls");
                    if (apiPlayUrls != null) {
                        playUrls.put("ORIGIN", apiPlayUrls.getString("ORIGIN"));
                    }
                } catch (JSONException ex) {

                }
                result = new VideoPlayResult(apiCode, apiDesc, apiOrientation, playUrls);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
