package com.example.sensorsapplication.util;


import com.example.sensorsapplication.constants.ProtocolConstants;

public class SensorDataParserUtil {
    public static String parseSensorData(int deviceType, int rawData) {
        switch (deviceType) {
            case ProtocolConstants.DeviceType.TEMP_HUMIDITY_SENSOR:
                int temperature = rawData / 100;
                int humidity = rawData % 100;
                return temperature + "°C / " + humidity + "%";

            case ProtocolConstants.DeviceType.PIR_SENSOR:
                return rawData == 1 ? "检测到人" : "无人";

            case ProtocolConstants.DeviceType.LIGHT_SENSOR:
                return rawData + " lux";

            default:
                return String.valueOf(rawData);
        }
    }
}
