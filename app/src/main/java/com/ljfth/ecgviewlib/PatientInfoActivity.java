package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ljfth.ecgviewlib.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.security.spec.ECField;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientInfoActivity extends BaseActivity {

    public static final String ACTION_SAVE = "action_save";
    public static final String ACTION_CLEAR = "action_clear";

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

        setPatientInfo();
    }

    /**
     * 设置病人信息
     */
    private void setPatientInfo() {
        String name = EcgSharedPrefrence.getName(this);
        String age = EcgSharedPrefrence.getAge(this);
        int sex = EcgSharedPrefrence.getSex(this);
        String hospitalNum = EcgSharedPrefrence.getHospitalNum(this);
        String bedNum = EcgSharedPrefrence.getBedNum(this);
        boolean paceMaking = EcgSharedPrefrence.getPaceMaking(this);
        if (TextUtils.isEmpty(name)) {
            mEtName.setText(name);
        }
        if (TextUtils.isEmpty(age)) {
            mEtAge.setText(age);
        }
        if (sex == Constant.SEX_MEN) {
            mRbMen.setChecked(true);
        } else {
            mRbWomen.setChecked(true);
        }
        if (TextUtils.isEmpty(hospitalNum)) {
            mEtNumber.setText(hospitalNum);
        }
        if (TextUtils.isEmpty(bedNum)) {
            mEtBedNumber.setText(bedNum);
        }
        if (paceMaking) {
            mRbYes.setChecked(true);
        } else {
            mRbNo.setChecked(true);
        }
    }

    @OnClick({R.id.btn_back, R.id.tv_in_hospital, R.id.tv_out_hospital})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_in_hospital:
                saveData();
                finish();
                break;
            case R.id.tv_out_hospital:
                clearData();
                finish();
                break;
        }
    }

    /**
     * 清除本地数据
     */
    private void clearData() {
        EcgSharedPrefrence.setName(PatientInfoActivity.this, null);
        EcgSharedPrefrence.setAge(PatientInfoActivity.this, null);
        EcgSharedPrefrence.setSex(PatientInfoActivity.this, Constant.SEX_MEN);
        EcgSharedPrefrence.setHospitalNum(PatientInfoActivity.this, null);
        EcgSharedPrefrence.setBedNum(PatientInfoActivity.this, null);
        EcgSharedPrefrence.setPaceMaking(PatientInfoActivity.this, false);

        HashMap<String, String> params = new HashMap<>();
        params.put("action", ACTION_CLEAR);
        EventBus.getDefault().post(params);
    }

    /**
     * 保存填写的数据到本地
     */
    private void saveData() {
        String name = mEtName.getText().toString().trim();
        String age = mEtAge.getText().toString().trim();
        String hospitalNum = mEtNumber.getText().toString().trim();
        String bedNum = mEtBedNumber.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(hospitalNum) || TextUtils.isEmpty(bedNum)) {
            Toast.makeText(PatientInfoActivity.this, "请填写完整信息！", Toast.LENGTH_SHORT).show();
            return;
        }
        EcgSharedPrefrence.setName(PatientInfoActivity.this, name);
        EcgSharedPrefrence.setAge(PatientInfoActivity.this, age);
        EcgSharedPrefrence.setSex(PatientInfoActivity.this, mSex);
        EcgSharedPrefrence.setHospitalNum(PatientInfoActivity.this, hospitalNum);
        EcgSharedPrefrence.setBedNum(PatientInfoActivity.this, bedNum);
        EcgSharedPrefrence.setPaceMaking(PatientInfoActivity.this, mPace == Constant.PACE_YES);

        HashMap<String, String> params = new HashMap<>();
        params.put("action", ACTION_SAVE);
        EventBus.getDefault().post(params);
    }

    class SexCheckLister implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_men) {
                mSex = Constant.SEX_MEN;
            } else if (checkedId == R.id.rb_women) {
                mSex = Constant.SEX_WOMEN;
            } else {
                mSex = Constant.SEX_MEN;
            }
        }
    }

    class PaceMakingCheckLister implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_yes) {
                mPace = Constant.PACE_YES;
            } else if (checkedId == R.id.rb_no) {
                mPace = Constant.PACE_NO;
            } else {
                mPace = Constant.PACE_YES;
            }
        }
    }
}
