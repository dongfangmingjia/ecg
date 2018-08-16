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
import android.widget.Toast;

import com.ljfth.ecgviewlib.adapter.SaveListAdapter;
import com.ljfth.ecgviewlib.base.BaseActivity;
import com.ljfth.ecgviewlib.model.FileModel;
import com.ljfth.ecgviewlib.utils.FileUtils;
import com.ljfth.ecgviewlib.utils.StringUtils;

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
        mPathList.clear();
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
                searchFile();
                break;
            case R.id.btn_delete:
                if (mPathList != null && mPathList.size() > 0) {
                    for (String path : mPathList) {
                        FileUtils.deleteFile(new File(path));
                    }
                    Toast.makeText(SaveActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                    mAdapter.setData(null);
                    mAdapter.clearPathList();
                    mFileList.clear();
                    getFileList();
                } else {
                    Toast.makeText(SaveActivity.this, "请选择要删除的文件！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_play:
                break;
            case R.id.btn_clear:
                FileUtils.deleteAllFiles(new File(getExternalCacheDir().getAbsolutePath()));
                mAdapter.setData(null);
                mAdapter.clearPathList();
                mFileList.clear();
                break;
        }
    }

    /**
     * 搜索文件
     */
    private void searchFile() {
        mAdapter.setData(null);
        mAdapter.clearPathList();
        mFileList.clear();
        String start = mEtStart.getText().toString().trim();
        String end = mEtEnd.getText().toString().trim();
        final long startDate = StringUtils.getStringToDate(start, "yyyy-MM-dd HH:mm:ss");
        final long endDate = StringUtils.getStringToDate(end, "yyyy-MM-dd HH:mm:ss");
        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end) || endDate < startDate) {
            return;
        }

        final String filePath = Constant.SAVE_PATH;
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
                                String time = file1.getName();
                                if (!(Long.valueOf(time) >= startDate && Long.valueOf(time) <= endDate)) {
                                    continue;
                                }
                                Date date = new Date(Long.valueOf(file1.getName()));
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                        Locale.CHINA);
                                fileModel.setName(dateFormat.format(date));
                            }
                            fileModel.setPath(file1.getPath());
                            fileModel.setSize(FileUtils.FormetFileSize(file1.length()));
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
                            fileModel.setSize(FileUtils.FormetFileSize(file1.length()));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HashMap<String, Object> params) {
        if (params != null) {
            String action = (String) params.get("action");
            if (TextUtils.equals(action, SaveListAdapter.ACTION_ITEM_CLICK)) {
                mPathList = (List<String>) params.get("list");
            }
        }
    }
}
