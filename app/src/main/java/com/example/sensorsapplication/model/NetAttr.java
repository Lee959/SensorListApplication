package com.example.sensorsapplication.model;

import com.google.gson.annotations.SerializedName;

/*
 * Represents network attributes for querying node data.
 * This class is used to create a request payload for querying network attributes,
 * which includes details such as network type, name, virtual/real judgment, PAN ID, and channel ID.
 * The request is structured to be sent to a server for processing the retrieval of network data.
 */

public class NetAttr {
    @SerializedName("net_type")          private String netType;            //需要查询的网络种类，大小写无关
    @SerializedName("net_name")          private String netName;            //需要查询的网络名称，一般是指平台中各无线网络网关板的器件名
    @SerializedName("virtual_real_judg") private String virtualRealJudg;    //real : 查询真实无线网络的信息 / virtual : 查询虚拟无线网络信息
    @SerializedName("pan_id")            private String panId;              //查询网络的panid，以十六进制字符串表示。限制值四位十六进制数
    @SerializedName("channel_id")        private String channelId;          //查询网络的信道，以十进制字符串表示，限制值0-16

    // Getter and Setter methods
    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getVirtualRealJudg() {
        return virtualRealJudg;
    }

    public void setVirtualRealJudg(String virtualRealJudg) {
        this.virtualRealJudg = virtualRealJudg;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "NetAttr{" +
                "netType='" + netType + '\'' +
                ", netName='" + netName + '\'' +
                ", virtualRealJudg='" + virtualRealJudg + '\'' +
                ", panId='" + panId + '\'' +
                ", channelId='" + channelId + '\'' +
                '}';
    }
}
