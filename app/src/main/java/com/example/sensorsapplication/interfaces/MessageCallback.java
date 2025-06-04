package com.example.sensorsapplication.interfaces;

/**
 * Enhanced MessageCallback interface with improved documentation
 * and support for real-time data updates
 */
public interface MessageCallback {

    /**
     * Called when network topology information is received
     * This contains the list of all nodes in the network
     * @param networkInfo JSON string containing network structure
     */
    void onNetworkInfoReceived(String networkInfo);

    /**
     * Called when individual node data is received
     * This includes sensor readings, device status, etc.
     * @param nodeData JSON string containing node sensor data
     */
    void onNodeDataReceived(String nodeData);

    /**
     * Called when a node status change request completes
     * @param result JSON string containing the result of status change
     */
    void onStatusSetResult(String result);

    /**
     * Called when auto-reporting is successfully started for a node
     * @param result JSON string confirming auto-report activation
     */
    void onAutoReportStarted(String result);

    /**
     * Called when auto-reporting is successfully stopped for a node
     * @param result JSON string confirming auto-report deactivation
     */
    void onAutoReportStopped(String result);

    /**
     * Called when auto-reported data is received from nodes
     * This is for real-time data that nodes send automatically
     * @param data JSON string containing automatically reported sensor data
     */
    void onAutoReportDataReceived(String data);

    /**
     * Called when there's a connection error or TCP communication issue
     * @param error String describing the error that occurred
     */
    void onConnectionError(String error);
}