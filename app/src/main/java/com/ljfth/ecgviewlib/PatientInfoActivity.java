package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ljfth.ecgviewlib.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientInfoActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_age)
    EditText mEtAge;
    @BindView(R.id.et_number)
    EditText mEtNumber;
    @BindView(R.id.et_bed_number)
    EditText mEtBedNumber;
    @BindView(R.id.rb_yes)
    RadioButton mRbYes;
    @BindView(R.id.rb_no)
    RadioButton mRbNo;
    @BindView(R.id.rg_pace_making)
    RadioGroup mRgPaceMaking;
    @BindView(R.id.rg_sex)
    RadioGroup mRgSex;
    @BindView(R.id.rb_men)
    RadioButton mRbMen;
    @BindView(R.id.rb_women)
    RadioButton mRbWomen;

    /**
     * 性别常量（男）
     */
    public static final int SEX_MEN = 0x001;
    /**
     * 性别常量（女）
     */
    public static final int SEX_WOMEN = 0x002;
    /**
     * 起搏常量（是）
     */
    public static final int PACE_YES = 0x003;
    /**
     * 起搏常量（否）
     */
    public static final int PACE_NO = 0x004;



    private int mSex;
    private int mPace;

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_patient_info;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mTitle.setText("病人信息");
        mRgSex.setOnCheckedChangeListener(new SexCheckLister());
        mRgPaceMaking.setOnCheckedChangeListener(new PaceMakingCheckLister());
    }

    @OnClick({R.id.btn_back, R.id.tv_in_hospital, R.id.tv_out_hospital})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_in_hospital:
                break;
            case R.id.tv_out_hospital:
                break;
        }
    }

    class SexCheckLister implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_men) {
                mSex = SEX_MEN;
            } else if (checkedId == R.id.rb_women) {
                mSex = SEX_WOMEN;
            } else {
                mSex = SEX_MEN;
            }
        }
    }

    class PaceMakingCheckLister implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_yes) {
                mPace = PACE_YES;
            } else if (checkedId == R.id.rb_no) {
                mPace = PACE_NO;
            } else {
                mPace = PACE_YES;
            }
        }
    }
}
