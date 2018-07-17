package com.ljfth.ecgviewlib;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ljfth.ecgviewlib.adapter.SaveListAdapter;
import com.ljfth.ecgviewlib.base.BaseActivity;
import com.ljfth.ecgviewlib.model.FileModel;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_save;
    }

    @Override
    protected void initData() {
        super.initData();
        mTitle.setText("存储");
        initRecyclerView();
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new SaveListAdapter(null);
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
        List<FileModel> list = new ArrayList<>();
        String filePath = getExternalCacheDir().getAbsolutePath();
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
                    fileModel.setName(file1.getName());
                    fileModel.setName(file1.getPath());
                    fileModel.setSize(FormetFileSize(file1.length()));
                    list.add(fileModel);
                }
            }
            mAdapter.setData(list);
        } catch (Exception e) {

        }
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
}
