package com.example.sensorsapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a request for auto-reporting network attributes.
 * This class is used to create a request payload for the auto-report feature,
 * which includes network attributes and message information.
 * The request is structured to be sent to a server
 * for processing auto-reporting of network data.
 **/

 
public class BaseMessage {
    @SerializedName("msg_type")
    private String msgType;

    @SerializedName("opt")
    private String opt;

    @SerializedName("net_attr")
    private NetAttr netAttr;

    // Constructors
    public BaseMessage() {}

    public BaseMessage(String msgType, String opt, NetAttr netAttr) {
        this.msgType = msgType;
        this.opt = opt;
        this.netAttr = netAttr;
    }

    // Getters and Setters
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }

    public String getOpt() { return opt; }
    public void setOpt(String opt) { this.opt = opt; }

    public NetAttr getNetAttr() { return netAttr; }
    public void setNetAttr(NetAttr netAttr) { this.netAttr = netAttr; }
}
