package com.example.sensorsapplication.network.request;

import com.example.sensorsapplication.constants.ProtocolConstants;
import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/*
 * Represents a request to set the status of a node in the network.
 * This class is used to create a request payload for controlling the status of a node,
 * which includes network attributes and message information.
 * The request is structured to be sent to a server for processing the status update of a node.
 */
public class SetNodeStatusRequest {
    @SerializedName("msg_type")
    private String msgType = ProtocolConstants.MessageType.CTRL;

    @SerializedName("opt")
    private String opt = "set_node_status";

    @SerializedName("net_attr")
    private NetAttr netAttr;

    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("node_addr")
        private String nodeAddr;

        @SerializedName("set_data")
        private String setData;

        public MsgInfo(String nodeAddr, String setData) {
            this.nodeAddr = nodeAddr;
            this.setData = setData;
        }
    }

    public SetNodeStatusRequest(NetAttr netAttr, String nodeAddr, String setData) {
        this.netAttr = netAttr;
        this.msgInfo = new MsgInfo(nodeAddr, setData);
    }
}