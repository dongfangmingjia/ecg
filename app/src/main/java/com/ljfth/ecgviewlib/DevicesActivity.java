package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ljfth.ecgviewlib.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DevicesActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.rv_devices)
    RecyclerView mRvDevices;

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

    @OnClick({R.id.btn_back, R.id.btn_search, R.id.btn_connect, R.id.btn_detection, R.id.btn_break, R.id.btn_restoration})
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
