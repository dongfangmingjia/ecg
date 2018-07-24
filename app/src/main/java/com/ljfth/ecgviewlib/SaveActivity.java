package com.ljfth.ecgviewlib;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ljfth.ecgviewlib.adapter.SaveListAdapter;
import com.ljfth.ecgviewlib.base.BaseActivity;
import com.ljfth.ecgviewlib.model.FileModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class SaveActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.et_start)
    EditText mEtStart;
    @BindView(R.id.et_end)
    EditText mEtEnd;
    private SaveListAdapter mAdapter;
    private List<FileModel> mFileList = new ArrayList<>();

    private List<String> mPathList = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mAdapter.setData(mFileList);
                    break;
            }
        }
    };

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_save;
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        mTitle.setText("存储");
        initRecyclerView();
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new SaveListAdapter(mFileList);
        mRvList.setLayoutManager(layoutManager);
        mRvList.setAdapter(mAdapter);

        getFileList();
    }

    @OnClick({R.id.btn_back, R.id.btn_search, R.id.btn_delete, R.id.btn_play, R.id.btn_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                break;
            case R.id.btn_delete:
                break;
            case R.id.btn_play:
                break;
            case R.id.btn_clear:
                break;
        }
    }


    /**
     * 扫描文件路径获取保存文件
     */
    private void getFileList() {
        final String filePath = getExternalCacheDir().getAbsolutePath();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("warner", "==============filePath==============" + filePath);
                try{
                    File file = new File(filePath);
                    File[] files = file.listFiles();
                    if (files != null) {
                        int count = files.length;
                        FileModel fileModel;
                        for (int i = 0; i < count; i++) {
                            File file1 = files[i];
                            fileModel = new FileModel();
                            if (!TextUtils.isEmpty(file1.getName())) {
                                Date date = new Date(Long.valueOf(file1.getName()));
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                        Locale.CHINA);
                                fileModel.setName(dateFormat.format(date));
                            }
                            fileModel.setPath(file1.getPath());
                            fileModel.setSize(FormetFileSize(file1.length()));
                            mFileList.add(fileModel);
                        }
                    }
                    mHandler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }



    /**
     * 转换文件大小
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize="0B";

        if(fileS==0){
            return wrongSize;
        }

        if (fileS < 1024){
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576){
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824){
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else{
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }

        return fileSizeString;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HashMap<String, String> params) {
        if (params != null) {
            String action = params.get("action");
            if (TextUtils.equals(action, SaveListAdapter.ACTION_ITEM_CLICK)) {
                String path = params.get("path");
                mPathList.add(path);
            }
        }
    }
}
