package com.zhang.xiaofei.smartsleep.Kit.Application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class BluetoothMonitorReceiver extends BroadcastReceiver {

    public Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null){
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Toast.makeText(context,"蓝牙正在打开",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            //Toast.makeText(context,"蓝牙已经打开",Toast.LENGTH_SHORT).show();
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Toast.makeText(context,"蓝牙正在关闭",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            //Toast.makeText(context,"蓝牙已经关闭",Toast.LENGTH_SHORT).show();
                            Message messageB = new Message();
                            messageB.what = 2;
                            handler.sendMessage(messageB);
                            break;
                    }
                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Toast.makeText(context,"蓝牙设备已连接",Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Toast.makeText(context,"蓝牙设备已断开",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}
