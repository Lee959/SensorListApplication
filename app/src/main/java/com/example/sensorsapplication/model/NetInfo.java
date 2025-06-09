package com.example.sensorsapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Represents network information for querying node data.
 * This class is used to create a request payload for querying network information,
 * which includes details such as node address, node role, and a list of child nodes.
 * The request is structured to be sent to a server for processing the retrieval of network data.
 */
public class NetInfo {
    @SerializedName("node_addr")     private int nodeAddr;
    @SerializedName("node_addr_str") private String nodeAddrStr;
    @SerializedName("node_role")     private String nodeRole;
    @SerializedName("child_list")    private List<NodeInfo> childList;

    // Child list element --------------------------------------------------
    public static class NodeInfo {
        @SerializedName("node_addr")     private int nodeAddr;
        @SerializedName("node_addr_str") private String nodeAddrStr;
        @SerializedName("node_role")     private String nodeRole;
        @SerializedName("ssr_type")      private int ssrType;
        @SerializedName("ssr_status")    private String ssrStatus;

        // getters & setters
        public int getNodeAddr()               { return nodeAddr; }
        public void setNodeAddr(int v)         { this.nodeAddr = v; }

        public String getNodeAddrStr()         { return nodeAddrStr; }
        public void   setNodeAddrStr(String v) { this.nodeAddrStr = v; }

        public String getNodeRole()            { return nodeRole; }
        public void   setNodeRole(String v)    { this.nodeRole = v; }

        public int getSsrType()                { return ssrType; }
        public void setSsrType(int v)          { this.ssrType = v; }

        public String getSsrStatus()              { return ssrStatus; }
        public void setSsrStatus(String v)        { this.ssrStatus = v; }

        public String getNodeInfoStr(int node_type) {
            String deviceInfo;
            switch (node_type) {
                case 0:
                    deviceInfo = "温湿度传感器";
                    break;
                case 1:
                    deviceInfo = "光敏传感器";
                    break;
                case 2:
                    deviceInfo = "超声波测距模块";
                    break;
                case 3:
                    deviceInfo = "大气压力传感器";
                    break;
                case 4:
                    deviceInfo = "有害气体气体传感器";
                    break;
                case 5:
                    deviceInfo = "三色灯模块";
                    break;
                case 8:
                    deviceInfo = "人体感应传感器";
                    break;
                case 9:
                    deviceInfo = "电机模块";
                    break;
                case 10:
                    deviceInfo = "电动窗帘";
                    break;
                case 11:
                    deviceInfo = "三路开关";
                    break;
                default:
                    deviceInfo = "未知设备"; // Unknown device
                    break;
            }
            return deviceInfo;
        }
    }

    // Getter and Setter methods
    public int getNodeAddr() {
        return nodeAddr;
    }
    public void setNodeAddr(int nodeAddr) {
        this.nodeAddr = nodeAddr;
    }
    public String getNodeAddrStr() {
        return nodeAddrStr;
    }
    public void setNodeAddrStr(String nodeAddrStr) {
        this.nodeAddrStr = nodeAddrStr;
    }
    public String getNodeRole() {
        return nodeRole;
    }
    public void setNodeRole(String nodeRole) {
        this.nodeRole = nodeRole;
    }

    public List<NodeInfo> getChildList() {
        return childList;
    }
    public void setChildList(List<NodeInfo> childList) {
        this.childList = childList;
    }

    @Override
    public String toString() {
        return "NetInfo{" +
                "nodeAddr=" + nodeAddr +
                ", nodeAddrStr='" + nodeAddrStr + '\'' +
                ", nodeRole='" + nodeRole + '\'' +
                ", childList=" + childList +
                '}';
    }
}