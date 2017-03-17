package com.zhhtao.bluedev.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhhtao.bluedev.R;
import com.zhhtao.bluedev.utils.UIUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author Administrator
 * 和设备定义协议
 * 由APP发出的控制指令格式：协议头（0xAA）+ 命令
 * 命令：0xA1-打开LED，0xA2-关闭LED，0xA3-打开蜂鸣器，0xA4-关闭蜂鸣器
 * 0xA5-获取设备所有信息，包括LED，蜂鸣器状态，温湿度。
 * 0xA6-让设备停止发送信息
 * 
 * 
 * 设定设备1s上传2次数据
 * APP接受到指令：协议头（0x55）+ 数据
 * 数据包括5个字节：
 * 第一个字节：0x01-LED为off， 0x00-LED为on
 * 第二个字节：0x00-蜂鸣器为off， 0x01-蜂鸣器为on
 * 第三个字节：湿度值
 * 第四个字节：温度值的整数部分
 * 第五个字节：温度值的小数部分
 */
public class DeviceControl extends Activity{
	protected static final int SHOW_INFO = 0;
	protected static final int STATE_CONNECTED = 1;
	protected static final int STATE_DISCONNECTED = 2;
	protected static final int ServicesDiscovered = 3;
	protected static final int SHOW_RSSI = 4;
	protected static final String TAG = "ZHT";
	
	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
	private static final String UUID_KEY_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
	
	/*
	 * 设备控制相关
	 */
	private static final byte[] CMD_LED_ON = {(byte) 0xAA,(byte) 0XA1}; 
	private static final byte[] CMD_LED_OFF = {(byte) 0xAA,(byte) 0XA2}; 
	private static final byte[] CMD_BEEP_ON = {(byte) 0xAA,(byte) 0XA3}; 
	private static final byte[] CMD_BEEP_OFF = {(byte) 0xAA,(byte) 0XA4}; 
	private static final byte[] CMD_GET_INFO = {(byte) 0xAA,(byte) 0XA5}; 
	private static final byte[] CMD_STOP_INFO = {(byte) 0xAA,(byte) 0XA6}; 
	
	private int temperaterInteger,temperatureDicemal,humidity;
	private int rssiValue = 0;
	private boolean stateLed,stateBeep;
	private static final Integer BEEP_ON = 1;
	private static final Integer BEEP_OFF = 2;
	private static final Integer LED_ON = 3;
	private static final Integer LED_OFF = 4;
	protected static final int DIALOG_PAY = 5;
	private final boolean ON = true;
	private final boolean OFF = false;
	
	private boolean connectState = false;
	protected boolean firstFlag = true;//首次连接标志
	
	private ImageView iv_beep,iv_led;
	private Button bt_beep,bt_led,bt_connect_discon;
	private TextView tv_humidity,tv_temperature, tv_state,tv_signal;
	
	private Activity context;
	private BluetoothDevice device;
	private String deviceName,deviceAddress;
	
	BluetoothAdapter mBluetoothAdapter;
	BluetoothGatt mBluetoothGatt;
//	private String mSendData,mReceiveData;
	private byte[] mSendData, mReceiveData;
	private volatile boolean sendDataPermission = false;
	private Message mMessage;
	
	
	private TextView tv_left_time;
	private Timer timer;
	private int serviceTime = 15;
	private TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			if (serviceTime == 0) {
				timer.cancel();
				mHandler.sendEmptyMessage(DIALOG_PAY);
				
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						tv_left_time.setText("剩余服务时长："+serviceTime);
					}
				});
			}
			serviceTime--;
		}
	};
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ServicesDiscovered:
				tv_state.setText("当前状态：已连接--服务可用");
				setButtonClickable(ON);
				timer = new Timer();
				timer.schedule(timerTask, 1000, 1000);
				
				if (firstFlag) {
					firstFlag = false;
					new Thread() {
						public void run() {
							while (connectState) {
								if (mBluetoothGatt != null && mBluetoothAdapter != null) {
									mBluetoothGatt.readRemoteRssi();
									SystemClock.sleep(1000);
								}
							}
						};
					}.start();
				}
				break;
			case STATE_CONNECTED:
				if (firstFlag) {
					tv_state.setText("当前状态：已连接");
					connectState = ON;
					setConnectButton(connectState);
				}
				break;
				
			case STATE_DISCONNECTED:
				tv_state.setText("当前状态：未连接");
				connectState = OFF;
				firstFlag = true;
				
				uiInit();
				break;
				
			case SHOW_INFO:
				setLed(stateLed);
				setBeep(stateBeep);
				setTemperature(temperaterInteger, temperatureDicemal);
				setHumidity(humidity);
				break;
			case SHOW_RSSI:
				setSignalValue(rssiValue );
				break;
				
			case DIALOG_PAY:
				disconnect();
				AlertDialog.Builder builder = new  AlertDialog.Builder(context);
				
				builder.setTitle("提示");
				builder.setMessage("服务时间结束，如需继续请支付费用，否则终止退出。");
				builder.setCancelable(false);
				
				builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, HomeActivityNew.class);
						startActivity(intent);
						finish();
					}
					
				});
				
				builder.setPositiveButton("去支付", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						Intent intent = new Intent(context, PayActivity.class);
//						intent.putExtra("device", device);
//						startActivity(intent);
//						finish();
					}
					
				});
				builder.create().show();
			default:
				break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		context = this;
		tv_state = (TextView) findViewById(R.id.tv_state);
		mSendData = CMD_LED_ON;
		device = (BluetoothDevice) getIntent().getParcelableExtra("device");
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		getActionBar().setTitle("设备:"+device.getName()+" 地址:"+device.getAddress());
		
		iv_beep = (ImageView) findViewById(R.id.iv_beep);
		iv_led = (ImageView) findViewById(R.id.iv_led);
		bt_beep = (Button) findViewById(R.id.bt_beep);
		bt_led = (Button) findViewById(R.id.bt_led);
		bt_connect_discon = (Button) findViewById(R.id.bt_connect_discon);
		tv_humidity = (TextView) findViewById(R.id.tv_humidity);
		tv_temperature = (TextView) findViewById(R.id.tv_temperature);
		tv_signal = (TextView) findViewById(R.id.tv_signal);
		
		tv_left_time = (TextView) findViewById(R.id.tv_left_time);
		uiInit();
		
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		bt_beep.setOnClickListener(myOnClickListener);
		bt_led.setOnClickListener(myOnClickListener);
		bt_connect_discon.setOnClickListener(myOnClickListener);
	}

	private void uiInit() {
		setBeep(OFF);
		setLed(OFF);
		setTemperature(0, 0);
		setHumidity(0);
		setConnectButton(connectState);
		setButtonClickable(OFF);
		setSignalValue(0);
	}
	
	//连接设备操作
	public void connect() {
		if (device != null) {
			connectToDevice(device.getAddress());
		}
	}
	
	//断开设备连接操作
	public void disconnect() {
		if (mBluetoothGatt != null && mBluetoothAdapter != null) {
			mBluetoothGatt.disconnect();
		}
	}
	
	//发送数据操作,貌似每次发送前都要重新连接
	//每次只能有一次
	public void sendData(byte[] CMD) {
		String date = new Date().toString();
		mSendData = CMD;
		if (device != null) {
			//发送连接设备请求，连接成功后，开始获取服务，
			//服务获取成功后才能进行发送
			connectToDevice(device.getAddress());
		}
	}
	
	public boolean connectToDevice(final String address) {
    	
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (deviceAddress != null && address.equals(deviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
            	UIUtils.showToast(context, "连接失败");
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        deviceAddress = address;
        return true;
    }

	//GATT服务回调函数
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		//连接状态改变时
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothGatt.STATE_CONNECTED) {
				mHandler.sendEmptyMessage(STATE_CONNECTED);
				// Attempts to discover services after successful connection.
                Log.e(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
                
			} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
				mHandler.sendEmptyMessage(STATE_DISCONNECTED);
			}
		}

		//发现有可用服务时 
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				mHandler.sendEmptyMessage(ServicesDiscovered);
				//向设备发送数据
				if (firstFlag) {
					BluetoothGattCharacteristic characteristic 
						= mBluetoothGatt.getService(UUID.fromString(UUID_KEY_SERVICE))
							.getCharacteristic(UUID.fromString(UUID_KEY_DATA));
					mBluetoothGatt.setCharacteristicNotification(characteristic, true);
					characteristic.setValue(CMD_GET_INFO);
					mBluetoothGatt.writeCharacteristic(characteristic);
				} else {
					sendDataToDevice();
				}
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
                UIUtils.showToast(context, "服务不可用");
            }
			
		}
		
		//APP收到device发送的数据后，会调用该函数
		//接收到的数据存放在 characteristic.getValue()
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			mReceiveData = characteristic.getValue();
			showInfo(mReceiveData);
			
			String res = new String(mReceiveData);
//			Log.e(TAG, "recive:"+new String(characteristic.getValue()));
			Log.e(TAG, "recive2:"+byteArrayToString(mReceiveData));
			super.onCharacteristicChanged(gatt, characteristic);
		}

		
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicRead(gatt, characteristic, status);
		}

		//向divice发送数据后 会回调该函数
		//即调用Gatt.writeCharacteristic(characteristic)后
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				sendDataPermission = true;
//				UIUtils.myToast(context, "指令发送成功");
				Log.e(TAG, "write success\nuuid:"+characteristic.getUuid().toString()
						+"\nvalue:"+new String(characteristic.getValue()));
			}
		}

		// RSSI（接收信号强度）Received Signal Strength Indicator
		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			super.onReadRemoteRssi(gatt, rssi, status);
			if (status == BluetoothGatt.GATT_SUCCESS) {
				rssiValue = rssi + 100;
//				Log.e(TAG, "rssi:"+rssi+"--"+(100+rssi));
				mHandler.sendEmptyMessage(SHOW_RSSI);
			}
		}
		
	};
	
	String byteArrayToString(byte[] bs) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bs) {
			builder.append(String.format("%02X ", b));
		}
		return builder.toString();
	}
	
	//该函数只能在服务建立后调用，不能直接用它来发送数据
	//在发送的过程中，一次不要发送太长的数据
	//测试时发现，超过20个字符时，PC端就接收不到数据了
	//向蓝牙设备发送数据
	//待发送的数据装载在 mSendData中，其值由发送动作进行设置
	void sendDataToDevice() {
		//每次只能有一次writeCharacteristic(characteristic)
		if (sendDataPermission == true) {
			BluetoothGattCharacteristic characteristic 
			= mBluetoothGatt.getService(UUID.fromString(UUID_KEY_SERVICE))
				.getCharacteristic(UUID.fromString(UUID_KEY_DATA));
			mBluetoothGatt.setCharacteristicNotification(characteristic, true);
			characteristic.setValue(mSendData);
		
			mBluetoothGatt.writeCharacteristic(characteristic);
			sendDataPermission = false;
		}
	}
	
	
	/*
	 * 设定设备1s上传2次数据
	 * APP接受到指令：协议头（0x55）+ 数据
	 * 数据包括5个字节：
	 * 第一个字节：0x01-LED为off， 0x00-LED为on
	 * 第二个字节：0x00-蜂鸣器为off， 0x01-蜂鸣器为on
	 * 第三个字节：湿度值
	 * 第四个字节：温度值的整数部分
	 * 第五个字节：温度值的小数部分
	 */
	//在界面显示消息
	void showInfo(byte[] mReceiveData) {
		if (mReceiveData[0] == 0x55) {//协议头
			//CRC校验
			Log.e(TAG, String.format("%02X", getCRcSum(mReceiveData)));
			if (getCRcSum(mReceiveData) == mReceiveData[6]) {
				if (mReceiveData[1] == 0x01) {
					//LED off
					stateLed = OFF;
				} else if (mReceiveData[1] == 0x00) {
					stateLed = ON;
				}
				
				if (mReceiveData[2] == 0x00) {
					//beep off
					stateBeep = OFF;
				} else if (mReceiveData[2] == 0x01) {
					stateBeep = ON;
				}
				humidity = mReceiveData[3];
				temperaterInteger = mReceiveData[4];
				temperatureDicemal = mReceiveData[5];
				mHandler.sendEmptyMessage(SHOW_INFO);
			}
		}
		
	}
	//简单的CRC校验
	static byte getCRcSum(byte[] bs) {
		byte sum=0;
		for (int i=0; i<=5; i++) {
			sum += bs[i];
		}
		return sum;
	}
	
	//按键的响应操作
	private class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_beep:
//				UIUtils.myToast(context, "beep");
				if (iv_beep.getTag() == BEEP_OFF) {
					setBeep(ON);
					sendData(CMD_BEEP_ON);
				} else {
					setBeep(OFF);
					sendData(CMD_BEEP_OFF);
				}
				break;
				
			case R.id.bt_led:
//				UIUtils.myToast(context, "led");
				if (iv_led.getTag() == LED_OFF) {
					setLed(ON);
					sendData(CMD_LED_ON);
				} else {
					setLed(OFF);
					sendData(CMD_LED_OFF);
				}
				break;
				
			case R.id.bt_connect_discon:
				if (connectState) {
					//当前为连接，需要断开
					sendData(CMD_STOP_INFO);
					setConnectButton("正在断开连接……");
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							disconnect();
						}
					}, 650);
				} else {
					//当前为断开，需要连接
					connect();
					setConnectButton("正在连接……");
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	//true on; false off
	public void setLed(boolean on) {
		if (on) {
			iv_led.setImageResource(R.drawable.ledon);
			iv_led.setTag(LED_ON);
		} else {
			iv_led.setImageResource(R.drawable.ledoff);
			iv_led.setTag(LED_OFF);
		}
	}
	
	//true on; false off
	public void setBeep(boolean on) {
		if (on) {
			iv_beep.setImageResource(R.drawable.beep_on);
			iv_beep.setTag(BEEP_ON);
		} else {
			iv_beep.setImageResource(R.drawable.beep_off);
			iv_beep.setTag(BEEP_OFF);
		}
	}
	
	//传入connectState，根据connectState设置按钮状态
	public void setConnectButton(boolean on) {
		if (on) {
			bt_connect_discon.setText("断开连接");
		} else {
			bt_connect_discon.setText("连接设备");
		}
	}
	
	public void setConnectButton(String str) {
		bt_connect_discon.setText(str);
	}
	
	//设置温度值，传入温度值的整数和小数部分
	public void setTemperature(int integer, int decimal) {
		tv_temperature.setText(integer+"."+decimal+"℃");
	}
	
	//设置湿度值
	public void setHumidity(int humidity) {
		tv_humidity.setText(humidity+"%");
	}
	
	void setButtonClickable(boolean on) {
		bt_beep.setEnabled(on);
		bt_led.setEnabled(on);
	}
	
	void setSignalValue(int rssi) {
		tv_signal.setText(rssi+"");
	}
	
	@Override
	protected void onStop() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
		super.onDestroy();
	}
}
