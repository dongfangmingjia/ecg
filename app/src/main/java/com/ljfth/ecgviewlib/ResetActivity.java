package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ljfth.ecgviewlib.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_reset;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTitle.setText("重置");
    }

    @OnClick({R.id.btn_back, R.id.btn_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_reset:

                break;
        }
    }
}
