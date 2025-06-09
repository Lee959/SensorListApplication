package com.example.sensorsapplication.util;

public class SwitchDataConvertUtil {
    public static int[] convertSwitchStateToString(String switchState) {
        // data is parse using _, example: "02_01_03" 0 means off, > 0 means on with the number indicating the state
        String[] states = switchState.split("_");
        int[] switchChannelState = new int[3];

        switchChannelState[0] = Integer.parseInt(states[0]); // Channel 1 state
        switchChannelState[1] = Integer.parseInt(states[1]); // Channel 2 state
        switchChannelState[2] = Integer.parseInt(states[2]); // Channel 3 state

        return switchChannelState;
    }

}
