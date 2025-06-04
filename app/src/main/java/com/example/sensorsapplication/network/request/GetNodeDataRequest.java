package com.example.sensorsapplication.network.request;

import com.example.sensorsapplication.model.NetAttr;
import com.google.gson.annotations.SerializedName;

/**
 * Request to get node data according to API specification:
 * {
 *     "msg_type": "query",
 *     "opt": "get_node_data",
 *     "net_attr": {
 *         "channel_id": "10",
 *         "net_type": "Zigbee",
 *         "pan_id": "1234",
 *         "virtual_real_judg": "virtual"
 *     },
 *     "msg_info": {
 *         "node_addr": "5571"
 *     }
 * }
 */
public class GetNodeDataRequest {
    @SerializedName("msg_type")
    private String msgType = "query";

    @SerializedName("opt")
    private String opt = "get_node_data";

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

    public GetNodeDataRequest(NetAttr netAttr, String nodeAddr) {
        this.netAttr = netAttr;
        this.msgInfo = new MsgInfo(nodeAddr);
    }
}
