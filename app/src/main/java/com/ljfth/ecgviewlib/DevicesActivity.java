package com.ljfth.ecgviewlib;

import android.hardware.usb.UsbDeviceConnection;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ljfth.ecgviewlib.adapter.DevicesListAdapter;
import com.ljfth.ecgviewlib.base.BaseActivity;
import com.ljfth.ecgviewlib.model.DevicesModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DevicesActivity extends BaseActivity implements DevicesListAdapter.OnItemClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;
    @BindView(R.id.text_log)
    TextView textLog;

    private int mCurrentType = 0;
    private byte[] mTransfer;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    byte[] trabsfer = new byte[256];
                    boolean bool =  Algorithm4Library.GetCmdResult(mCurrentType, trabsfer);
                    Log.e("ecg", "==============返回指令===========" + trabsfer[0] + " index1 : " + Integer.toHexString(trabsfer[1] & 0xFF));
                    textLog.setText("返回结果：：：：：：：：" + trabsfer[0]);
                    updateResultData(mCurrentType, trabsfer);
                    break;
            }

        }
    };
    private DevicesListAdapter mAdapter;

    /**
     * 处理返回的数据结果
     * @param trabsfer
     */
    private void updateResultData(int type, byte[] trabsfer) {
        if (trabsfer != null && trabsfer[0] > 0 && trabsfer[0] % 8 == 0) {
            List<DevicesModel> deviceList = new ArrayList<>();
            DevicesModel devicesModel;
            switch (type) {
                case Constant.TYPE_SEARCH:
                    Log.e("ecg", "================处理返回结果=============");
                    for (int i = 0; i < trabsfer[0] / 8; i++) {
                        devicesModel = new DevicesModel();
                        byte[] data = new byte[256];
                        StringBuilder builder = new StringBuilder();
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 1] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 2] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 3] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 4] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 5] & 0xff));
                        devicesModel.setMacStr(builder.toString());
                        devicesModel.setSignalStr("-" + String.valueOf(trabsfer[8 * i + 1 + 6] & 0xff));
                        devicesModel.setDeviceType(String.valueOf(trabsfer[8 * i + 1 + 7] & 0xff));
                        data[0] = 7;
                        data[1] = trabsfer[8 * i + 1];
                        data[2] = trabsfer[8 * i + 1 + 1];
                        data[3] = trabsfer[8 * i + 1 + 2];
                        data[4] = trabsfer[8 * i + 1 + 3];
                        data[5] = trabsfer[8 * i + 1 + 4];
                        data[6] = trabsfer[8 * i + 1 + 5];
                        data[7] = trabsfer[8 * i + 1 + 7];
                        devicesModel.setDevicesData(data);
                        deviceList.add(devicesModel);
                    }
                    break;
                case Constant.TYPE_CONNECT:
                    for (int i = 0; i < trabsfer[0] / 8; i++) {
                        devicesModel = new DevicesModel();
                        StringBuilder builder = new StringBuilder();
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 1] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 2] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 3] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 4] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 5] & 0xff));
                        devicesModel.setMacStr(builder.toString());
                        devicesModel.setDeviceType(String.valueOf(trabsfer[8 * i + 1 + 6] & 0xff));
                        deviceList.add(devicesModel);
                    }
                    cmdResult(new byte[256]);
                    break;
                case Constant.TYPE_DETECTION:
                    Log.e("ecg", "===============检测返回============");
                    for (int i = 0; i < trabsfer[0] / 8; i++) {
                        devicesModel = new DevicesModel();
                        byte[] data = new byte[256];
                        devicesModel.setConnectStr(String.valueOf(trabsfer[8 * i + 1]));
                        StringBuilder builder = new StringBuilder();
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 1] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 2] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 3] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 4] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 5] & 0xff));
                        builder.append(Integer.toHexString(trabsfer[8 * i + 1 + 6] & 0xff));
                        devicesModel.setMacStr(builder.toString());
                        devicesModel.setDeviceType(String.valueOf(trabsfer[8 * i + 1 + 7] & 0xff));
                        data[0] = 7;
                        data[1] = trabsfer[8 * i + 1 + 1];
                        data[2] = trabsfer[8 * i + 1 + 2];
                        data[3] = trabsfer[8 * i + 1 + 3];
                        data[4] = trabsfer[8 * i + 1 + 4];
                        data[5] = trabsfer[8 * i + 1 + 5];
                        data[6] = trabsfer[8 * i + 1 + 6];
                        data[7] = trabsfer[8 * i + 1 + 7];
                        devicesModel.setDevicesData(data);
                        deviceList.add(devicesModel);
                    }
                    break;
                case Constant.TYPE_BREAK:
                    cmdResult(new byte[256]);
                    break;
                case Constant.TYPE_RESTORATION:
                    cmdResult(new byte[256]);
                    break;
            }
            Log.e("ecg", "=================设置结果到页面=============");
            mAdapter.setDeviceList(deviceList);
        }
    }


    @Override
    protected void updateReceivedData(byte[] data) {
        try {
            Algorithm4Library.addRecvData(data, data.length);
            mHandler.sendEmptyMessageAtTime(1, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAdapter = new DevicesListAdapter(this,null);
        mRvDevices.setLayoutManager(manager);
        mRvDevices.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
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

            cmdResult(new byte[256]);

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
                mCurrentType = Constant.TYPE_SEARCH;
                mTransfer = new byte[256];
                cmdResult(mTransfer);
                break;
            case R.id.btn_connect:
                mCurrentType = Constant.TYPE_CONNECT;
                cmdResult(mTransfer);
                break;
            case R.id.btn_detection:
                mCurrentType = Constant.TYPE_DETECTION;
                mTransfer = new byte[256];
                cmdResult(mTransfer);
                break;
            case R.id.btn_break:
                mCurrentType = Constant.TYPE_BREAK;
                cmdResult(mTransfer);
                break;
            case R.id.btn_restoration:
                mCurrentType = Constant.TYPE_RESTORATION;
                mTransfer = new byte[256];
                cmdResult(mTransfer);
                break;
        }
    }

    /**
     * 构建并发送指令
     * @param transfer
     */
    private void cmdResult(byte[] transfer) {
        int cmd4Dev;
        if (transfer == null) {
            return;
        }
        if (mCurrentType == Constant.TYPE_BREAK) {
            byte[] transfers = new byte[256];
            transfers[0] = 1;
            transfers[1] = transfer[7];
            transfers[2] = transfer[1];
            transfers[3] = transfer[2];
            transfers[4] = transfer[3];
            transfers[5] = transfer[4];
            transfers[6] = transfer[5];
            transfers[7] = transfer[6];
            cmd4Dev = Algorithm4Library.GeneralCmd4Dev(mCurrentType, transfers);
        } else {
            cmd4Dev = Algorithm4Library.GeneralCmd4Dev(mCurrentType, transfer);
        }
        textLog.setText("=============发送指令===========" + cmd4Dev);
        byte b = transfer[0];
        if (b > 0) {
            byte[] bytes = new byte[b];
            System.arraycopy(transfer, 1, bytes, 0, b);
            Log.e("ecg", "==============写入指令============");
            textLog.setText(textLog.getText().toString() + "=============发送指令===========" + cmd4Dev);
            writeIoManage(bytes);
        }
    }

    @Override
    public void clickListener(byte[] data) {
        if (data != null) {
            mTransfer = data;
        }
    }
}
