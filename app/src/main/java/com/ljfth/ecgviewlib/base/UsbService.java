package com.ljfth.ecgviewlib.base;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Debug;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.ljfth.ecgviewlib.Constant;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsbService extends Service {

    private UsbManager mUsbManager;
    private UsbSerialPort mPort;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private long mReciveDataTime = 0;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String curItentActionName = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(curItentActionName)) {
                // 硬件断开连接
                if (mSerialIoManager != null) {
                    mSerialIoManager.stop();
                    mSerialIoManager = null;
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(curItentActionName)) {
                // 硬件连接
                if (mPort != null) {
                    mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
                    mExecutor.submit(mSerialIoManager);
                }
            }
        }
    };

    private UsbBinder mUsbBinder = new UsbBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mUsbBinder;
    }


    @Override
    public void onCreate() {
        Log.e("warner", "==============onCreate============");
        super.onCreate();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        initPort();

        // ACTION_USB_DEVICE_DETACHED 这个事件监听需要通过广播，activity监听不到
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceiver, filter);

        Algorithm4Library.InitSingleInstance();

        connection();

    }

    /**
     * 连接USB驱动
     */
    private void connection() {
        if (mPort != null) {
            mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
            mExecutor.submit(mSerialIoManager);
            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
            if (connection != null) {
                try {
                    mPort.open(connection);
                    mPort.setParameters(230400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    Log.e("warner", "==============connection   open============");
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        mPort.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    mPort = null;
                }
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("warner", "==============onStartCommand============");
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        Log.e("warner", "==============onDestroy============");
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    public class UsbBinder extends Binder {

        public void writeManage(byte[] array) {
            writeIoManage(array);
        }

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

    /**
     * 硬件连接后数据接收的回调
     */
    private SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

        @Override
        public void onNewData(byte[] data) {
            Log.e("warner", "============接收数据==========" + data.length);
            saveData(data);
        }

        @Override
        public void onRunError(Exception e) {
            Log.e("warner", "============接收数据 Exception==========");
            e.printStackTrace();
        }
    };

    private void saveData(final byte[] data) {
        new Thread(){
            @Override
            public void run() {
                if (TextUtils.isEmpty(Constant.SAVE_PATH)) {
                    return;
                }
                String path = null;
                if (System.currentTimeMillis() - mReciveDataTime > 60000) {
                    path = Constant.SAVE_PATH + "/" + System.currentTimeMillis();
                }
                writeData2File(path, data);
            }
        }.start();
    }

    /**写数据到文件*/
    private void writeData2File(String path, byte[] data) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            FileOutputStream outputStream = openFileOutput(path, MODE_PRIVATE);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeIoManage(byte[] array) {
        String str = String.format("array len %d", array.length);
        Log.e("warner", "recv len " + str);
        Log.e("warner", "mPort " + mPort);
        if (mPort != null) {
            if (array.length > 0) {
                try {
                    int nRet = mPort.write(array, 100);
                    str = String.format("w succ %d", nRet);
                    Log.e("warner", "Serial testwrite success" + str);
                } catch (IOException e2) {
                    //ignore
                    str = String.format("write error ");
                    Log.e("warner", "Serial testwrite error" + str);
                    //mTextViewHR.setText("write Error");
                }
            }
        }
    }
}
