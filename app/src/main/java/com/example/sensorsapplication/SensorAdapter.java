package com.example.sensorsapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sensorsapplication.constants.ProtocolConstants;
import com.example.sensorsapplication.model.NodeInfo;
import com.example.sensorsapplication.util.SensorDataParserUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SensorAdapter extends ArrayAdapter<NodeInfo> {

    private Context context;
    private List<NodeInfo> deviceList;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public SensorAdapter(Context context, List<NodeInfo> deviceList) {
        super(context, R.layout.item_sensor, deviceList);
        this.context = context;
        this.deviceList = deviceList;
    }

    /*
     * getView method to create and return the view for each item in the list.
     * @param position The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate (if available).
     * @param parent The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sensor, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceIcon = convertView.findViewById(R.id.device_icon);
            viewHolder.deviceName = convertView.findViewById(R.id.device_name);
            viewHolder.deviceValue = convertView.findViewById(R.id.device_value);
            viewHolder.lastUpdate = convertView.findViewById(R.id.last_update);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NodeInfo device = deviceList.get(position);
        setDeviceIcon(viewHolder.deviceIcon, device.getSsrType());

        viewHolder.deviceName.setText(device.getNodeInfoStr(device.getSsrType()));
        viewHolder.deviceValue.setText(SensorDataParserUtil.parseSensorData(device.getSsrType(), device.getSsrStatus()));

        if (viewHolder.lastUpdate != null) {
            viewHolder.lastUpdate.setText(timeFormat.format(new Date()));
        }

        return convertView;
    }

    /*
     * Sets the icon for the device based on its type.
     * @param imageView The ImageView to set the icon on.
     * @param deviceType The type of the device.
     * This method updates the ImageView with the appropriate icon
     */
    private void setDeviceIcon(ImageView imageView, int deviceType) {
        switch (deviceType) {
            case (ProtocolConstants.DeviceType.TEMP_HUMIDITY_SENSOR):
                imageView.setImageResource(R.drawable.thermostat_24px);
                break;
            case (ProtocolConstants.DeviceType.LIGHT_SENSOR):
                imageView.setImageResource(R.drawable.light_sensor_24px);
                break;
            case (ProtocolConstants.DeviceType.ULTRASONIC_SENSOR):
                imageView.setImageResource(R.drawable.distance_sensor_24px);
                break;
            case (ProtocolConstants.DeviceType.PRESSURE_SENSOR):
                imageView.setImageResource(R.drawable.air_pressure_24px);
                break;
            case (ProtocolConstants.DeviceType.GAS_SENSOR):
                imageView.setImageResource(R.drawable.gas_sensor_24px);
                break;
            case (ProtocolConstants.DeviceType.PIR_SENSOR):
                imageView.setImageResource(R.drawable.motion_sensor_24px);
                break;
            default:
                imageView.setImageResource(R.drawable.device_unknown_24px);
                break;
        }
    }


    /*
     * ViewHolder class to hold the views for each item in the list.
     */
    private static class ViewHolder {
        ImageView deviceIcon;
        TextView deviceName;
        TextView deviceValue;
        TextView lastUpdate; // Added this field
    }

    /*
     * Updates the data in the adapter with a new list of devices.
     * @param newDeviceList The new list of NodeInfo objects to update the adapter with.
     */
    public void updateData(List<NodeInfo> newDeviceList) {
        this.deviceList.clear();
        this.deviceList.addAll(newDeviceList);
        notifyDataSetChanged();
    }

    /*
     * Adds a new device to the adapter.
     * @param device The NodeInfo object representing the new device to be added.
     */
    public void addDevice(NodeInfo device) {
        this.deviceList.add(device);
        notifyDataSetChanged();
    }

    /*
     * Updates an existing device in the adapter.
     * @param updatedDevice The NodeInfo object representing the updated device.
     */
    public void updateDevice(NodeInfo updatedDevice) {
        boolean found = false;
        for (int i = 0; i < deviceList.size(); i++) {
            NodeInfo currentDevice = deviceList.get(i);
            // Check if the current device matches the updated device by node address or node address string
            if (currentDevice.getNodeAddr() == updatedDevice.getNodeAddr() ||
                    (currentDevice.getNodeAddrStr() != null && currentDevice.getNodeAddrStr().equals(updatedDevice.getNodeAddrStr()))) {
                deviceList.set(i, updatedDevice);
                found = true;
                break;
            }
        }
        if (!found) { // If not found, add it
            deviceList.add(updatedDevice);
        }
        notifyDataSetChanged();
    }

    public void clearDevices() {
        this.deviceList.clear();
        notifyDataSetChanged();
    }
}