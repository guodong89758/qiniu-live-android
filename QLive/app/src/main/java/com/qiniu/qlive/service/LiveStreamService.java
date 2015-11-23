package com.qiniu.qlive.service;

import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.config.Remote;
import com.qiniu.qlive.service.result.GetStreamResult;
import com.qiniu.qlive.service.result.StartPublishResult;
import com.qiniu.qlive.service.result.StopPublishResult;
import com.qiniu.qlive.service.result.StreamStatus;
import com.qiniu.qlive.utils.Tools;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

/**
 * Created by jemy on 11/2/15.
 */
public class LiveStreamService implements APICode {
    public static StreamStatus getStreamStatus(String sessionId, String streamId) {
        StreamStatus result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken)
                    .add("streamId", streamId).build();
            Request req = new Request.Builder().url(Remote.url(Remote.STREAM_PUBILSH_STATUS_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");
                result = new StreamStatus(apiCode, apiDesc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    //get stream
    public static GetStreamResult getStream(String sessionId) {
        GetStreamResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder()
                    .add("sessionId", sessionId)
                    .add("accessToken", accessToken).build();
            Request req = new Request.Builder().url(Remote.url(Remote.GET_STREAM_SERVICE))
                    .method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");
                String apiStream = jsonObject.getString("stream");
                String apiStreamId = jsonObject.getString("streamId");
                result = new GetStreamResult(apiCode, apiDesc, apiStreamId, apiStream);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    //start publish stream
    public static StartPublishResult startPublish(String sessionId, String streamId, String streamTitle, int streamQuality, int streamOrientation) {
        StartPublishResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder().add("sessionId", sessionId).add("accessToken", accessToken)
                    .add("streamTitle", streamTitle).add("streamId", streamId)
                    .add("streamQuality", streamQuality + "").add("streamOrientation", streamOrientation + "").build();
            Request req = new Request.Builder().url(Remote.url(Remote.START_PUBLISH_SERVICE)).method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");
                String apiPublishId = jsonObject.getString("publishId");
                result = new StartPublishResult(apiCode, apiDesc, apiPublishId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    //stop publish stream
    public static StopPublishResult stopPublish(String sessionId, String publishId) {
        StopPublishResult result = null;
        String accessToken = Tools.createAccessToken(sessionId);
        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody reqBody = new FormEncodingBuilder().add("sessionId", sessionId).add("accessToken", accessToken)
                    .add("publishId", publishId).build();
            Request req = new Request.Builder().url(Remote.url(Remote.STOP_PUBLISH_SERVICE)).method("POST", reqBody).build();
            Response resp = client.newCall(req).execute();
            if (resp.isSuccessful()) {
                String respBody = resp.body().string();
                JSONObject jsonObject = new JSONObject(respBody);
                int apiCode = jsonObject.getInt("code");
                String apiDesc = jsonObject.getString("desc");
                result = new StopPublishResult(apiCode, apiDesc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
}


