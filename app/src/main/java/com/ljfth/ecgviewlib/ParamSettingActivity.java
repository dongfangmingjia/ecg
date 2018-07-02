package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ljfth.ecgviewlib.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParamSettingActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.switch_view)
    SwitchCompat mSwitchView;
    @BindView(R.id.et_sp_top)
    EditText mEtSpTop;
    @BindView(R.id.et_sp_bottom)
    EditText mEtSpBottom;
    @BindView(R.id.rb_sp_12)
    RadioButton mRbSp12;
    @BindView(R.id.rb_sp_25)
    RadioButton mRbSp25;
    @BindView(R.id.rb_sp_50)
    RadioButton mRbSp50;
    @BindView(R.id.et_ecg_top)
    EditText mEtEcgTop;
    @BindView(R.id.et_ecg_bottom)
    EditText mEtEcgBottom;
    @BindView(R.id.rb_ecg_12)
    RadioButton mRbEcg12;
    @BindView(R.id.rb_ecg_25)
    RadioButton mRbEcg25;
    @BindView(R.id.rb_ecg_50)
    RadioButton mRbEcg50;
    @BindView(R.id.et_resp_top)
    EditText mEtRespTop;
    @BindView(R.id.et_resp_bottom)
    EditText mEtRespBottom;
    @BindView(R.id.rb_resp_12)
    RadioButton mRbResp12;
    @BindView(R.id.rb_resp_25)
    RadioButton mRbResp25;
    @BindView(R.id.rb_resp_50)
    RadioButton mRbResp50;
    @BindView(R.id.et_temp_top)
    EditText mEtTempTop;
    @BindView(R.id.et_temp_bottom)
    EditText mEtTempBottom;
    @BindView(R.id.rb_cnibp_12)
    RadioButton mRbCnibp12;
    @BindView(R.id.rb_cnibp_25)
    RadioButton mRbCnibp25;
    @BindView(R.id.rb_cnibp_50)
    RadioButton mRbCnibp50;
    @BindView(R.id.et_sbp_top)
    EditText mEtSbpTop;
    @BindView(R.id.et_sbp_bottom)
    EditText mEtSbpBottom;
    @BindView(R.id.et_dbp_top)
    EditText mEtDbpTop;
    @BindView(R.id.et_dbp_bottom)
    EditText mEtDbpBottom;
    @BindView(R.id.et_map_top)
    EditText mEtMapTop;
    @BindView(R.id.et_map_bottom)
    EditText mEtMapBottom;
    @BindView(R.id.sb_volume)
    SeekBar mSbVolume;
    @BindView(R.id.rg_sp)
    RadioGroup mRgSp;
    @BindView(R.id.rg_ecg)
    RadioGroup mRgEcg;
    @BindView(R.id.rg_resp)
    RadioGroup mRgResp;
    @BindView(R.id.rg_cnibp)
    RadioGroup mRgCnibp;

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_param_setting;
    }

    @OnClick({R.id.btn_back, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                break;
        }
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mTitle.setText("参数设置");
    }
}
