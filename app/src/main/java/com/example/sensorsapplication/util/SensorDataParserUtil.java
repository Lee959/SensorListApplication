package com.example.sensorsapplication.util;


import com.example.sensorsapplication.constants.ProtocolConstants;

public class SensorDataParserUtil {
    public static String parseSensorData(int deviceType, int rawData) {
        switch (deviceType) {
            case ProtocolConstants.DeviceType.TEMP_HUMIDITY_SENSOR:
                // 8498 -> 84°C / 98%
                int temperature = rawData / 100;
                int humidity = rawData % 100;
                return temperature + "°C / " + humidity + "%";

            case ProtocolConstants.DeviceType.PIR_SENSOR:
                return rawData == 1 ? "检测到人" : "无人";

            case ProtocolConstants.DeviceType.LIGHT_SENSOR:
                return rawData + " lux";

            case ProtocolConstants.DeviceType.GAS_SENSOR:
                return rawData + " ppm";

            case ProtocolConstants.DeviceType.PRESSURE_SENSOR:
                return rawData + " Pa";

            case ProtocolConstants.DeviceType.ULTRASONIC_SENSOR:
                return rawData + " cm";

            default:
                return String.valueOf(rawData);
        }
    }
}
