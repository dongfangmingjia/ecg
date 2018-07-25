package com.ljfth.ecgviewlib;

import android.hardware.usb.UsbDeviceConnection;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ljfth.ecgviewlib.base.BaseActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class DevicesActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] trabsfer = new byte[256];
                    Algorithm4Library.GetCmdResult(0, trabsfer);

                    break;
            }

        }
    };

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_devices;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mTitle.setText("设备连接");
        initRecyclerView();
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);

    }


    @Override
    protected void onResume() {
        super.onResume();
        startIoManager();

        if (mPort == null) {

        } else {
            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
            if (connection == null) {

                return;
            }

            try {
                mPort.open(connection);
                mPort.setParameters(230400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Toast.makeText(DevicesActivity.this, "Error setting up device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    mPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mPort = null;
                return;
            }

            byte[] transfer = new byte[256];
            Algorithm4Library.GeneralCmd4Dev(0, transfer);
            byte b = transfer[0];
            if (b > 0) {
                byte[] bytes = new byte[b];
                System.arraycopy(transfer, 1, bytes, 0, b);
                writeIoManage(bytes);
                mHandler.sendEmptyMessageAtTime(1, 500);
            }

        }
        onDeviceStateChange();
    }

    @OnClick({R.id.btn_back, R.id.btn_search, R.id.btn_connect, R.id.btn_detection, R.id.btn_break,
            R.id.btn_restoration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                break;
            case R.id.btn_connect:
                break;
            case R.id.btn_detection:
                break;
            case R.id.btn_break:
                break;
            case R.id.btn_restoration:
                break;
        }
    }
}
