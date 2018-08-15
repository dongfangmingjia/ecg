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
import android.util.Log;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.ljfth.ecgviewlib.Constant;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsbService extends Service {

    private UsbManager mUsbManager;
    private UsbSerialPort mPort;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private DataCallBackListener mCallBackListener;

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
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(ACTION_USB_DEVICE_PERMISSION);
        registerReceiver(mReceiver, filter);

        Log.e("test", "onCreate");
        Algorithm4Library.InitSingleInstance();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("warner", "==============onStartCommand============");
        if (mPort != null) {
            mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
            mExecutor.submit(mSerialIoManager);
            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
            if (connection != null) {
                try {
                    mPort.open(connection);
                    mPort.setParameters(230400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    Log.e("warner", "==============onStartCommand   open============");
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        mPort.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    mPort = null;
                }
                writeIoManage(GeneralSpO2Command(true));
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        Log.e("warner", "==============onDestroy============");
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    public class UsbBinder extends Binder {

        public UsbService getService() {
            return UsbService.this;
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
//            if (mCallBackListener != null) {
//                mCallBackListener.callBack(data);
//            }
            saveData(data);
        }

        @Override
        public void onRunError(Exception e) {

        }
    };

    private void saveData(byte[] data) {
        new Thread(){
            @Override
            public void run() {
                String path = Constant.SAVE_PATH + "/" + System.currentTimeMillis();

            }
        }.start();
    }

    public void writeIoManage(byte[] array) {
        String str = String.format("array len %d", array.length);
        Log.e("warner", "recv len " + str);
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

    // 打开或关闭血氧
    private byte[] GeneralSpO2Command(boolean isOpen) {

        byte[] array = new byte[10];

        array[0] = (byte) (0xAA);
        array[1] = (byte) (0xAA);
        array[2] = (byte) (0x00);
        array[3] = (byte) (0x01);
        if (isOpen) {
            array[4] = (byte) (0x42);
        } else {
            array[4] = (byte) (0x43);
        }
        array[5] = (byte) (0x00);
        array[6] = (byte) (0x00);
        array[7] = (byte) (0x00);
        array[8] = (byte) (0x55);
        array[9] = (byte) (0x55);

        return array;
    }

    public void setCallBackListener(DataCallBackListener listener) {
        this.mCallBackListener = listener;
    }

    public interface DataCallBackListener {

        void callBack(byte[] data);
    }
}
