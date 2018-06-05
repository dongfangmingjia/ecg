package com.ljfth.ecgviewlib;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algorithm4.library.algorithm4library.Algorithm4Library;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.ljfth.ecgviewlib.base.BaseFragment;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * 主页Fragment
 */
public class HomeFragment extends BaseFragment implements View.OnTouchListener {


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_home;
    }

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
                    Toast.makeText(getActivity(), "Runner stopped.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNewData(final byte[] data) {
                    getActivity().runOnUiThread(new Runnable() {
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

//        final String message = "Read " + data.length + " bytes: \n"
//                + HexDump.dumpHexString(data) + "\n\n";
//        mDumpTextView.append(message);
//        mDumpTextView.setText(message);
//        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());

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

        //一下都是直接拿到数据显示，老代码，要废弃
//        if (data != null && data.length == 112 && (data[0] & 0xFF) == 0xAA) {
//            //0x01是心电图
//            String message = "";
//            switch (data[1] & 0xFF) {
//                //血氧
//                case 0x00:
//                    for (int i = 2; i < data.length - 3; i += 6) {
//                        int value1 = data[i + 2] & 0xFF | (data[i + 1] & 0xFF) << 8 | (data[i] & 0xFF) << 16;
//                        message += value1 + " ";
//                        if (bcgWaveView1 != null) {
//                            bcgWaveView1.drawWave(value1);
//                        }
//                        //    mUsbLog.setText(message);
//                    }
//                    break;
//                //心电
//                case 0x01:
//                    for (int i = 2; i < data.length - 3; i += 9) {
//                        int value1 = data[i + 2] & 0xFF | (data[i + 1] & 0xFF) << 8 | (data[i] & 0xFF) << 16;
//                        message += value1 + " ";
//                        if (bcgWaveView2 != null) {
//                            bcgWaveView2.drawWave(value1);
//                        }
//                    }
//                    //    mUsbLog.setText(message);
//                    break;
//                //血压
//                case 0x02:
//                    for (int i = 2; i < data.length - 3; i += 4) {
//                        int value1 = data[i + 1] & 0xFF | (data[i] & 0xFF) << 8;
//                        message += value1 + " ";
//                        if (bcgWaveView3 != null) {
//                            bcgWaveView3.drawWave(value1);
//                        }
////                        mUsbLog.setText(message);
//                    }
//                    break;
//                //呼吸
//                case 0x03:
//                    for (int i = 2; i < data.length - 3; i += 3) {
//                        int value1 = data[i + 2] & 0xFF | (data[i + 1] & 0xFF) << 8 | (data[i] & 0xFF) << 16;
//                        message += value1 + " ";
//                        if (bcgWaveView4 != null) {
//                            bcgWaveView4.drawWave(value1);
//                        }
////                        mUsbLog.setText(message);
//                    }
//                    byte[] data1 = new byte[1];
//                    byte[] data2 = new byte[1];
//                    data1[0] = data[2];
//                    data2[0] = data[3];
//                    mTemperature.setText(bcd2Str(data1) + "." + bcd2Str(data2));
//                    break;
//                default:
////                    mUsbLog.setText((data[1] & 0xFF) + "");
//
//            }

//        }
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


    private String getDateSavePath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String name = simpleDateFormat.format(new Date());
        return getActivity().getExternalCacheDir().getAbsolutePath() + name;
    }


    @Override
    protected void initWidget(View root) {
        mTitleTextView = (TextView) root.findViewById(R.id.text_usb);
        mTextViewRate  = (TextView) root.findViewById(R.id.graph_father1_data_text_left);
        mTextViewBPM= (TextView) root.findViewById(R.id.graph_father1_data_text_right);
        mTextViewHR= (TextView) root.findViewById(R.id.graph_father2_data_text);
        mTextViewmmHg= (TextView) root.findViewById(R.id.graph_father3_data_text);
        mTextViewRR= (TextView) root.findViewById(R.id.graph_father4_data_text_left);
        mTemperature = (TextView) root.findViewById(R.id.graph_father4_data_text_right);
        mTextViewPI = (TextView) root.findViewById(R.id.graph_father1_data_text_right1);

        mUsbLog = (TextView) root.findViewById(R.id.text_log);
        linearLayout = (LinearLayout) root.findViewById(R.id.root);
        view = getActivity().getWindow().getDecorView();
        linearLayout.setOnTouchListener(this);




//        btn_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (btn_start.getText().equals("start")) {
//                    flag = true;
//                    generateData();
//                    btn_start.setText("stop");
//                } else {
//                    flag = false;
//                    stopData();
//                    btn_start.setText("start");
//
//                }
//            }
//        });

        //ljfth:
        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        initPort();
        initView(root);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String curItentActionName = intent.getAction();
//                Toast.makeText(context, String.valueOf("Current Intent Action: " + curItentActionName), 1000).show();

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
        getActivity().registerReceiver(mReceiver, filter);

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
                    Toast.makeText(getActivity(), "没有发现USB,或者USB设备超过1个", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

//    public void stopData() {
//         scheduledExecutor.shutdown();;
//    }

//    public void generateData() {
//        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                getEcgData();
//            }
//        }, 1, 20, TimeUnit.MILLISECONDS);
//
//    }

//    int i = 0;
//
//    public void getEcgData() {
//        if (flag) {
//            final int y = (int) (500 * Math.sin(2 * Math.PI * i / 100) + 700);
//            Log.i("tag", y + "");
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    bcgWaveView3.drawWave(y);
//                }
//            });
//            i++;
//        }
//    }

    private void initView(final View root) {
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
                                    leftTitle = (TextView) root.findViewById(R.id.graph_father1_left);
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
                                    leftTitle = (TextView) root.findViewById(R.id.graph_father2_left);
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
                                    leftTitle = (TextView) root.findViewById(R.id.graph_father3_left);
                                    sampleRe = 4000000;
                                    waveColor = Color.WHITE;
                                    id1 = R.id.graph_father3_data;
                                    id2 = R.id.graph_father3_left;
                                    break;
                                //呼吸
                                case 3:
                                    graph_father = (RelativeLayout) view.findViewById(R.id.graph_father4);
                                    leftTitle = (TextView) root.findViewById(R.id.graph_father4_left);
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
                            EcgWaveView ecgWaveView = new EcgWaveView(getActivity(), width, height);
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
    public void onResume() {
//        byte a[][] = new byte[10][112];
//        Algorithm4Library.addData(a, 112, 10);
//        double data[] = {1,2,3,4,5,6,7,8,9,10};
//        Algorithm4Library.getValue(data);
//        for (int i = 0; i < 10; i++)
//        {
//            Log.d("ljfth", "data " + i + "=" + data[i]);
//
//        }

        super.onResume();

//        Toast.makeText(MainActivity.this, "Resumed, port=" + mPort, Toast.LENGTH_SHORT).show();
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
                mPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (IOException e) {
                Toast.makeText(getActivity(), "Error setting up device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
        onDeviceStateChange();

        Log.e("test", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
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
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
        Log.e("test", "onDestroy");
    }
}
