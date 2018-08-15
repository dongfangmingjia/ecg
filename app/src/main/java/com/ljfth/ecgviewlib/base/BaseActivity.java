package com.ljfth.ecgviewlib.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;

/**
 * Created by warner on 2017/9/29.
 */

public abstract class BaseActivity extends AppCompatActivity {

	protected UsbManager mUsbManager;
	protected UsbSerialPort mPort;
	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	private SerialInputOutputManager mSerialIoManager;
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在界面未初始化之前调用的初始化窗口
		initWindows();

		if (initArgs(getIntent().getExtras())) {
			setContentView(getcontentLayoutId());
			initWidget();
			initData();
		} else {
			finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		String action = intent.getAction();
//		if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
//			initPort();
//		}
//		if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
//			NoDeviceDetached();
//		}
//        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();
	}

	/**初始化窗口*/
	protected void initWindows() {

	}

	/**
	 * 初始化相关参数
	 * 参数正确返回true，错误返回false
	 */
	protected boolean initArgs(Bundle bundle) {
		return true;
	}

	/**获取界面资源ID*/
	protected abstract int getcontentLayoutId();

	/**初始化控件*/
	protected void initWidget() {
		ButterKnife.bind(this);
//		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//		initPort();
//
//		mReceiver = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String curItentActionName = intent.getAction();
//				if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(curItentActionName)) {
//					NoDeviceDetached();
//				}
//			}
//		};
//		// ACTION_USB_DEVICE_DETACHED 这个事件监听需要通过广播，activity监听不到
//		IntentFilter filter = new IntentFilter();
////        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
////        filter.addAction(ACTION_USB_DEVICE_PERMISSION);
//		registerReceiver(mReceiver, filter);
//
//		Log.e("test", "onCreate");
//		Algorithm4Library.InitSingleInstance();
	}

	/**初始化数据*/
	protected void initData() {

	}


	@Override
	public boolean onSupportNavigateUp() {
		// 当点击界面导航返回时，finish当前界面
		finish();
		return super.onSupportNavigateUp();
	}

	@Override
	public void onBackPressed() {
		// 获取当前activity下的所有的Fragment
		@SuppressLint("RestrictedApi")
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		// 判断是否为空
		if (fragments != null && fragments.size() > 0) {
			for (Fragment fragment :fragments) {
				// 判断是否是我们能够处理的fragment的类型
				if (fragment instanceof BaseFragment) {
					// 判断是否拦截了返回按钮
					if (((BaseFragment) fragment).onBackPressed()) {
						// 拦截返回按钮，直接return
						return;
					}
				}
			}
		}
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(mReceiver);
	}

	private void initPort() {
		List<UsbSerialDriver> drivers =
				UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
		if (drivers != null) {
			for (UsbSerialDriver driver : drivers) {
				List<UsbSerialPort> ports = driver.getPorts();
				if (ports != null && ports.size() == 1) {
					mPort = ports.get(0);
//                    Toast.makeText(this, "发现一个USB设备,Port = " + mPort, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "没有发现USB,或者USB设备超过1个", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	protected void stopIoManager() {
		if (mSerialIoManager != null) {
//            Toast.makeText(MainActivity.this, "Stopping io manager ..", Toast.LENGTH_SHORT).show();
			mSerialIoManager.stop();
			mSerialIoManager = null;
		}
	}

	protected void startIoManager() {
		if (mPort != null) {
//            Toast.makeText(MainActivity.this, "Starting io manager ..", Toast.LENGTH_SHORT).show();
			mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
			mExecutor.submit(mSerialIoManager);
		}
	}

	private final SerialInputOutputManager.Listener mListener =
			new SerialInputOutputManager.Listener() {

				@Override
				public void onRunError(Exception e) {
					Toast.makeText(BaseActivity.this, "Runner stopped.", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onNewData(final byte[] data) {
					Log.e("ecg","===========返回数据接收长度===========" + data.length);
					for (byte b : data) {
						Log.e("ecg","===========返回数据接收===========" + Integer.toHexString(b & 0xFF));
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateReceivedData(data);
						}
					});
				}
			};

	protected void updateReceivedData(byte[] data){}

	protected void NoDeviceDetached(){}

	protected void onDeviceStateChange() {
		stopIoManager();
		startIoManager();
	}

	protected void writeIoManage(byte[] array) {
		String str = String.format("array len %d", array.length);
		Log.i("test", "recv len " + str);
		if (mPort != null) {
			if (array.length > 0) {
				try {
					int nRet = mPort.write(array, 100);
					str = String.format("w succ %d", nRet);
					Log.e("test", "Serial testwrite success" + str);
				} catch (IOException e2) {
					//ignore
					str = String.format("write error ");
					Log.e("test", "Serial testwrite error" + str);
					//mTextViewHR.setText("write Error");
				}
			}
		}
	}
}
