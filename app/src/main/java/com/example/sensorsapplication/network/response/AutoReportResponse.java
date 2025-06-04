package com.example.sensorsapplication.network.response;


import com.example.sensorsapplication.model.BaseMessage;
import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/*
 * Represents a response for auto-reporting network attributes.
 * This class is used to handle the response payload
 * for the auto-report feature, which includes message information about the network node.
 * The response is structured to be received from a server
 * for processing auto-reporting of network data.
 */

public class AutoReportResponse extends BaseMessage {
    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("node_addr")
        private int nodeAddr;

        @SerializedName("node_addr_str")
        private String nodeAddrStr;

        @SerializedName("status")
        private String status;

        public MsgInfo() {}

        public MsgInfo(int nodeAddr, String nodeAddrStr, String status) {
            this.nodeAddr = nodeAddr;
            this.nodeAddrStr = nodeAddrStr;
            this.status = status;
        }

        public int getNodeAddr() { return nodeAddr; }
        public void setNodeAddr(int nodeAddr) { this.nodeAddr = nodeAddr; }

        public String getNodeAddrStr() { return nodeAddrStr; }
        public void setNodeAddrStr(String nodeAddrStr) { this.nodeAddrStr = nodeAddrStr; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public AutoReportResponse() {}

    public AutoReportResponse(String opt, NetAttr netAttr, MsgInfo msgInfo) {
        super("ctrl_ret", opt, netAttr);
        this.msgInfo = msgInfo;
    }

    public MsgInfo getMsgInfo() { return msgInfo; }
    public void setMsgInfo(MsgInfo msgInfo) { this.msgInfo = msgInfo; }
}