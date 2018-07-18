package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ljfth.ecgviewlib.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

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
                setData();
                break;
        }
    }

    /**
     * 设置数据
     */
    private void setData() {
        // 参数设置为默认
        EcgSharedPrefrence.setSpo2Upper(this, null);
        EcgSharedPrefrence.setSpo2Floor(this, null);
        EcgSharedPrefrence.setEcgUpper(this, null);
        EcgSharedPrefrence.setEcgFloor(this, null);
        EcgSharedPrefrence.setRespUpper(this, null);
        EcgSharedPrefrence.setRespFloor(this, null);
        EcgSharedPrefrence.setTempUpper(this, null);
        EcgSharedPrefrence.setTempFloor(this, null);
        EcgSharedPrefrence.setSbpUpper(this, null);
        EcgSharedPrefrence.setSbpFloor(this, null);
        EcgSharedPrefrence.setDbpUpper(this,null);
        EcgSharedPrefrence.setDbpFloor(this, null);
        EcgSharedPrefrence.setMapUpper(this, null);
        EcgSharedPrefrence.setMapFloor(this, null);
        EcgSharedPrefrence.setRing(this, false);
        // 清空病人信息
        EcgSharedPrefrence.setName(this, null);
        EcgSharedPrefrence.setAge(this, null);
        EcgSharedPrefrence.setSex(this, Constant.SEX_MEN);
        EcgSharedPrefrence.setHospitalNum(this, null);
        EcgSharedPrefrence.setBedNum(this, null);
        EcgSharedPrefrence.setPaceMaking(this, false);


        HashMap<String, String> params = new HashMap<>();
        params.put("action", PatientInfoActivity.ACTION_CLEAR);
        EventBus.getDefault().post(params);
        finish();
    }
}
