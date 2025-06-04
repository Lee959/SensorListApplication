package com.example.sensorsapplication.network.request;

import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;


/*
 * Represents a request to stop auto-reporting network attributes.
 * This class is used to create a request payload for stopping the auto-report feature,
 * which includes network attributes and message information.
 */
public class StopAutoReportRequest {
    @SerializedName("msg_type")
    private String msgType = "control";

    @SerializedName("opt")
    private String opt = "stop_auto_report";

    @SerializedName("net_attr")
    private NetAttr netAttr;

    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("node_addr")
        private String nodeAddr;

        public MsgInfo(String nodeAddr) {
            this.nodeAddr = nodeAddr;
        }
    }

    public StopAutoReportRequest(NetAttr netAttr, String nodeAddr) {
        this.netAttr = netAttr;
        this.msgInfo = new MsgInfo(nodeAddr);
    }
}