package com.ljfth.ecgviewlib.base;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsbService extends Service {

    private UsbManager mUsbManager;
    private UsbSerialPort mPort;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private BroadcastReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        initPort();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String curItentActionName = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(curItentActionName)) {

                }
            }
        };
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
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
}
