package com.qiniu.qlive.service.result;


public class GetStreamResult extends ApiResult {

    private String streamId;
    private String stream;

    public GetStreamResult(int code, String desc, String streamId, String stream) {
        super(code, desc);
        this.streamId = streamId;
        this.stream = stream;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getStream() {
        return stream;
    }
}
