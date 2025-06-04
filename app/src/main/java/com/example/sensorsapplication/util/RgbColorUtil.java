package com.example.sensorsapplication.util;

/**
 * Utility class for RGB color operations
 */
public class RgbColorUtil {
    
    /**
     * Extracts RGB components from a status value
     * @param status The RGB status value
     * @return int array with [red, green, blue] values
     */
    public static int[] extractRgbComponents(int status) {
        int[] rgb = new int[3];
        rgb[0] = (status >> 16) & 0xFF; // Red
        rgb[1] = (status >> 8) & 0xFF;  // Green
        rgb[2] = status & 0xFF;         // Blue
        return rgb;
    }
    
    /**
     * Creates an RGB status value from individual components
     * @param red Red component (0-255)
     * @param green Green component (0-255)
     * @param blue Blue component (0-255)
     * @return Combined RGB status value
     */
    public static int createRgbStatus(int red, int green, int blue) {
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }
    
    /**
     * Checks if the RGB light is on (any component > 0)
     * @param status The RGB status value
     * @return true if the light is on, false otherwise
     */
    public static boolean isLightOn(int status) {
        return status != 0;
    }
}