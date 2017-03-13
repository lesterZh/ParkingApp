package com.zhhtao.bluedev.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.activity.PayActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangHaiTao
 * @ClassName: SearchBleFragment
 * Description: TODO
 * @date 2016/5/31 16:32
 */
public class SearchBleFragment extends Fragment {

    protected static final int SHOW_BLUE_LIST = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SCAN_PERIOD = 10000;//10s后停止扫描
    private Activity context;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private ListView lv_blueList;
    private Button btn_scan_ble;
    private Handler mHandler;

    private RelativeLayout rl_scanning,rl_no_device;

    private Handler handler2 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_BLUE_LIST:
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blue_scan, container, false);

        context = getActivity();
        mHandler = new Handler();
        mScanning = true;
        rl_scanning = (RelativeLayout) view.findViewById(R.id.rl_scanning);
        rl_no_device = (RelativeLayout) view.findViewById(R.id.rl_no_device);
        btn_scan_ble = (Button) view.findViewById(R.id.btn_scan_ble);
        btn_scan_ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanning();
            }
        });


        //判断系统是否支持蓝牙
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "系统不支持BLE", Toast.LENGTH_SHORT).show();
            context.finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "系统不支持BLE", Toast.LENGTH_SHORT).show();
            context.finish();
        }

        lv_blueList = (ListView) view.findViewById(R.id.ll_blue_scan);
        lv_blueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                BluetoothDevice device = mLeDeviceListAdapter.getDevice(arg2);
//				Intent intent = new Intent(context, DeviceControl.class);
                Intent intent = new Intent(context, PayActivity.class);
                intent.putExtra("deviceName", device.getName());
                intent.putExtra("deviceAddress", device.getAddress());
                intent.putExtra("device", device);
                startActivity(intent);
                if (mScanning == true) {
                    scanLeDevice(false);
                }
            }
        });

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        lv_blueList.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);


        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(mReceiver);
    }

    //扫描按钮操作
    public void scanning() {
        Toast.makeText(context, "开始扫描蓝牙设备", Toast.LENGTH_SHORT).show();
        mLeDeviceListAdapter.clear();
        handler2.sendEmptyMessage(SHOW_BLUE_LIST);
        scanLeDevice(true);
    }



    //扫描蓝牙设备
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(boolean enable) {

        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    mScanning = false;
                    rl_scanning.setVisibility(View.INVISIBLE);
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //未发现蓝牙设备
                    if (!mLeDeviceListAdapter.hasDevices()) {
                        rl_no_device.setVisibility(View.VISIBLE);
                    }
                }
            }, SCAN_PERIOD);//10s后停止扫描
            mScanning = true;
            rl_no_device.setVisibility(View.INVISIBLE);
            rl_scanning.setVisibility(View.VISIBLE);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //貌似startDiscovery()的设备搜索能力更强一些
            //startLeScan只搜索低功耗设备
//			mBluetoothAdapter.startDiscovery();
        } else {
            mScanning = false;
            rl_no_device.setVisibility(View.INVISIBLE);
            rl_scanning.setVisibility(View.INVISIBLE);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private class LeDeviceListAdapter extends BaseAdapter {

        /**
         * BluetoothDevice:
         * Represents a remote Bluetooth device.
         * A BluetoothDevice lets you create a connection with the respective device
         * or query information about it,
         * such as the name, address, class, and bonding state.
         */
        private List<BluetoothDevice> mLeDevices;

        LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<>();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //检测是否有蓝牙设备
        public boolean hasDevices() {
            if (mLeDevices.size() == 0)
                return false;
            else return true;
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            // TODO Auto-generated method stub
            if (convertView != null && convertView instanceof LinearLayout) {
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.item_blue, null);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.tv_address);
                convertView.setTag(viewHolder);
            }

            BluetoothDevice device = mLeDevices.get(position);
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText("unknown device");
            }

            viewHolder.deviceAddress.setText(device.getAddress());
            return convertView;
        }

        class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
        }

    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback
            = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mLeDeviceListAdapter.addDevice(device);
            handler2.sendEmptyMessage(SHOW_BLUE_LIST);
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a
                // ListView
                mLeDeviceListAdapter.addDevice(device);
                System.out.println("find device:"+device.getAddress());
                handler2.sendEmptyMessage(SHOW_BLUE_LIST);
            }
        }
    };
}
