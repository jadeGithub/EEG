package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索到的设备列表适配器
 */
public class DevicesAdapter extends BaseAdapter {

    static class DeviceViewHolder {
        TextView tvDeviceName;
        TextView tvDeviceAddress;
    }

    private Context context;
    private List<BluetoothDevice> list;

    public DevicesAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list == null ?  0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        if(list == null){
            return null;
        }
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public View getView(int i, View view, ViewGroup viewGroup) {
        DeviceViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.devices_item,null);
            viewHolder = new DeviceViewHolder();
            viewHolder.tvDeviceName = view.findViewById(R.id.ble_name);
            viewHolder.tvDeviceAddress=view.findViewById(R.id.ble_adress);
            view.setTag(viewHolder);
        }else{
            viewHolder = (DeviceViewHolder) view.getTag();
        }

        if(list.get(i).getName() == null){
            viewHolder.tvDeviceName.setText("NULL");
        }else{
            viewHolder.tvDeviceName.setText(list.get(i).getName());
        }
        viewHolder.tvDeviceAddress.setText(list.get(i).getAddress());
        return view;
    }


    /**
     * 添加列表子项
     */
    public void addDevice(BluetoothDevice bluetoothDevice){
        if(!list.contains(bluetoothDevice)){
            list.add(bluetoothDevice);
        }
        notifyDataSetChanged(); //刷新

    }

    /**
     * 清空列表
     */
    public void clear(){
        if(list != null){
            list.clear();
        }
        notifyDataSetChanged(); //刷新
    }

}
