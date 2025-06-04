package com.example.sensorsapplication.model;

import com.example.sensorsapplication.util.RgbColorUtil;
import com.google.gson.annotations.SerializedName;

/*
 * Represents network attributes for querying node data.
 * This class is used to create a request payload for querying network attributes,
 * which includes details such as network type, name, virtual/real judgment, PAN ID, and channel ID.
 * The request is structured to be sent to a server for processing the retrieval of network data.
 */
public class NodeInfo {
    @SerializedName("node_addr")     private int nodeAddr;          //节点地址，以整数表示
    @SerializedName("node_addr_str") private String nodeAddrStr;    //节点地址，十六进制字符串表示，便于查看与对照
    @SerializedName("node_role")     private String nodeRole;       //表示节点的网络类型，如"Enddevice"为zigbee终端
    @SerializedName("ssr_type")      private int ssrType;           //对应传感器/控制器的枚举编号，表示该节点连接的对应器件种类，具体请查看传感器控制器编号表
    @SerializedName("ssr_status")    private int ssrStatus;         //表示该传感器/控制器的采集值/状态值

    // RGB Light Attribute
    private int red;
    private int green;
    private int blue;

    // Constructor
    public NodeInfo(int nodeAddr, String nodeRole, int ssrType, int ssrStatus) {
        this.nodeAddr = nodeAddr;
        this.nodeAddrStr = String.format("0x%04X", nodeAddr); // Convert to hex string
        this.nodeRole = nodeRole;
        this.ssrType = ssrType;
        this.ssrStatus = ssrStatus;

        if (this.ssrType == 5) { // 5 is the device type for RGB_LED
            int[] rgbComponents = RgbColorUtil.extractRgbComponents(this.ssrStatus);
            this.red = rgbComponents[0];
            this.green = rgbComponents[1];
            this.blue = rgbComponents[2];
        }
    }

    // Getters and Setters
    public int getNodeAddr() { return nodeAddr; }

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

    public int getSsrType() {
        return ssrType;
    }

    public void setSsrType(int ssrType) {
        this.ssrType = ssrType;
    }

    public int getSsrStatus() {
        return ssrStatus;
    }

    public void setSsrStatus(int ssrStatus) {
        this.ssrStatus = ssrStatus;
        // change the function if target type if RGB Light
        if (this.ssrType == 5) { // 5 is the device type for RGB_LED
            int[] rgbComponents = RgbColorUtil.extractRgbComponents(this.ssrStatus);
            this.red = rgbComponents[0];
            this.green = rgbComponents[1];
            this.blue = rgbComponents[2];
        }
    }

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

    // Getters for R, G, B components
    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "nodeAddr=" + nodeAddr +
                ", nodeAddrStr='" + nodeAddrStr + '\'' +
                ", nodeRole='" + nodeRole + '\'' +
                ", ssrType=" + ssrType +
                ", ssrStatus=" + ssrStatus +
                (ssrType == 5 ? ", red=" + red + ", green=" + green + ", blue=" + blue : "") + // Added RGB to toString
                '}';
    }
}
