package com.example.sensorsapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sensorsapplication.model.NetInfo;
import com.example.sensorsapplication.model.NodeInfo;
import com.example.sensorsapplication.network.response.AutoReportResponse;
import com.example.sensorsapplication.network.response.GetNodeDataResponse;
import com.example.sensorsapplication.network.response.SetNodeStatusResponse;
import com.example.sensorsapplication.util.SensorParserUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.example.sensorsapplication.network.client.ClientManager;
import com.example.sensorsapplication.network.response.GetNetInfoResponse;
import com.example.sensorsapplication.constants.ProtocolConstants;
import com.example.sensorsapplication.interfaces.MessageCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MessageCallback {

    private static final String TAG = "MainActivity";
    private static final String SERVER_IP = "10.0.2.2";
    private static final int SERVER_PORT = 1234;

    private static final String ZIGBEE_NET_NAME = "ZigbeeNet";
    private static final String ZIGBEE_PAN_ID = "1234";
    private static final String ZIGBEE_CHANNEL_ID = "10";

    private ListView snesorListView;

    private ClientManager clientManager;
    private Gson gson;
    private Handler mainThreadHandler;


    // Sensor management
    private List<NodeInfo> sensorList;
    private SensorAdapter sensorAdapter;

    // Connection state
    private boolean isConnectedToServer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "=== MainActivity onCreate START ===");
        Log.d(TAG, "Server Config: " + SERVER_IP + ":" + SERVER_PORT);
        Log.d(TAG, "Zigbee Network: " + ZIGBEE_NET_NAME + ", PAN: " + ZIGBEE_PAN_ID + ", Channel: " + ZIGBEE_CHANNEL_ID);

        mainThreadHandler = new Handler(Looper.getMainLooper());
        gson = new Gson();
        clientManager = ClientManager.getInstance();
        clientManager.setMessageCallback(this);

        snesorListView = findViewById(R.id.sensor_list);


        sensorList = new ArrayList<>();
        sensorAdapter = new SensorAdapter(this, sensorList);
        snesorListView.setAdapter(sensorAdapter);


        connectAndQueryInitialStatus();
        Log.d(TAG, "=== MainActivity onCreate COMPLETE ===");
    }

    private void connectAndQueryInitialStatus() {
        Log.d(TAG, "=== STARTING SERVER CONNECTION ===");

        new Thread(() -> {
            try {
                Log.d(TAG, "Attempting to connect to " + SERVER_IP + ":" + SERVER_PORT);
                boolean connected = clientManager.connect(SERVER_IP, SERVER_PORT);

                runOnUiThread(() -> {
                    if (connected) {
                        isConnectedToServer = true;
                        Toast.makeText(MainActivity.this, "Connected to server!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "SUCCESS: Connected to server");

                        queryNetworkInfo();
                    } else {
                        isConnectedToServer = false;
                        Toast.makeText(MainActivity.this, "Failed to connect to server.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "ERROR: Failed to connect to server");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception during connection", e);
                runOnUiThread(() -> {
                    isConnectedToServer = false;
                    Toast.makeText(MainActivity.this, "Connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void queryNetworkInfo() {
        if (!isConnectedToServer) {
            Toast.makeText(this, "Not connected to server.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "=== QUERYING NETWORK INFO ===");

        new Thread(() -> {
            try {
                clientManager.queryZigbeeNetworkInfo(
                        ZIGBEE_NET_NAME,
                        ZIGBEE_PAN_ID,
                        ZIGBEE_CHANNEL_ID
                );
                Log.d(TAG, "Network info query sent successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error querying network info", e);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error querying network: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /**
     * 为所有传感器启动自动上报功能
     */
    private void startAutoReportForAllSensors() {
        if (!isConnectedToServer || sensorList.isEmpty()) {
            Log.w(TAG, "Cannot start auto report: not connected or no sensors");
            return;
        }

        Log.d(TAG, "=== STARTING AUTO REPORT FOR ALL SENSORS ===");

        new Thread(() -> {
            // 为每个传感器启动自动上报，每1秒更新一次
            for (NodeInfo sensor : sensorList) {
                try {
                    clientManager.startZigbeeAutoReport(
                            ZIGBEE_NET_NAME,
                            ZIGBEE_PAN_ID,
                            ZIGBEE_CHANNEL_ID,
                            sensor.getNodeAddrStr(),
                            "1000" // 1000ms = 1秒间隔
                    );

                    Log.d(TAG, "Sent auto report request for sensor: " + sensor.getNodeAddrStr());

                    // 避免同时发送太多请求，稍微延迟
                    Thread.sleep(100);

                } catch (Exception e) {
                    Log.e(TAG, "Error starting auto report for sensor " + sensor.getNodeAddrStr(), e);
                }
            }
        }).start();
    }

    /**
     * 停止所有传感器的自动上报功能
     */
    private void stopAutoReportForAllSensors() {
        if (!isConnectedToServer || sensorList.isEmpty()) {
            Log.w(TAG, "Cannot stop auto report: not connected or no sensors");
            return;
        }

        Log.d(TAG, "=== STOPPING AUTO REPORT FOR ALL SENSORS ===");

        new Thread(() -> {
            for (NodeInfo sensor : sensorList) {
                try {
                    clientManager.stopZigbeeAutoReport(
                            ZIGBEE_NET_NAME,
                            ZIGBEE_PAN_ID,
                            ZIGBEE_CHANNEL_ID,
                            sensor.getNodeAddrStr()
                    );

                    Log.d(TAG, "Sent stop auto report request for sensor: " + sensor.getNodeAddrStr());
                    Thread.sleep(50);

                } catch (Exception e) {
                    Log.e(TAG, "Error stopping auto report for sensor " + sensor.getNodeAddrStr(), e);
                }
            }
        }).start();
    }

    private void updateDeviceValue(String nodeAddr, int newValue) {
        for (NodeInfo sensor : sensorList) {
            if (sensor.getNodeAddrStr().equals(nodeAddr)) {
                sensor.setSsrStatus(String.valueOf(newValue));

                sensorAdapter.notifyDataSetChanged();

                String formattedValue = SensorParserUtil.parseStatusData(sensor.getSsrType(), String.valueOf(newValue));
                Log.d(TAG, "Updated device " + nodeAddr + " value to: " + formattedValue);
                break;
            }
        }
    }

    /*

            --- MessageCallback Implementation ---

     */

    @Override
    public void onNetworkInfoReceived(String networkInfoJson) {
        runOnUiThread(() -> {
            Log.d(TAG, "=== NETWORK INFO CALLBACK TRIGGERED ===");
            Log.d(TAG, "Raw network info length: " + (networkInfoJson != null ? networkInfoJson.length() : "null"));
            Log.d(TAG, "Raw network info: " + networkInfoJson);

            try {
                GetNetInfoResponse response = gson.fromJson(networkInfoJson, GetNetInfoResponse.class);
                if (response != null && response.getOpt().equals(ProtocolConstants.Operation.GET_NET_INFO)) {
                    processNetworkInfoResponse(response);
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing NetworkInfo JSON: " + networkInfoJson, e);
                Toast.makeText(MainActivity.this, "Error parsing network information.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error processing network info: " + networkInfoJson, e);
            }
        });
    }

    private void processNetworkInfoResponse(GetNetInfoResponse response) {
        if (response == null || response.getMsgInfo() == null || response.getMsgInfo().getChildList() == null) {
            Log.w(TAG, "Network info response is invalid or contains no child nodes.");
            sensorList.clear();
            sensorAdapter.notifyDataSetChanged();
            return;
        }

        List<NetInfo.NodeInfo> childNodes = response.getMsgInfo().getChildList();
        Log.d(TAG, "Found " + childNodes.size() + " total nodes in network");
        sensorList.clear();

        for (NetInfo.NodeInfo netNode : childNodes) {
            Log.d(TAG, "Device found - Addr: " + netNode.getNodeAddrStr() +
                    ", Type: " + netNode.getSsrType() +
                    ", Role: " + netNode.getNodeRole() +
                    ", Status: " + netNode.getSsrStatus());
        }

        for (NetInfo.NodeInfo netNode : childNodes) {
            Log.d(TAG, "Checking device " + netNode.getNodeAddrStr() + " with type " + netNode.getSsrType());
            if (netNode.getSsrType() == ProtocolConstants.DeviceType.TEMP_HUMIDITY_SENSOR ||
                    netNode.getSsrType() == ProtocolConstants.DeviceType.GAS_SENSOR ||
                    netNode.getSsrType() == ProtocolConstants.DeviceType.LIGHT_SENSOR ||
                    netNode.getSsrType() == ProtocolConstants.DeviceType.PIR_SENSOR ||
                    netNode.getSsrType() == ProtocolConstants.DeviceType.PRESSURE_SENSOR ||
                    netNode.getSsrType() == ProtocolConstants.DeviceType.ULTRASONIC_SENSOR) {
                NodeInfo sensor = new NodeInfo(
                        netNode.getNodeAddr(),
                        netNode.getNodeRole(),
                        netNode.getSsrType(),
                        netNode.getSsrStatus()
                );
                sensorList.add(sensor);
                Log.d(TAG, "Added Sensor: " + sensor.getNodeAddrStr() + " (Type: " + netNode.getSsrType() + ")");
            }
        }

        Log.d(TAG, "Total Sensor devices found: " + sensorList.size());
        sensorAdapter.notifyDataSetChanged();

        // 自动为所有传感器启动自动上报
        if (sensorList.size() > 0) {
            // 延迟一下再启动自动上报，确保UI更新完成
            mainThreadHandler.postDelayed(() -> {
                startAutoReportForAllSensors();
            }, 1000);
        }

        Log.d(TAG, "Network info processing complete - " + sensorList.size() + " Sensor devices");
    }

    @Override
    public void onNodeDataReceived(String nodeDataJson) {
        runOnUiThread(() -> {
            Log.d(TAG, "=== NODE DATA CALLBACK TRIGGERED ===");
            Log.d(TAG, "Received: " + nodeDataJson);

            try {
                GetNodeDataResponse response = gson.fromJson(nodeDataJson, GetNodeDataResponse.class);

                if (response != null && response.getOpt().equals(ProtocolConstants.Operation.GET_NODE_DATA)) {
                    GetNodeDataResponse.MsgInfo msgInfo = response.getMsgInfo();
                    if (msgInfo != null) {
                        updateDeviceValue(msgInfo.getNodeAddrStr(), msgInfo.getNodeData());
                    }
                }

            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing NodeDataResponse JSON: " + nodeDataJson, e);
                Toast.makeText(MainActivity.this, "Error parsing node data.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error processing node data: " + nodeDataJson, e);
            }
        });
    }

    @Override
    public void onStatusSetResult(String resultSetJson) {
        runOnUiThread(() -> {
            Log.d(TAG, "=== STATUS SET RESULT ===");
            Log.d(TAG, "Result: " + resultSetJson);

            try {
                SetNodeStatusResponse response = gson.fromJson(resultSetJson, SetNodeStatusResponse.class);
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing SetNodeStatusResponse JSON: " + resultSetJson, e);
                Toast.makeText(MainActivity.this, "Error parsing status update result.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error processing status set result: " + resultSetJson, e);
            }
        });
    }

    @Override
    public void onAutoReportStarted(String result) {
        Log.d(TAG, "Auto Report Started: " + result);
        runOnUiThread(() -> {
            try {
                AutoReportResponse response = gson.fromJson(result, AutoReportResponse.class);
                if (response != null && response.getMsgInfo() != null) {
                    AutoReportResponse.MsgInfo msgInfo = response.getMsgInfo();
                    if ("success".equals(msgInfo.getStatus())) {
                        Toast.makeText(MainActivity.this, "自动上报已启动", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Auto report started successfully for node: " + msgInfo.getNodeAddrStr());
                    } else {
                        Toast.makeText(MainActivity.this, "启动自动上报失败", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Auto report start failed: " + msgInfo.getStatus());
                    }
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing AutoReportResponse JSON: " + result, e);
            } catch (Exception e) {
                Log.e(TAG, "Error processing auto report start result: " + result, e);
            }
        });
    }

    @Override
    public void onAutoReportStopped(String result) {
        Log.d(TAG, "Auto Report Stopped: " + result);
        runOnUiThread(() -> {
            try {
                AutoReportResponse response = gson.fromJson(result, AutoReportResponse.class);
                if (response != null && response.getMsgInfo() != null) {
                    AutoReportResponse.MsgInfo msgInfo = response.getMsgInfo();
                    if ("success".equals(msgInfo.getStatus())) {
                        Toast.makeText(MainActivity.this, "自动上报已停止", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Auto report stopped successfully for node: " + msgInfo.getNodeAddrStr());
                    } else {
                        Toast.makeText(MainActivity.this, "停止自动上报失败", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Auto report stop failed: " + msgInfo.getStatus());
                    }
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "Error parsing AutoReportResponse JSON: " + result, e);
            } catch (Exception e) {
                Log.e(TAG, "Error processing auto report stop result: " + result, e);
            }
        });
    }

    @Override
    public void onAutoReportDataReceived(String data) {
        Log.d(TAG, "Auto Report Data Received: " + data);

        // 处理自动上报的数据并更新UI
        runOnUiThread(() -> {
            try {
                GetNodeDataResponse response = gson.fromJson(data, GetNodeDataResponse.class);
                if (response != null && response.getMsgInfo() != null) {
                    GetNodeDataResponse.MsgInfo msgInfo = response.getMsgInfo();

                    // 更新传感器数据
                    updateDeviceValue(msgInfo.getNodeAddrStr(), msgInfo.getNodeData());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing auto report data: " + data, e);
                // 如果解析失败，尝试直接调用原有的处理方法
                onNodeDataReceived(data);
            }
        });
    }

    @Override
    public void onConnectionError(String error) {
        Log.e(TAG, "Connection Error: " + error);
        runOnUiThread(() -> {
            isConnectedToServer = false;
            Toast.makeText(MainActivity.this, "Connection Error: " + error, Toast.LENGTH_LONG).show();
        });
    }


    /*

        --- Lifecycle Methods ---

     */


    @Override
    protected void onDestroy() {
        Log.d(TAG, "=== MainActivity onDestroy ===");

        stopAutoReportForAllSensors();

        if (clientManager != null) clientManager.disconnect();
        if (mainThreadHandler != null) {
            mainThreadHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");
        if (isConnectedToServer && sensorList.isEmpty()) {
            Log.d(TAG, "Resuming, list empty, querying network info.");
            queryNetworkInfo();
        }
    }
}