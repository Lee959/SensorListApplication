package com.example.sensorsapplication.network.client;

/*
 * ClientManager.java
 * Singleton class to manage the TCP client connection and operations.
 * This class provides methods to connect, disconnect, send messages,
 * and perform Zigbee operations.
 */


import com.example.sensorsapplication.constants.ProtocolConstants;
import com.example.sensorsapplication.interfaces.MessageCallback;

public class ClientManager {
    private static ClientManager instance;
    private TcpClient tcpClient;

    private ClientManager() {
        tcpClient = new TcpClient();
    }

    /*
     * synchronized getInstance method to ensure that only one instance
     * @return the singleton instance of ClientManager.
     */
    public static synchronized ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager();
        }
        return instance;
    }

    /*
     * Sets the message callback for the TCP client.
     * @param callback the MessageCallback to handle incoming messages.
     * This method allows the client to receive messages from the server.
     * The callback will be invoked whenever a message is received.
     */

    public void setMessageCallback(MessageCallback callback) {
        tcpClient.setMessageCallback(callback);
    }

    public boolean connect(String host, int port) {
        return tcpClient.connect(host, port);
    }

    public void disconnect() {
        tcpClient.disconnect();
    }

    public boolean isConnected() {
        return tcpClient.isConnected();
    }

    /*
     * Sends a raw JSON message to the server.
     * @param jsonMessage the JSON message to be sent.
     * This method allows the client to send raw JSON messages
     */
    public void sendRawMessage(String jsonMessage) {
        tcpClient.sendMessage(jsonMessage);
    }

    /*
     * Sends a Zigbee network command to the server.
     * @param netName the name of the Zigbee network.
     * @param panId the PAN ID of the Zigbee network.
     * @param channelId the channel ID of the Zigbee network.
     * @param nodeAddr the address of the Zigbee node.
     * This method allows the client to send commands related to Zigbee networks.
     */
    public void stopZigbeeAutoReport(String netName, String panId, String channelId, String nodeAddr) {
        tcpClient.stopAutoReport(
                ProtocolConstants.NetworkType.ZIGBEE,
                netName,
                ProtocolConstants.VirtualReal.VIRTUAL,
                panId,
                channelId,
                nodeAddr
        );
    }

    /*
     * Queries the Zigbee node data from the server.
     * @param netName the name of the Zigbee network.
     * @param panId the PAN ID of the Zigbee network.
     * @param channelId the channel ID of the Zigbee network.
     * @param nodeAddr the address of the Zigbee node.
     * This method allows the client to retrieve data from a specific Zigbee node.
     */
    public void queryZigbeeNodeData(String netName, String panId, String channelId, String nodeAddr) {
        tcpClient.queryNodeData(
                netName,
                panId,
                channelId,
                nodeAddr
        );
    }


    /*
     * Sets the status of a Zigbee node.
     * @param netName the name of the Zigbee network.
     * @param panId the PAN ID of the Zigbee network.
     * @param channelId the channel ID of the Zigbee network.
     * @param nodeAddr the address of the Zigbee node.
     * @param status the status to be set for the Zigbee node.
     * This method allows the client to update the status of a specific Zigbee node.
     */
    public void setZigbeeNodeStatus(String netName, String panId, String channelId, String nodeAddr, String status) {
        tcpClient.setNodeStatus(
                ProtocolConstants.NetworkType.ZIGBEE,
                netName,
                ProtocolConstants.VirtualReal.VIRTUAL,
                panId,
                channelId,
                nodeAddr,
                status
        );
    }

    /*
     * Queries the Zigbee network information.
     * @param netName the name of the Zigbee network.
     * @param panId the PAN ID of the Zigbee network.
     * @param channelId the channel ID of the Zigbee network.
     * This method allows the client to retrieve information about a specific Zigbee network.
     */
    public void queryZigbeeNetworkInfo(String netName, String panId, String channelId) {
        tcpClient.queryNetworkInfo(
                ProtocolConstants.NetworkType.ZIGBEE,
                netName,
                panId,
                channelId
        );
    }

    /*
     * Starts the Zigbee auto-reporting feature.
     * @param netName the name of the Zigbee network.
     * @param panId the PAN ID of the Zigbee network.
     * @param channelId the channel ID of the Zigbee network.
     * @param nodeAddr the address of the Zigbee node.
     * @param interval the reporting interval in seconds.
     * This method allows the client to initiate auto-reporting for a specific Zigbee node.
     */
    public void startZigbeeAutoReport(String netName, String panId, String channelId, String nodeAddr, String interval) {
        tcpClient.startAutoReport(
                ProtocolConstants.NetworkType.ZIGBEE,
                netName,
                ProtocolConstants.VirtualReal.VIRTUAL,
                panId,
                channelId,
                nodeAddr,
                interval
        );
    }
}