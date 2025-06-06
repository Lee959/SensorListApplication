package com.example.sensorsapplication.network.response;


import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/*
 * Represents a response for auto-reporting network attributes.
 * This class is used to create a request payload for querying node data,
 * which includes network attributes and message information.
 * The request is structured to be sent to a server
 * for processing the retrieval of node data.
 */

public class GetNodeDataResponse {
    @SerializedName("msg_type")
    private String msgType;

    @SerializedName("opt")
    private String opt;

    @SerializedName("net_attr")
    private NetAttr netAttr;

    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("node_addr")
        private int nodeAddr;

        @SerializedName("node_addr_str")
        private String nodeAddrStr;

        @SerializedName("node_type")
        private int nodeType;

        //todo: change to String
        @SerializedName("node_data")
        private int nodeData;

        // Getters
        public int getNodeAddr() { return nodeAddr; }
        public String getNodeAddrStr() { return nodeAddrStr; }
        public int getNodeType() { return nodeType; }
        public int getNodeData() { return nodeData; }
    }

    // Getters
    public String getMsgType() { return msgType; }
    public String getOpt() { return opt; }
    public NetAttr getNetAttr() { return netAttr; }
    public MsgInfo getMsgInfo() { return msgInfo; }
}
