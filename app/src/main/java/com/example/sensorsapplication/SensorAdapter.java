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

import java.util.List;


public class SensorAdapter extends ArrayAdapter<NodeInfo> {

    private Context context;
    private List<NodeInfo> deviceList;

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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        NodeInfo device = deviceList.get(position);
        setDeviceIcon(viewHolder.deviceIcon, device.getSsrType());

        viewHolder.deviceName.setText(device.getNodeInfoStr(device.getSsrType()));
        viewHolder.deviceValue.setText(SensorDataParserUtil.parseSensorData(device.getSsrType(), device.getSsrStatus()));

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
    }
}