package com.example.sensorsapplication.util;

/**
 * Utility class for RGB color operations
 * The RGB value is represented as a string in the format "red_green_blue"
 */
public class RgbColorUtil {
    
    /**
     * Extracts RGB components from a status value
     * @param status The RGB status value
     * @return int array with [red, green, blue] values
     */
    public static int[] extractRgbComponents(String status) {
        String[] parts = status.split("_");
        int[] rgb = new int[3];
        rgb[0] = Integer.parseInt(parts[0]); // Red
        rgb[1] = Integer.parseInt(parts[1]); // Green
        rgb[2] = Integer.parseInt(parts[2]); // Blue
        return rgb;
    }

    /**
     * Creates an RGB status value from individual components
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @return Combined RGB status value
     */
    public static String createRgbStatus(int red, int green, int blue) {
        return String.format("%d_%d_%d", red, green, blue);
    }
    
    /**
     * Checks if the RGB light is on (any component > 0)
     * @param status The RGB status value
     * @return true if the light is on, false otherwise
     */
    public static boolean isLightOn(String status) {
        int[] rgb = extractRgbComponents(status);
        return rgb[0] > 0 || rgb[1] > 0 || rgb[2] > 0;
    }
}