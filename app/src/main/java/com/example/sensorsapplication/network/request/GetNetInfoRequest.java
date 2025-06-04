package com.example.sensorsapplication.network.request;

import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/*
 * Represents a request to get network information.
 * This class is used to create a request payload for querying network attributes,
 * which includes the network attributes and search criteria.
 * The request is structured to be sent to a server for processing network information retrieval.
 */

public class GetNetInfoRequest {
    @SerializedName("msg_type")
    private String msgType = "query";

    @SerializedName("opt")
    private String opt = "get_net_info";

    @SerializedName("net_attr")
    private NetAttr netAttr;

    @SerializedName("msg_info")
    private MsgInfo msgInfo;

    public static class MsgInfo {
        @SerializedName("search")
        private String search;

        public MsgInfo(String search) {
            this.search = search;
        }
    }

    public GetNetInfoRequest(NetAttr netAttr, String search) {
        this.netAttr = netAttr;
        this.msgInfo = new MsgInfo(search);
    }
}
