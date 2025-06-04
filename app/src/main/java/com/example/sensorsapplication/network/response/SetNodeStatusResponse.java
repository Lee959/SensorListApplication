package com.example.sensorsapplication.network.response;

import com.example.sensorsapplication.model.BaseMessage;
import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/*
 * Represents network attributes for querying node data.
 * This class is used to create a request payload for setting the status of a node,
 * which includes details such as network type, name, virtual/real judgment, PAN ID, and channel ID.
 * The request is structured to be sent to a server for processing the status update of a node.
 */
public class SetNodeStatusResponse extends BaseMessage {
    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("node_addr")
        private int nodeAddr;

        @SerializedName("node_addr_str")
        private String nodeAddrStr;

        @SerializedName("node_type")
        private int nodeType;

        @SerializedName("node_status")
        private int nodeStatus;

        @SerializedName("status")
        private String status;

        public MsgInfo() {}

        public MsgInfo(int nodeAddr, String nodeAddrStr, int nodeType, int nodeStatus, String status) {
            this.nodeAddr = nodeAddr;
            this.nodeAddrStr = nodeAddrStr;
            this.nodeType = nodeType;
            this.nodeStatus = nodeStatus;
            this.status = status;
        }

        // Getters and Setters
        public int getNodeAddr() { return nodeAddr; }
        public void setNodeAddr(int nodeAddr) { this.nodeAddr = nodeAddr; }

        public String getNodeAddrStr() { return nodeAddrStr; }
        public void setNodeAddrStr(String nodeAddrStr) { this.nodeAddrStr = nodeAddrStr; }

        public int getNodeType() { return nodeType; }
        public void setNodeType(int nodeType) { this.nodeType = nodeType; }

        public int getNodeStatus() { return nodeStatus; }
        public void setNodeStatus(int nodeStatus) { this.nodeStatus = nodeStatus; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public SetNodeStatusResponse() {
        super("ctrl", "set_node_status", null);
    }

    public SetNodeStatusResponse(NetAttr netAttr, MsgInfo msgInfo) {
        super("ctrl", "set_node_status", netAttr);
        this.msgInfo = msgInfo;
    }

    public MsgInfo getMsgInfo() { return msgInfo; }
    public void setMsgInfo(MsgInfo msgInfo) { this.msgInfo = msgInfo; }
}