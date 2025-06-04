// GetNetInfoResponse.java
package com.example.sensorsapplication.network.response;

import com.example.sensorsapplication.model.NetAttr;
import com.example.sensorsapplication.model.NetInfo;
import com.google.gson.annotations.SerializedName;

/*
 * Represents a response to a request for network information.
 * This class is used to parse the response payload from the server,
 * which includes network attributes and message information.
 * The response structure is designed to be compatible with the server's response format.
 */
public class GetNetInfoResponse {
    @SerializedName("msg_type")
    private String msgType;

    @SerializedName("opt")
    private String opt;

    @SerializedName("net_attr")
    private NetAttr netAttr;

    @SerializedName("msg_info")
    private NetInfo msgInfo;

    // Getters
    public String getMsgType() { return msgType; }
    public String getOpt() { return opt; }
    public NetAttr getNetAttr() { return netAttr; }
    public NetInfo getMsgInfo() { return msgInfo; }
}