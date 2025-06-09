package com.example.sensorsapplication.util;


import com.example.sensorsapplication.constants.ProtocolConstants;

public class SensorParserUtil {
    public static String parseStatusData(int deviceType, String rawData) {
        switch (deviceType) {
            case ProtocolConstants.DeviceType.TEMP_HUMIDITY_SENSOR:
                String[] parts = String.valueOf(rawData).split("_");
                parts[0] = String.valueOf(Integer.parseInt(parts[0]));
                parts[1] = String.valueOf(Integer.parseInt(parts[1]));
                return parts[0] + "°C / " + parts[1] + "%";

            case ProtocolConstants.DeviceType.PIR_SENSOR:
                return Integer.parseInt(rawData) == 1 ? "检测到人" : "无人";

            case ProtocolConstants.DeviceType.LIGHT_SENSOR:
                return Integer.parseInt(rawData) + " lux";

            case ProtocolConstants.DeviceType.GAS_SENSOR:
                return Integer.parseInt(rawData) + " ppm";

            case ProtocolConstants.DeviceType.PRESSURE_SENSOR:
                return Integer.parseInt(rawData) + " Pa";

            case ProtocolConstants.DeviceType.ULTRASONIC_SENSOR:
                return Integer.parseInt(rawData) + " cm";
            default:
                return String.valueOf(Integer.parseInt(rawData)) + " units"; // Default case for other device types
        }
    }

    // helper function if the deviceType is temp_humidity_sensor it will using _ to split the string, if other will directly convert to int
    public static int parseSensorDataToInt(int deviceType, String rawData) {
        switch (deviceType) {
            case ProtocolConstants.DeviceType.TEMP_HUMIDITY_SENSOR:
                String[] parts = String.valueOf(rawData).split("_");
                return Integer.parseInt(parts[0]);

            default:
                return Integer.parseInt(rawData);
        }
    }
}
