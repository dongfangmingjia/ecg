package com.ljfth.ecgviewlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljfth.ecgviewlib.EcgViewInterface;
import com.ljfth.ecgviewlib.EcgWaveView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.spec.ECGenParameterSpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.ljfth.ecgviewlib.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private Button btn_start;
    private boolean flag = false;
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);

    private UsbManager mUsbManager;
    private UsbSerialPort mPort;
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

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private BroadcastReceiver mReceiver;
    private int dataCount = 0;
    private byte [][] Data_ = new byte[10][112];
    private int dataSaveCount = 0;
    private String dataSavePath;
    private static int SaveCountMax = 1000;

    private FileOutputStream outputStream;
    private BufferedOutputStream bufferedOutputStream;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Toast.makeText(MainActivity.this, "Runner stopped.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNewData(final byte[] data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateReceivedData(data);
                        }
                    });
                }
            };

    private void saveData(byte[] data){
        //存储数据
        dataSaveCount++;
        if (dataSaveCount == 1 || outputStream == null || bufferedOutputStream == null)
        {
            dataSavePath = getDateSavePath();
            try{
                outputStream = new FileOutputStream(dataSavePath);
                bufferedOutputStream = new BufferedOutputStream(outputStream);
                bufferedOutputStream.write(data);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if( bufferedOutputStream != null ){
                    try {
                        bufferedOutputStream.close();
                        bufferedOutputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if ( outputStream != null ) {
                    try {
                        outputStream.close();
                        outputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (dataSaveCount <= SaveCountMax)
        {
            try{
                bufferedOutputStream.write(data);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
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

    private void updateReceivedData(byte[] data) {
        double sampledData[][] = new double[12][250];
        boolean isGetSampledData = false;
        int controlNum = 1;
        if (dataCount < controlNum)
        {
            Data_[dataCount] = data.clone();
        }
        else
        {
            Algorithm4Library.addData(Data_, 112, 10);
            dataCount = 0;
            Data_[dataCount] = data.clone();
            Algorithm4Library.getSampledData(sampledData);
            isGetSampledData = true;
        }

        if(isGetSampledData)
        {
            double data1[] = new double[10];
            Algorithm4Library.getValue(data1);

            mTextViewRate.setText(String.format("%.0f", data1[0]));
            mTextViewBPM.setText(String.format("%.1f", data1[1]));
            mTextViewPI.setText(String.format("%.1f", data1[2]));

            mTextViewHR.setText(String.format("%.1f", data1[3]));

            mTextViewRR.setText(String.format("%.1f", data1[4] < 0 ? 0 : data1[4]));

//            if(data1[5] != -1)
            {
                mTemperature.setText(String.format("%.1f", data1[5] < 0 ? 0 : data1[5]));
            }

            mTextViewmmHg.setText((data1[7] < 0 ? 0 : data1[7]) + "/" + (data1[6] < 0 ? 0 : data1[6]));
        }

        dataCount++;

        if(isGetSampledData)
        {
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

    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
//            Toast.makeText(MainActivity.this, "Stopping io manager ..", Toast.LENGTH_SHORT).show();
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (mPort != null) {
//            Toast.makeText(MainActivity.this, "Starting io manager ..", Toast.LENGTH_SHORT).show();
            mSerialIoManager = new SerialInputOutputManager(mPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }


    private void writeIoManage(byte[] array) {
        String str = String.format("array len %d", array.length);
        textTitle.setText(str);
        Log.i("test" ,"recv len "+str);
        if (mPort != null)
        {
            if (array.length > 0) {
                try {
                    int nRet = mPort.write(array, 100);
                    str = String.format("w succ %d", nRet);
                    textTitle.setText(str);
                    //mTextViewHR.setText("write ok");
                }
                catch(IOException e2){
                    //ignore
                    str = String.format("write error ");
                    textTitle.setText(str+e2.toString());
                    Log.e("test", "Serial testwrite error");
                    //mTextViewHR.setText("write Error");
                }
            }
        }
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            initPort();
        }
        if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            mTitleTextView.setText("No serial device.");
        }
//        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();
    }

    private String getDateSavePath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String name = simpleDateFormat.format(new Date());
        return getExternalCacheDir().getAbsolutePath() + name;
    }

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_main2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitleTextView = (TextView) findViewById(R.id.text_usb);
        mTextViewRate  = (TextView) findViewById(R.id.graph_father1_data_text_left);
        mTextViewBPM= (TextView) findViewById(R.id.graph_father1_data_text_right);
        mTextViewHR= (TextView) findViewById(R.id.graph_father2_data_text);
        mTextViewmmHg= (TextView) findViewById(R.id.graph_father3_data_text);
        mTextViewRR= (TextView) findViewById(R.id.graph_father4_data_text_left);
        mTemperature = (TextView) findViewById(R.id.graph_father4_data_text_right);
        mTextViewPI = (TextView) findViewById(R.id.graph_father1_data_text_right1);

        mUsbLog = (TextView) findViewById(R.id.text_log);
        linearLayout = (LinearLayout) findViewById(R.id.root);
        view = getWindow().getDecorView();
        linearLayout.setOnTouchListener(this);

        //ljfth:
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        initPort();
        initView();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String curItentActionName = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(curItentActionName)) {
                    mTitleTextView.setText("No serial device.");
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

        if (mPort == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            UsbDeviceConnection connection = mUsbManager.openDevice(mPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                mPort.open(connection);
                mPort.setParameters(230400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error setting up device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    mPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                mPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + mPort.getClass().getSimpleName());

            writeIoManage(GeneralSpO2Command(true));
        }
        onDeviceStateChange();

        Log.e("test", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawerLayout.closeDrawer(Gravity.START);
        stopIoManager();
        if (mPort != null) {
            try {
                mPort.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
        Log.e("test", "onPause");
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
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
        Log.e("test", "onDestroy");
    }

    //*************************一些命令*************************

    // 打开或关闭血氧
    private  byte[] GeneralSpO2Command(boolean isOpen) {

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
    private  byte[] GeneralECGCommand(boolean isOpen) {

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
    private  byte[] GeneralTempCommand(boolean isOpen) {

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
    private  byte[] GeneralNIBPCommand(boolean isOpen) {

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
            } else if (TextUtils.equals(action, PatientInfoActivity.ACTION_CLEAR)) {
                textTitle.setText("");
            }
        }
    }
}
