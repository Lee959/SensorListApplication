package com.example.sensorsapplication.network.client;


import com.example.sensorsapplication.constants.ProtocolConstants;
import com.example.sensorsapplication.interfaces.MessageCallback;
import com.example.sensorsapplication.model.NetAttr;
import com.example.sensorsapplication.network.request.AutoReportRequest;
import com.example.sensorsapplication.network.request.GetNetInfoRequest;
import com.example.sensorsapplication.network.request.GetNodeDataRequest;
import com.example.sensorsapplication.network.request.SetNodeStatusRequest;
import com.example.sensorsapplication.network.request.StopAutoReportRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TcpClient {
    private static final String TAG = "TcpClient";
    private static final long JSON_COMPLETION_TIMEOUT_MS = 3000;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Gson gson;
    private boolean isConnected = false;
    private ExecutorService executorService;
    private MessageCallback messageCallback;

    private ScheduledExecutorService scheduledExecutor;
    private ScheduledFuture<?> currentTimeoutTask;
    private final Object timeoutLock = new Object();
    private volatile boolean jsonProcessed = false;

    public TcpClient() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        executorService = Executors.newSingleThreadExecutor();
        scheduledExecutor = Executors.newScheduledThreadPool(1);
        System.out.println(TAG + ": TcpClient initialized - FIXED VERSION with timeout handling");
    }

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
        System.out.println(TAG + ": Message callback set");
    }

    /*
     * Connect to the server at the specified host and port.
     * Returns true if the connection was successful, false otherwise.
     * @param String host - The server's hostname or IP address.
     * @param int port - The server's port number.
     */
    public boolean connect(String host, int port) {
        try {
            System.out.println(TAG + ": Attempting to connect to " + host + ":" + port);
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            isConnected = true;

            System.out.println(TAG + ": Successfully connected to server: " + host + ":" + port);

            executorService.submit(this::receiveMessages);

            return true;
        } catch (IOException e) {
            System.err.println(TAG + ": Failed to connect to server: " + e.getMessage());
            if (messageCallback != null) {
                messageCallback.onConnectionError("Failed to connect: " + e.getMessage());
            }
            return false;
        }
    }

    /*
     * Disconnect from the server and clean up resources.
     */
    public void disconnect() {
        System.out.println(TAG + ": Disconnecting from server");
        isConnected = false;

        // Cancel any pending timeout
        synchronized (timeoutLock) {
            if (currentTimeoutTask != null) {
                currentTimeoutTask.cancel(false);
                currentTimeoutTask = null;
            }
        }

        if (socket != null) {
            try {
                socket.close();
                System.out.println(TAG + ": Socket closed successfully");
            } catch (IOException e) {
                System.err.println(TAG + ": Error closing socket: " + e.getMessage());
            }
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println(TAG + ": Executor service shut down");
        }
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdownNow();
        }
    }

    /*
     * Check if the client is currently connected to the server.
     * @return boolean - true if connected, false otherwise.
     */
    public boolean isConnected() {
        return isConnected && socket != null && socket.isConnected() && !socket.isClosed();
    }

 
    private void receiveMessages() {
        System.out.println(TAG + ": Starting FIXED message receiving thread with timeout handling");

        StringBuilder jsonBuffer = new StringBuilder();
        int braceDepth = 0;
        boolean insideJson = false;

        try {
            String line;
            while (isConnected && (line = reader.readLine()) != null) {
                System.out.println(TAG + ": RAW_LINE_READ: " + line);
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) {
                    continue;
                }

                jsonBuffer.append(trimmedLine).append(" ");

                for (char c : trimmedLine.toCharArray()) {
                    if (c == '{') {
                        if (braceDepth == 0) {
                            insideJson = true;
                            jsonProcessed = false;
                            System.out.println(TAG + ": Starting new JSON object reconstruction");
                            startJsonTimeout(jsonBuffer);
                        }
                        braceDepth++;
                    } else if (c == '}') {
                        braceDepth--;
                    }
                }

                if (insideJson && braceDepth == 0) {
                    String completeJson = jsonBuffer.toString().trim();
                    System.out.println(TAG + ": ===== JSON OBJECT COMPLETE =====");
                    System.out.println(TAG + ": JSON LENGTH: " + completeJson.length());
                    System.out.println(TAG + ": COMPLETE JSON: " + completeJson);

                    jsonProcessed = true;
                    cancelJsonTimeout();

                    if (isCompleteJson(completeJson)) {
                        System.out.println(TAG + ": JSON VALIDATION PASSED - Processing message");
                        handleServerMessage(completeJson);
                    } else {
                        System.err.println(TAG + ": JSON VALIDATION FAILED");
                    }

                    jsonBuffer.setLength(0);
                    braceDepth = 0;
                    insideJson = false;
                    jsonProcessed = false;
                }

                if (jsonBuffer.length() > 10000) {
                    System.err.println(TAG + ": Buffer too large, clearing");
                    cancelJsonTimeout();
                    jsonBuffer.setLength(0);
                    braceDepth = 0;
                    insideJson = false;
                    jsonProcessed = false;
                }
            }
        } catch (IOException e) {
            if (isConnected) {
                System.err.println(TAG + ": Error receiving messages: " + e.getMessage());
                if (messageCallback != null) {
                    messageCallback.onConnectionError("Connection lost: " + e.getMessage());
                }
                isConnected = false;
            }
        } finally {
            cancelJsonTimeout();
        }

        System.out.println(TAG + ": Message receiving thread ended");
    }

    /**
     * Start a timeout for the current JSON being reconstructed
     * @param buffer The StringBuilder containing the current JSON being reconstructed
     * This method schedules a timeout task that will run after a specified period
     */
    private void startJsonTimeout(final StringBuilder buffer) {
        synchronized (timeoutLock) {
            if (currentTimeoutTask != null) {
                currentTimeoutTask.cancel(false);
            }

            currentTimeoutTask = scheduledExecutor.schedule(() -> {
                if (jsonProcessed) {
                    System.out.println(TAG + ": JSON already processed, skipping timeout handler");
                    return;
                }

                String currentBuffer = buffer.toString();
                System.out.println(TAG + ": JSON reconstruction timeout after " + JSON_COMPLETION_TIMEOUT_MS + "ms");

                int depth = 0;
                for (char c : currentBuffer.toCharArray()) {
                    if (c == '{') depth++;
                    else if (c == '}') depth--;
                }

                System.out.println(TAG + ": Buffer length: " + currentBuffer.length() + " characters");
                System.out.println(TAG + ": Missing " + depth + " closing brackets");

                tryProcessIncompleteJson(currentBuffer, depth);
            }, JSON_COMPLETION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Cancel the current JSON timeout
     */
    private void cancelJsonTimeout() {
        synchronized (timeoutLock) {
            if (currentTimeoutTask != null) {
                currentTimeoutTask.cancel(false);
                currentTimeoutTask = null;
            }
        }
    }

    /**
     * Attempt to process incomplete JSON for diagnostic purposes
     * @param incompleteJson The potentially incomplete JSON string
     * @param missingBrackets The number of missing closing brackets
     */
    private void tryProcessIncompleteJson(String incompleteJson, int missingBrackets) {
        System.out.println(TAG + ": Attempting to process potentially incomplete JSON");
        System.out.println(TAG + ": JSON length: " + incompleteJson.length() + " characters");

        if (isCompleteJson(incompleteJson)) {
            System.out.println(TAG + ": JSON is actually complete - processing");
            handleServerMessage(incompleteJson);
            return;
        }

        int openCurly = 0, closeCurly = 0;
        for (char c : incompleteJson.toCharArray()) {
            if (c == '{') openCurly++;
            else if (c == '}') closeCurly++;
        }

        System.out.println(TAG + ": Bracket count - { : " + openCurly + ", } : " + closeCurly);

        int missingCurly = openCurly - closeCurly;
        System.out.println(TAG + ": Missing " + missingCurly + " curly braces");

        // Try to complete the JSON by adding missing braces
        StringBuilder completedJson = new StringBuilder(incompleteJson.trim());
        for (int i = 0; i < missingCurly; i++) {
            completedJson.append(" }");
        }

        String attempted = completedJson.toString();
        System.out.println(TAG + ": Attempting to parse completed JSON");

        if (isCompleteJson(attempted)) {
            System.out.println(TAG + ": Successfully completed JSON - processing");
            handleServerMessage(attempted);
        } else {
            System.err.println(TAG + ": Could not repair incomplete JSON");
            System.err.println(TAG + ": Incomplete JSON was: " + incompleteJson);
        }
    }

    /**
     * Check if a string contains a complete, valid JSON object
     * @param jsonString The string to check
     */
    private boolean isCompleteJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            System.err.println(TAG + ": JSON validation failed - null or empty");
            return false;
        }

        try {
            int depth = 0;
            for (char c : jsonString.toCharArray()) {
                if (c == '{') depth++;
                else if (c == '}') depth--;
                if (depth < 0) {
                    System.err.println(TAG + ": JSON validation failed - negative brace depth");
                    return false;
                }
            }
            if (depth != 0) {
                System.err.println(TAG + ": JSON validation failed - unbalanced braces (depth: " + depth + ")");
                return false;
            }

            JsonObject parsed = JsonParser.parseString(jsonString).getAsJsonObject();
            boolean hasRequiredFields = parsed.has("msg_type") && parsed.has("opt");

            if (hasRequiredFields) {
                System.out.println(TAG + ": JSON validation PASSED - msg_type: " +
                        parsed.get("msg_type").getAsString() + ", opt: " + parsed.get("opt").getAsString());
            } else {
                System.err.println(TAG + ": JSON validation failed - missing required fields (msg_type/opt)");
            }

            return hasRequiredFields;
        } catch (JsonSyntaxException e) {
            System.err.println(TAG + ": JSON validation failed - Syntax error: " + e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            System.err.println(TAG + ": JSON validation failed - Not a JSON object: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println(TAG + ": JSON validation failed - Unexpected error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handle a complete server message
     * @param message The complete JSON message received from the server
     * This method processes the message and calls the appropriate callback methods
     * based on the message type and operation.
     */
    private void handleServerMessage(String message) {
        try {
            System.out.println(TAG + ": ===== PROCESSING SERVER MESSAGE =====");
            System.out.println(TAG + ": Message length: " + message.length());
            System.out.println(TAG + ": Message content: " + message);

            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

            if (!jsonObject.has("msg_type") || !jsonObject.has("opt")) {
                System.err.println(TAG + ": Invalid message format - missing required fields");
                return;
            }

            String msgType = jsonObject.get("msg_type").getAsString();
            String opt = jsonObject.get("opt").getAsString();

            System.out.println(TAG + ": Message type: '" + msgType + "', Operation: '" + opt + "'");

            if (messageCallback == null) {
                System.err.println(TAG + ": ERROR - Message callback is null!");
                return;
            }

            switch (msgType) {
                case ProtocolConstants.MessageType.QUERY_RET:
                    System.out.println(TAG + ": Routing to QUERY_RET handler");
                    handleQueryResponse(opt, message);
                    break;
                case ProtocolConstants.MessageType.CTRL_RET:
                    System.out.println(TAG + ": Routing to CTRL_RET handler");
                    handleControlResponse(opt, message);
                    break;
                default:
                    System.err.println(TAG + ": Unknown or unhandled message type: '" + msgType + "' with opt: '" + opt + "'");
                    if (messageCallback != null && opt.equals(ProtocolConstants.Operation.AUTO_REPORT) && !msgType.endsWith("_ret")) {
                        messageCallback.onAutoReportDataReceived(message);
                    }
                    break;
            }

        } catch (JsonSyntaxException e) {
            System.err.println(TAG + ": JSON parsing error in handleServerMessage: " + e.getMessage() + " --- JSON: " + message);
        } catch (IllegalStateException e) {
            System.err.println(TAG + ": Not a JSON object in handleServerMessage: " + e.getMessage() + " --- JSON: " + message);
        }
        catch (Exception e) {
            System.err.println(TAG + ": Unexpected error handling server message: " + e.getMessage() + " --- JSON: " + message);
            e.printStackTrace();
        }
    }


    /**
     * Handle query responses
     * @param opt The operation type from the query response
     * @param message The complete JSON message received from the server
     * This method processes the query response based on the operation type
     * and calls the appropriate callback methods.
     */
    private void handleQueryResponse(String opt, String message) {
        System.out.println(TAG + ": ===== HANDLING QUERY RESPONSE =====");
        System.out.println(TAG + ": Operation: '" + opt + "'");

        switch (opt) {
            case ProtocolConstants.Operation.GET_NET_INFO:
                System.out.println(TAG + ": Processing GET_NET_INFO response - calling onNetworkInfoReceived");
                if (messageCallback != null) {
                    messageCallback.onNetworkInfoReceived(message);
                    System.out.println(TAG + ": onNetworkInfoReceived callback invoked successfully");
                } else {
                    System.err.println(TAG + ": Cannot call onNetworkInfoReceived - callback is null");
                }
                break;

            case ProtocolConstants.Operation.GET_NODE_DATA:
                System.out.println(TAG + ": Processing GET_NODE_DATA response - calling onNodeDataReceived");
                if (messageCallback != null) {
                    messageCallback.onNodeDataReceived(message);
                    System.out.println(TAG + ": onNodeDataReceived callback invoked successfully");
                } else {
                    System.err.println(TAG + ": Cannot call onNodeDataReceived - callback is null");
                }
                break;

            default:
                System.err.println(TAG + ": Unknown query operation: '" + opt + "'");
                break;
        }
    }

    /**
     * Handle control responses
     * @param opt The operation type from the control response
     * @param message The complete JSON message received from the server
     */
    private void handleControlResponse(String opt, String message) {
        System.out.println(TAG + ": ===== HANDLING CONTROL RESPONSE =====");
        System.out.println(TAG + ": Operation: '" + opt + "'");

        if (messageCallback == null) {
            System.err.println(TAG + ": Cannot handle control response - callback is null");
            return;
        }

        switch (opt) {
            case ProtocolConstants.Operation.SET_NODE_STATUS:
                System.out.println(TAG + ": Calling onStatusSetResult");
                messageCallback.onStatusSetResult(message);
                break;
            case ProtocolConstants.Operation.AUTO_REPORT:
                System.out.println(TAG + ": Calling onAutoReportStarted");
                messageCallback.onAutoReportStarted(message);
                break;
            case ProtocolConstants.Operation.STOP_AUTO_REPORT:
                System.out.println(TAG + ": Calling onAutoReportStopped");
                messageCallback.onAutoReportStopped(message);
                break;
            default:
                System.err.println(TAG + ": Unknown control operation: '" + opt + "'");
                break;
        }
    }

    /*
     * Send a message to the server.
     * @param String message - The message to send.
     */
    public void sendMessage(final String message) {
        if (!isConnected() || writer == null) {
            System.err.println(TAG + ": Cannot send message - not connected or writer not available");
            if (messageCallback != null) {
                messageCallback.onConnectionError("Not connected to server");
            }
            return;
        }

        // Send synchronously on the calling thread for reliability
        try {
            System.out.println(TAG + ": SENDING MESSAGE TO SERVER: " + message);
            writer.println(message);
            writer.flush();
            System.out.println(TAG + ": Message sent successfully");
        } catch (Exception e) {
            System.err.println(TAG + ": Error sending message: " + e.getMessage());
            if (messageCallback != null) {
                messageCallback.onConnectionError("Error sending message: " + e.getMessage());
            }
        }
    }


    /*
     * Query com.example.gatewayapplicaton_version2.network information.
     * @param String netType - The type of com.example.gatewayapplicaton_version2.network (e.g., "Zigbee").
     * @param String netName - The name of the com.example.gatewayapplicaton_version2.network.
     * @param String panId - The PAN ID of the com.example.gatewayapplicaton_version2.network.
     * @param String channelId - The channel ID of the com.example.gatewayapplicaton_version2.network.
     * This method constructs a GetNetInfoRequest and sends it to the server.
     */
    public void queryNetworkInfo(String netType, String netName, String panId, String channelId) {
        System.out.println(TAG + ": Creating network info query");

        NetAttr netAttr = new NetAttr();
        netAttr.setNetType(netType);
        netAttr.setNetName(netName);
        // As per documentation, virtual_real_judg is needed for virtual networks
        netAttr.setVirtualRealJudg(ProtocolConstants.VirtualReal.VIRTUAL); // Assuming virtual as per examples
        netAttr.setPanId(panId);
        netAttr.setChannelId(channelId);

        GetNetInfoRequest request = new GetNetInfoRequest(netAttr, "all"); // "search: all" as per doc [cite: 15]
        String messageJson = gson.toJson(request);
        sendMessage(messageJson);
    }

    /*
     * Query data for a specific node.
     * @param String netName - The name of the com.example.gatewayapplicaton_version2.network.
     * @param String panId - The PAN ID of the com.example.gatewayapplicaton_version2.network.
     * @param String channelId - The channel ID of the com.example.gatewayapplicaton_version2.network.
     * @param String nodeAddr - The address of the node to query.
     * This method constructs a GetNodeDataRequest and sends it to the server.
     */

    public void queryNodeData(String netName, String panId, String channelId, String nodeAddr) {
        System.out.println(TAG + ": Creating node data query for node " + nodeAddr);

        NetAttr netAttr = new NetAttr();
        netAttr.setNetType(ProtocolConstants.NetworkType.ZIGBEE);
        netAttr.setNetName(netName);
        netAttr.setVirtualRealJudg(ProtocolConstants.VirtualReal.VIRTUAL); // Assuming virtual
        netAttr.setPanId(panId);
        netAttr.setChannelId(channelId);

        GetNodeDataRequest request = new GetNodeDataRequest(netAttr, nodeAddr);
        String messageJson = gson.toJson(request);
        sendMessage(messageJson);
    }


    /*
     * Set the status of a node.
     * @param String netType - The type of com.example.gatewayapplicaton_version2.network (e.g., "Zigbee").
     * @param String netName - The name of the com.example.gatewayapplicaton_version2.network.
     * @param String virtualReal - Indicates if the com.example.gatewayapplicaton_version2.network is virtual or real.
     * @param String panId - The PAN ID of the com.example.gatewayapplicaton_version2.network.
     * @param String channelId - The channel ID of the com.example.gatewayapplicaton_version2.network.
     * @param String nodeAddr - The address of the node to set status for.
     * @param String setData - The data to set for the node.
     * This method constructs a SetNodeStatusRequest and sends it to the server.
     */
    public void setNodeStatus(String netType, String netName, String virtualReal,
                              String panId, String channelId, String nodeAddr, String setData) {
        System.out.println(TAG + ": Creating set node status for node " + nodeAddr + " with data " + setData);
        NetAttr netAttr = new NetAttr();
        netAttr.setNetType(netType);
        netAttr.setNetName(netName);
        netAttr.setVirtualRealJudg(virtualReal); // Use parameter
        netAttr.setPanId(panId);
        netAttr.setChannelId(channelId);

        SetNodeStatusRequest request = new SetNodeStatusRequest(netAttr, nodeAddr, setData);
        String messageJson = gson.toJson(request);
        sendMessage(messageJson);
    }


    /*
     * Start auto-reporting for a node.
     * @param String netType - The type of com.example.gatewayapplicaton_version2.network (e.g., "Zigbee").
     * @param String netName - The name of the com.example.gatewayapplicaton_version2.network.
     * @param String virtualReal - Indicates if the com.example.gatewayapplicaton_version2.network is virtual or real.
     * @param String panId - The PAN ID of the com.example.gatewayapplicaton_version2.network.
     * @param String channelId - The channel ID of the com.example.gatewayapplicaton_version2.network.
     * @param String nodeAddr - The address of the node to start auto-reporting for.
     * @param String intervalSeconds - The interval in seconds for auto-reporting.
     * This method constructs an AutoReportRequest and sends it to the server.
     */
    public void startAutoReport(String netType, String netName, String virtualReal,
                                String panId, String channelId, String nodeAddr, String intervalSeconds) {
        System.out.println(TAG + ": Creating start auto report for node " + nodeAddr + " interval " + intervalSeconds + "s");
        NetAttr netAttr = new NetAttr();
        netAttr.setNetType(netType);
        netAttr.setNetName(netName);
        netAttr.setVirtualRealJudg(virtualReal); // Use parameter
        netAttr.setPanId(panId);
        netAttr.setChannelId(channelId);

        AutoReportRequest request = new AutoReportRequest(netAttr, nodeAddr, intervalSeconds); // Pass intervalSeconds [cite: 53]
        String messageJson = gson.toJson(request);
        sendMessage(messageJson);
    }

    /*
     * Stop auto-reporting for a node.
     * @param String netType - The type of com.example.gatewayapplicaton_version2.network (e.g., "Zigbee").
     * @param String netName - The name of the com.example.gatewayapplicaton_version2.network.
     * @param String virtualReal - Indicates if the com.example.gatewayapplicaton_version2.network is virtual or real.
     * @param String panId - The PAN ID of the com.example.gatewayapplicaton_version2.network.
     * @param String channelId - The channel ID of the com.example.gatewayapplicaton_version2.network.
     * @param String nodeAddr - The address of the node to stop auto-reporting for.
     * This method constructs a StopAutoReportRequest and sends it to the server.
     */
    public void stopAutoReport(String netType, String netName, String virtualReal,
                               String panId, String channelId, String nodeAddr) {
        System.out.println(TAG + ": Creating stop auto report for node " + nodeAddr);
        NetAttr netAttr = new NetAttr();
        netAttr.setNetType(netType);
        netAttr.setNetName(netName);
        netAttr.setVirtualRealJudg(virtualReal); // Use parameter
        netAttr.setPanId(panId);
        netAttr.setChannelId(channelId);

        StopAutoReportRequest request = new StopAutoReportRequest(netAttr, nodeAddr);
        String messageJson = gson.toJson(request);
        sendMessage(messageJson);
    }
}