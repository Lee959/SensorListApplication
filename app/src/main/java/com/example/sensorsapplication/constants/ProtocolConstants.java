package com.example.sensorsapplication.constants;

public class ProtocolConstants {
    public static class MessageType {
        public static final String QUERY = "query";
        public static final String QUERY_RET = "query_ret";
        public static final String CTRL = "ctrl";
        public static final String CTRL_RET = "ctrl_ret";
    }

    public static class Operation {
        public static final String GET_NET_INFO = "get_net_info";
        public static final String GET_NODE_DATA = "get_node_data";
        public static final String SET_NODE_STATUS = "set_node_status";
        public static final String AUTO_REPORT = "auto_report";
        public static final String STOP_AUTO_REPORT = "stop_auto_report";
    }

    public static class NetworkType {
        public static final String ZIGBEE = "Zigbee";
    }

    public static class VirtualReal {
        public static final String VIRTUAL = "virtual";
        public static final String REAL = "real";
    }

    public static class DeviceType {
        public static final int TEMP_HUMIDITY_SENSOR = 0;
        public static final int LIGHT_SENSOR = 1;
        public static final int ULTRASONIC_SENSOR = 2;
        public static final int PRESSURE_SENSOR = 3;
        public static final int GAS_SENSOR = 4;
        public static final int RGB_LED = 5;
        public static final int PIR_SENSOR = 8;
        public static final int MOTOR = 9;
        public static final int ELECTRIC_CURTAIN = 10;
        public static final int THREE_WAY_SWITCH = 11;
    }
}