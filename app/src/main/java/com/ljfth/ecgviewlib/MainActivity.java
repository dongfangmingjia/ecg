package com.ljfth.ecgviewlib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.ljfth.ecgviewlib.base.BaseActivity;
import com.ljfth.ecgviewlib.base.UsbService;
import com.ljfth.ecgviewlib.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements View.OnTouchListener {
    private RelativeLayout graph_father;
    private LinearLayout linearLayout;
    private EcgWaveView bcgWaveView1;
    private EcgWaveView bcgWaveView2;
    private EcgWaveView bcgWaveView3;
    private EcgWaveView bcgWaveView4;
    private View view;

    private TextView mTitleTextView;
    private TextView mUsbLog;
    private TextView mTemperature;

    private TextView mTextViewHR;
    private TextView mTextViewRate;
    private TextView mTextViewBPM;
    private TextView mTextViewmmHg;
    private TextView mTextViewRR;
    private TextView mTextViewPI;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.text_title)
    TextView textTitle;

    private int dataCount = 0;
    private byte[][] Data_ = new byte[10][112];
    private int dataSaveCount = 0;
    private String dataSavePath;
    private static int SaveCountMax = 1000;

    private FileOutputStream outputStream;
    private BufferedOutputStream bufferedOutputStream;
    private Intent mServiceIntent;

    private UsbConnection mConnection = new UsbConnection();
    private UsbService.UsbBinder mBinder;

    private void saveData(byte[] data) {
        //存储数据
        dataSaveCount++;
        if (dataSaveCount == 1 || outputStream == null || bufferedOutputStream == null) {
            dataSavePath = getDateSavePath();
            try {
                outputStream = new FileOutputStream(dataSavePath);
                bufferedOutputStream = new BufferedOutputStream(outputStream);
                bufferedOutputStream.write(data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                        bufferedOutputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                        outputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (dataSaveCount <= SaveCountMax) {
            try {
                bufferedOutputStream.write(data);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (dataSaveCount == SaveCountMax) {
                    dataSaveCount = 0;
                    if (bufferedOutputStream != null) {
                        try {
                            bufferedOutputStream.close();
                            bufferedOutputStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (outputStream != null) {
                        try {
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void updateReceivedData(byte[] data) {
        String str = String.format("dLen %d", data.length);
        textTitle.setText(str);
        double sampledData[][] = new double[12][256];
        double sampleDataLen[] = new double[12];
        boolean isGetSampledData = false;

        try {
            Algorithm4Library.addRecvData(data, data.length);
            Log.e("ecg","===========传入数据长度===========" + data.length);
            Log.e("ecg","===========获得命令结果===========" + data.length);
            byte[] trabsfer = new byte[256];
            boolean bool =  Algorithm4Library.GetCmdResult(0, trabsfer);
            Log.e("ecg", "==============MainActivr返回指令===========" + Integer.toHexString(trabsfer[0]) + " index1 : " + Integer.toHexString(trabsfer[1] & 0xFF));

            Algorithm4Library.getSampledData(sampledData, sampleDataLen);
            //Algorithm4Library.getSampledData(sampledLen);
            isGetSampledData = true;
        } catch (Exception e) {
            textTitle.setText("Alg error0 " + e.toString());
        }

        if (isGetSampledData) {
            double data1[] = new double[10];
            try {
                Algorithm4Library.getValue(data1);
            } catch (Exception e) {
                textTitle.setText("Alg error1" + e.toString());
            }

            mTextViewRate.setText(String.format("%.0f", data1[0]));
            if (data1[0] <= StringUtils.string2Int(EcgSharedPrefrence.getSpo2Upper(MainActivity.this)) &&
                    data1[0] >= StringUtils.string2Int(EcgSharedPrefrence.getSpo2Floor(MainActivity.this))) {
                mTextViewRate.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.blue));
            } else {
                mTextViewRate.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
            }
            mTextViewBPM.setText(String.format("%.1f", data1[1]));
            mTextViewPI.setText(String.format("%.1f", data1[2]));

            mTextViewHR.setText(String.format("%.1f", data1[3]));
            if (data1[3] <= StringUtils.string2Int(EcgSharedPrefrence.getEcgUpper(MainActivity.this)) &&
                    data1[3] >= StringUtils.string2Int(EcgSharedPrefrence.getEcgFloor(MainActivity.this))) {
                mTextViewHR.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green));
            } else {
                mTextViewHR.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
            }

            mTextViewRR.setText(String.format("%.1f", data1[4] < 0 ? 0 : data1[4]));
            if (data1[4] <= StringUtils.string2Int(EcgSharedPrefrence.getRespUpper(MainActivity.this))
                    && data1[4] >= StringUtils.string2Int(EcgSharedPrefrence.getRespFloor(MainActivity.this))) {
                mTextViewRR.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.resp_normal_color));
            } else {
                mTextViewRR.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
            }

            mTemperature.setText(String.format("%.1f", data1[5] < 0 ? 0 : data1[5]));
            if (data1[5] <= StringUtils.string2Int(EcgSharedPrefrence.getTempUpper(MainActivity.this))
                    && data1[5] >= StringUtils.string2Int(EcgSharedPrefrence.getTempFloor(MainActivity.this))) {
                mTemperature.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.resp_normal_color));
            } else {
                mTemperature.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
            }

            mTextViewmmHg.setText((data1[7] < 0 ? 0 : data1[7]) + "/" + (data1[6] < 0 ? 0 : data1[6]));
            String s = mTextViewmmHg.getText().toString().trim();
            if (!TextUtils.isEmpty(s)) {
                String sbpUpper = EcgSharedPrefrence.getSbpUpper(MainActivity.this);
                String sbpFloor = EcgSharedPrefrence.getSbpFloor(MainActivity.this);
                String dbpUpper = EcgSharedPrefrence.getDbpUpper(MainActivity.this);
                String dbpFloor = EcgSharedPrefrence.getDbpFloor(MainActivity.this);
                ForegroundColorSpan errorColor = new ForegroundColorSpan(Color.RED);
                ForegroundColorSpan normalColor = new ForegroundColorSpan(Color.WHITE);
                int index = s.indexOf("/");
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(s);
                if ((data1[7] > StringUtils.string2Int(sbpUpper) || data1[7] < StringUtils.string2Int(sbpFloor))
                        && (data1[6] < StringUtils.string2Int(dbpUpper) && data1[6] > StringUtils.string2Int(dbpFloor))) {
                    builder.setSpan(errorColor, 0, index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(normalColor, index, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else if ((data1[7] < StringUtils.string2Int(sbpUpper) && data1[7] > StringUtils.string2Int(sbpFloor))
                        && (data1[6] > StringUtils.string2Int(dbpUpper) || data1[6] < StringUtils.string2Int(dbpFloor))) {
                    builder.setSpan(normalColor, 0, index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(errorColor, index, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else if ((data1[7] < StringUtils.string2Int(sbpUpper) && data1[7] > StringUtils.string2Int(sbpFloor))
                        && (data1[6] < StringUtils.string2Int(dbpUpper) && data1[6] > StringUtils.string2Int(dbpFloor))) {
                    builder.setSpan(normalColor, 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else {
                    builder.setSpan(errorColor, 0, s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
                mTextViewmmHg.setText(builder);
            }

        }

        dataCount++;

        if (isGetSampledData) {
            //血氧
            for (int i = 1; i <= sampledData[1][0]; i++) {
                bcgWaveView1.drawWave((int) sampledData[1][i]);
            }
            //心电
            for (int i = 1; i <= sampledData[3][0]; i++) {
                bcgWaveView2.drawWave((int) sampledData[3][i]);
            }
            //呼吸
            for (int i = 1; i <= sampledData[9][0]; i++) {
                bcgWaveView4.drawWave((int) sampledData[9][i]);
            }
        }

        saveData(data);
    }

    private String getDateSavePath() {
        return getExternalCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis();
    }

    @Override
    protected void NoDeviceDetached() {
        mTitleTextView.setText("No serial device.");
    }

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleTextView = (TextView) findViewById(R.id.text_usb);
        mTextViewRate = (TextView) findViewById(R.id.graph_father1_data_text_left);
        mTextViewBPM = (TextView) findViewById(R.id.graph_father1_data_text_right);
        mTextViewHR = (TextView) findViewById(R.id.graph_father2_data_text);
        mTextViewmmHg = (TextView) findViewById(R.id.graph_father3_data_text);
        mTextViewRR = (TextView) findViewById(R.id.graph_father4_data_text_left);
        mTemperature = (TextView) findViewById(R.id.graph_father4_data_text_right);
        mTextViewPI = (TextView) findViewById(R.id.graph_father1_data_text_right1);

        mUsbLog = (TextView) findViewById(R.id.text_log);
        linearLayout = (LinearLayout) findViewById(R.id.root);
        view = getWindow().getDecorView();
        linearLayout.setOnTouchListener(this);

        //ljfth:
        initView();
    }


    @Override
    protected void initData() {
        super.initData();
        mServiceIntent = new Intent(MainActivity.this, UsbService.class);
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        for (int i = 0; i < 4; i++) {
                            int sampleRe = 20000;
                            double change_nV = 1D;
                            double baseline_voltage = 1D;
                            int waveColor = Color.RED;
                            int id1 = 0;
                            int id2 = 0;
                            TextView leftTitle = mTitleTextView;
                            switch (i) {
                                //血氧
                                case 0:
                                    graph_father = (RelativeLayout) view.findViewById(R.id.graph_father1);
                                    leftTitle = (TextView) findViewById(R.id.graph_father1_left);
                                    sampleRe = 4500000;
                                    change_nV = 0.125;
                                    baseline_voltage = 0.585;
                                    waveColor = Color.argb(255, 43, 172, 246);
                                    id1 = R.id.graph_father1_data;
                                    id2 = R.id.graph_father1_left;
                                    break;
                                //心电
                                case 1:
                                    graph_father = (RelativeLayout) view.findViewById(R.id.graph_father2);
                                    leftTitle = (TextView) findViewById(R.id.graph_father2_left);
                                    sampleRe = 20000;
                                    change_nV = 2;
                                    baseline_voltage = -2.2;
                                    waveColor = Color.GREEN;
                                    id1 = R.id.graph_father2_data;
                                    id2 = R.id.graph_father2_left;
                                    break;
                                //血压
                                case 2:
                                    graph_father = (RelativeLayout) view.findViewById(R.id.graph_father3);
                                    leftTitle = (TextView) findViewById(R.id.graph_father3_left);
                                    sampleRe = 4000000;
                                    waveColor = Color.WHITE;
                                    id1 = R.id.graph_father3_data;
                                    id2 = R.id.graph_father3_left;
                                    break;
                                //呼吸
                                case 3:
                                    graph_father = (RelativeLayout) view.findViewById(R.id.graph_father4);
                                    leftTitle = (TextView) findViewById(R.id.graph_father4_left);
                                    sampleRe = 4000000;
                                    change_nV = 4;
                                    baseline_voltage = -24.64;
                                    waveColor = Color.argb(255, 255, 228, 71);
                                    id1 = R.id.graph_father4_data;
                                    id2 = R.id.graph_father4_left;
                                    break;
                            }
                            int width = graph_father.getWidth() - view.findViewById(id1).getWidth();
                            int height = graph_father.getHeight();
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, height);
                            layoutParams.addRule(RelativeLayout.LEFT_OF, id1);
                            EcgWaveView ecgWaveView = new EcgWaveView(getBaseContext(), width, height);
                            ecgWaveView.setTag(leftTitle.getText().toString());
                            ecgWaveView.setN_frequency(100);
                            ecgWaveView.setSampleRe(sampleRe);
                            ecgWaveView.initGridVoltage(change_nV);
                            ecgWaveView.initBaseLineVoltage(baseline_voltage);
                            ecgWaveView.setSampleV(10);
                            ecgWaveView.setWaveAdapter(true);
                            ecgWaveView.setWaveColor(waveColor);
                            ecgWaveView.setGridColor(Color.argb(255, 26, 40, 59));
                            ecgWaveView.setGridFullFill(true);
                            ecgWaveView.init(ecgViewListener);

                            graph_father.addView(ecgWaveView, layoutParams);
                            leftTitle.bringToFront();
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                            switch (i) {
                                case 0:
                                    bcgWaveView1 = ecgWaveView;
                                    break;
                                case 1:
                                    bcgWaveView2 = ecgWaveView;
                                    break;
                                case 2:
                                    bcgWaveView3 = ecgWaveView;
                                    break;
                                case 3:
                                    bcgWaveView4 = ecgWaveView;
                                    break;
                            }
                        }

                    }
                });
    }

    private EcgViewInterface ecgViewListener = new EcgViewInterface() {
        @Override
        public void onError(EcgWaveView view, Exception e) {

        }

        @Override
        public void onShowMessage(EcgWaveView view, String t, int i) {
            Log.i("tag", "心电接口回调--》" + t);
            if (i == 0) {
                //  Toast.makeText(getApplication(),"时间：" + t + "ms/格",Toast.LENGTH_SHORT).show();
            } else if (i == 1) {
                mUsbLog.setText(view.getTag() + " : change_nV = " + t);
                //   Toast.makeText(getApplication(),"电压："+t+"mv/格",Toast.LENGTH_SHORT).show();

            } else if (i == 2) {
                mUsbLog.setText(view.getTag() + " : baseline_voltage = " + t);
            }
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("tag", "linear监听");
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startIoManager();
//
//        if (mPort == null) {
//            mTitleTextView.setText("No serial device.");
//        } else {
//            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
//            if (connection == null) {
//                mTitleTextView.setText("Opening device failed");
//                return;
//            }
//
//            try {
//                mPort.open(connection);
//                mPort.setParameters(230400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//            } catch (IOException e) {
//                Toast.makeText(MainActivity.this, "Error setting up device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                mTitleTextView.setText("Error opening device: " + e.getMessage());
//                try {
//                    mPort.close();
//                } catch (IOException e2) {
//                    // Ignore.
//                }
//                mPort = null;
//                return;
//            }
//            mTitleTextView.setText("Serial device: " + mPort.getClass().getSimpleName());
//
//            // 血氧
////            writeIoManage(GeneralSpO2Command(true));
//            // 心电、呼吸
////            writeIoManage(GeneralECGCommand(true));
//            // 血压
////            writeIoManage(GeneralNIBPCommand(true));
//        }
//        onDeviceStateChange();
//
//        Log.e("test", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mDrawerLayout.closeDrawer(Gravity.START);
//        stopIoManager();
//        if (mPort != null) {
//            try {
//                mPort.close();
//            } catch (IOException e) {
//                // Ignore.
//            }
//        }
//        Log.e("test", "onPause");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("test", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("test", "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("test", "onStop");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
//        stopService(mServiceIntent);
        EventBus.getDefault().unregister(this);
        Log.e("test", "onDestroy");
    }

    //*************************一些命令*************************

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

    // 打开或关闭心电和呼吸
    private byte[] GeneralECGCommand(boolean isOpen) {

        byte[] array = new byte[10];
        //byte array[] = {0xAA, 0xAA, 0x00, 0x01, 0x42, 0x00, 0x00, 0x00, 0x55, 0x55};

        array[0] = (byte) (0xAA);
        array[1] = (byte) (0xAA);
        array[2] = (byte) (0x00);
        array[3] = (byte) (0x01);
        if (isOpen) {
            array[4] = (byte) (0x42);
        } else {
            array[4] = (byte) (0x43);
        }
        array[5] = (byte) (0x01);
        array[6] = (byte) (0x00);
        array[7] = (byte) (0x00);
        array[8] = (byte) (0x55);
        array[9] = (byte) (0x55);

        return array;
    }

    // 打开或关闭温度
    private byte[] GeneralTempCommand(boolean isOpen) {

        byte[] array = new byte[10];
        //byte array[] = {0xAA, 0xAA, 0x00, 0x01, 0x42, 0x00, 0x00, 0x00, 0x55, 0x55};

        array[0] = (byte) (0xAA);
        array[1] = (byte) (0xAA);
        array[2] = (byte) (0x00);
        array[3] = (byte) (0x01);
        if (isOpen) {
            array[4] = (byte) (0x42);
        } else {
            array[4] = (byte) (0x43);
        }
        array[5] = (byte) (0x04);
        array[6] = (byte) (0x00);
        array[7] = (byte) (0x00);
        array[8] = (byte) (0x55);
        array[9] = (byte) (0x55);

        return array;
    }

    // 打开或关闭血压
    private byte[] GeneralNIBPCommand(boolean isOpen) {

        byte[] array = new byte[10];
        //byte array[] = {0xAA, 0xAA, 0x00, 0x01, 0x42, 0x00, 0x00, 0x00, 0x55, 0x55};

        array[0] = (byte) (0xAA);
        array[1] = (byte) (0xAA);
        array[2] = (byte) (0x00);
        array[3] = (byte) (0x01);
        if (isOpen) {
            array[4] = (byte) (0x42);
        } else {
            array[4] = (byte) (0x43);
        }
        array[5] = (byte) (0x05);
        array[6] = (byte) (0x00);
        array[7] = (byte) (0x00);
        array[8] = (byte) (0x55);
        array[9] = (byte) (0x55);

        return array;
    }

    //*************************一些命令*************************


    @OnClick({R.id.stv_patient_info, R.id.stv_device_attachment, R.id.stv_param_setting,
            R.id.stv_save, R.id.stv_reset, R.id.stv_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_patient_info:
                startActivity(new Intent(MainActivity.this, PatientInfoActivity.class));
                break;
            case R.id.stv_device_attachment:
                startActivity(new Intent(MainActivity.this, DevicesActivity.class));
                break;
            case R.id.stv_param_setting:
                startActivity(new Intent(MainActivity.this, ParamSettingActivity.class));
                break;
            case R.id.stv_save:
                startActivity(new Intent(MainActivity.this, SaveActivity.class));
                break;
            case R.id.stv_reset:
                startActivity(new Intent(MainActivity.this, ResetActivity.class));
                break;
            case R.id.stv_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HashMap<String, String> params) {
        if (params != null) {
            String action = params.get("action");
            if (TextUtils.equals(action, PatientInfoActivity.ACTION_SAVE)) {
                String name = EcgSharedPrefrence.getName(MainActivity.this);
                String bedNum = EcgSharedPrefrence.getBedNum(MainActivity.this);
                textTitle.setText(name + "  -  " + bedNum + "床");
                Constant.SAVE_PATH = getExternalCacheDir().getAbsolutePath() + "/" + name;
            } else if (TextUtils.equals(action, PatientInfoActivity.ACTION_CLEAR)) {
                textTitle.setText("");
            } else if (TextUtils.equals(action, ParamSettingActivity.ACTION_PARAM_SETTING)) {
                // 参数设置完成

            }
        }
    }



    private class UsbConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("warner", "===========onServiceConnected===========");
            mBinder = (UsbService.UsbBinder) service;
            mBinder.writeManage(GeneralSpO2Command(true));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    }

}
