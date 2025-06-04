package com.example.sensorsapplication.network.request;

import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a request for auto-reporting network attributes.
 * This class is used to create a request payload for the auto-report feature,
 * which includes network attributes and message information.
 * The request is structured to be sent to a server
 * for processing auto-reporting of network data.
 */

public class AutoReportRequest {
    @SerializedName("msg_type")
    private String msgType = "control";

    @SerializedName("opt")
    private String opt = "auto_report";

    @SerializedName("net_attr")
    private NetAttr netAttr;

    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("node_addr")
        private String nodeAddr;

        @SerializedName("interval")
        private String interval;

        public MsgInfo(String nodeAddr, String interval) {
            this.nodeAddr = nodeAddr;
            this.interval = interval;
        }
    }

    public AutoReportRequest(NetAttr netAttr, String nodeAddr, String interval) {
        this.netAttr = netAttr;
        this.msgInfo = new MsgInfo(nodeAddr, interval);
    }
}
