package com.zhhtao.bluedev.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhhtao.bluedev.R;

import c.b.BP;
import c.b.PListener;
import c.b.QListener;

public class PayActivity extends Activity{
	private Button btnPay,btnBack;
	private Context context;
	private RelativeLayout rl_tip, rl_payOk, rl_payFail;
	String orderId;
	ProgressDialog dialog;
	private BluetoothDevice device;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		context = this;
		device = (BluetoothDevice) getIntent().getParcelableExtra("device");
		
		// 必须先初始化
//     	BP.init(this, "ecb54b452ae84a52f7c45894cabef9af");
     	BP.init(context, "ecb54b452ae84a52f7c45894cabef9af");
     	
		btnBack = (Button) findViewById(R.id.btnBack);
		btnPay = (Button) findViewById(R.id.btnPay);
		
		rl_tip = (RelativeLayout) findViewById(R.id.rl_tip);
		rl_payOk = (RelativeLayout) findViewById(R.id.rl_payOk);
		rl_payFail = (RelativeLayout) findViewById(R.id.rl_payFail);
		
		btnPay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				payByAli();
//				enterDeviceActivity();
			}
		});
		
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}
	
	private void enterDeviceActivity() {
		Intent intent = new Intent(context, DeviceControl.class);
		intent.putExtra("device", device);
		startActivity(intent);
		finish();
	}
	
	// 调用支付宝支付
	void payByAli() {
		showDialog("正在获取订单...");
		// BmobPay.init(this, APPID);
		
		BP.pay(this, null, null, 0.02, true, new PListener() {

			// 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
			@Override
			public void unknow() {
				Toast.makeText(context, "支付结果未知,请稍后手动查询",
						Toast.LENGTH_SHORT).show();

				hideDialog();
			}

			// 支付成功,如果金额较大请手动查询确认
			@Override
			public void succeed() {
				Toast.makeText(context, "支付成功!", Toast.LENGTH_SHORT)
						.show();
				rl_payFail.setVisibility(View.INVISIBLE);
				rl_tip.setVisibility(View.INVISIBLE);
				rl_payOk.setVisibility(View.VISIBLE);
				hideDialog();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						enterDeviceActivity();
					}
				}, 500);

			}

			// 无论成功与否,返回订单号
			@Override
			public void orderId(String orderId) {
				// 此处应该保存订单号,比如保存进数据库等,以便以后查询
				PayActivity.this.orderId = orderId;
				showDialog("获取订单成功!请等待跳转到支付页面~");
			}

			// 支付失败,原因可能是用户中断支付操作,也可能是网络原因
			@Override
			public void fail(int code, String reason) {
				Toast.makeText(context, "支付中断!", Toast.LENGTH_SHORT)
						.show();
				rl_payOk.setVisibility(View.INVISIBLE);
				rl_tip.setVisibility(View.INVISIBLE);
				rl_payFail.setVisibility(View.VISIBLE);
				hideDialog();
			}
		});
	}

		// 执行订单查询
		void queryOrder() {
			showDialog("正在查询订单...");
			
			BP.query(this, orderId, new QListener() {

				@Override
				public void succeed(String status) {
					Toast.makeText(context, "查询成功!该订单状态为 : " + status,
							Toast.LENGTH_SHORT).show();
					hideDialog();
				}

				@Override
				public void fail(int code, String reason) {
					Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT)
							.show();
					hideDialog();
				}
			});
		}
		
		
		void showDialog(String message) {
			try {
				if (dialog == null) {
					dialog = new ProgressDialog(this);
					dialog.setCancelable(true);
				}
				dialog.setMessage(message);
				dialog.show();
			} catch (Exception e) {
				// 在其他线程调用dialog会报错
			}
		}

		void hideDialog() {
			if (dialog != null && dialog.isShowing())
				try {
					dialog.dismiss();
				} catch (Exception e) {
				}
		}
		
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	
}
