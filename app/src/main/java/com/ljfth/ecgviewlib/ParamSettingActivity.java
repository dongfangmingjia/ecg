package com.ljfth.ecgviewlib;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ljfth.ecgviewlib.base.BaseActivity;
import com.ljfth.ecgviewlib.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;

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
    @BindView(R.id.et_ecg_top)
    EditText mEtEcgTop;
    @BindView(R.id.et_ecg_bottom)
    EditText mEtEcgBottom;
    @BindView(R.id.et_resp_top)
    EditText mEtRespTop;
    @BindView(R.id.et_resp_bottom)
    EditText mEtRespBottom;
    @BindView(R.id.et_temp_top)
    EditText mEtTempTop;
    @BindView(R.id.et_temp_bottom)
    EditText mEtTempBottom;
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

    public static final String ACTION_PARAM_SETTING = "action_param_setting";

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
                saveData();
                break;
        }
    }

    /**
     * 保存数据到本地
     */
    private void saveData() {
        String spTop = mEtSpTop.getText().toString().trim();
        String spBottom = mEtSpBottom.getText().toString().trim();
        String ecgTop = mEtEcgTop.getText().toString().trim();
        String ecgBottom = mEtEcgBottom.getText().toString().trim();
        String respTop = mEtRespTop.getText().toString().trim();
        String respBottom = mEtRespBottom.getText().toString().trim();
        String tempTop = mEtTempTop.getText().toString().trim();
        String tempBottom = mEtTempBottom.getText().toString().trim();
        String sbpTop = mEtSbpTop.getText().toString().trim();
        String sbpBottom = mEtSpBottom.getText().toString().trim();
        String dbpTop = mEtDbpTop.getText().toString().trim();
        String dbpBottom = mEtDbpBottom.getText().toString().trim();
        String mapTop = mEtMapTop.getText().toString().trim();
        String mapBottom = mEtMapBottom.getText().toString().trim();


        if (StringUtils.string2Int(spTop) > Constant.SPO2TOP ||
                StringUtils.string2Int(spBottom) < Constant.SPO2BOTTOM ||
                StringUtils.string2Int(spBottom) > StringUtils.string2Int(spTop)) {
            Toast.makeText(this, "Spo2参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.string2Int(ecgTop) > Constant.ECGTOP ||
                StringUtils.string2Int(ecgBottom) < Constant.ECGBOTTOM ||
                StringUtils.string2Int(ecgBottom) > StringUtils.string2Int(ecgTop)) {
            Toast.makeText(this, "Ecg参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.string2Int(respTop) > Constant.RESPTOP ||
                StringUtils.string2Int(respBottom) < Constant.RESPBOTTOM ||
                StringUtils.string2Int(respBottom) > StringUtils.string2Int(respTop)) {
            Toast.makeText(this, "Resp参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.string2Int(tempTop) > Constant.TEMPTOP ||
                StringUtils.string2Int(tempBottom) < Constant.TEMPBOTTOM ||
                StringUtils.string2Int(tempBottom) > StringUtils.string2Int(tempTop)) {
            Toast.makeText(this, "Temp参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.string2Int(sbpTop) > Constant.SBPTOP ||
                StringUtils.string2Int(sbpBottom) < Constant.SBPBOTTOM ||
                StringUtils.string2Int(sbpBottom) > StringUtils.string2Int(sbpTop)) {
            Toast.makeText(this, "SBP参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.string2Int(dbpTop) > Constant.DBPTOP ||
                StringUtils.string2Int(dbpBottom) < Constant.DBPBOTTOM ||
                StringUtils.string2Int(dbpBottom) > StringUtils.string2Int(dbpTop)) {
            Toast.makeText(this, "DBP参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.string2Int(mapTop) > Constant.MAPTOP ||
                StringUtils.string2Int(mapBottom) < Constant.MAPBOTTOM ||
                StringUtils.string2Int(mapBottom) > StringUtils.string2Int(mapTop)) {
            Toast.makeText(this, "MAP参数超出限定范围", Toast.LENGTH_SHORT).show();
            return;
        }
        EcgSharedPrefrence.setSpo2Upper(this, spTop);
        EcgSharedPrefrence.setSpo2Floor(this, spBottom);
        EcgSharedPrefrence.setEcgUpper(this, ecgTop);
        EcgSharedPrefrence.setEcgFloor(this, ecgBottom);
        EcgSharedPrefrence.setRespUpper(this, respTop);
        EcgSharedPrefrence.setRespFloor(this, respBottom);
        EcgSharedPrefrence.setTempUpper(this, tempTop);
        EcgSharedPrefrence.setTempFloor(this, tempBottom);
        EcgSharedPrefrence.setSbpUpper(this, sbpTop);
        EcgSharedPrefrence.setSbpFloor(this, sbpBottom);
        EcgSharedPrefrence.setDbpUpper(this,dbpTop);
        EcgSharedPrefrence.setDbpFloor(this, dbpBottom);
        EcgSharedPrefrence.setMapUpper(this, mapTop);
        EcgSharedPrefrence.setMapFloor(this, mapBottom);
        EcgSharedPrefrence.setRing(this, mSwitchView.isChecked());

        HashMap<String, String> params = new HashMap<>();
        params.put("action", ACTION_PARAM_SETTING);
        EventBus.getDefault().post(params);
        finish();
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        mTitle.setText("参数设置");
    }

    @Override
    protected void initData() {
        super.initData();

        String spo2Upper = EcgSharedPrefrence.getSpo2Upper(this);
        String spo2Floor = EcgSharedPrefrence.getSpo2Floor(this);
        String ecgUpper = EcgSharedPrefrence.getEcgUpper(this);
        String ecgFloor = EcgSharedPrefrence.getEcgFloor(this);
        String respUpper = EcgSharedPrefrence.getRespUpper(this);
        String respFloor = EcgSharedPrefrence.getRespFloor(this);
        String tempUpper = EcgSharedPrefrence.getTempUpper(this);
        String tempFloor = EcgSharedPrefrence.getTempFloor(this);
        String sbpUpper = EcgSharedPrefrence.getSbpUpper(this);
        String sbpFloor = EcgSharedPrefrence.getSbpFloor(this);
        String dbpUpper = EcgSharedPrefrence.getDbpUpper(this);
        String dbpFloor = EcgSharedPrefrence.getDbpFloor(this);
        String mapUpper = EcgSharedPrefrence.getMapUpper(this);
        String mapFloor = EcgSharedPrefrence.getMapFloor(this);
        boolean ring = EcgSharedPrefrence.getRing(this);

        if (!TextUtils.isEmpty(spo2Upper)) {
            mEtSpTop.setText(spo2Upper);
        }
        if (!TextUtils.isEmpty(spo2Floor)) {
            mEtSpBottom.setText(spo2Floor);
        }
        if (!TextUtils.isEmpty(ecgUpper)) {
            mEtEcgTop.setText(ecgUpper);
        }
        if (!TextUtils.isEmpty(ecgFloor)) {
            mEtEcgBottom.setText(ecgFloor);
        }
        if (!TextUtils.isEmpty(respUpper)) {
            mEtRespTop.setText(respUpper);
        }
        if (!TextUtils.isEmpty(respFloor)) {
            mEtRespBottom.setText(respFloor);
        }
        if (!TextUtils.isEmpty(tempUpper)) {
            mEtTempTop.setText(tempUpper);
        }
        if (!TextUtils.isEmpty(tempFloor)) {
            mEtTempBottom.setText(tempFloor);
        }
        if (!TextUtils.isEmpty(sbpUpper)) {
            mEtSbpTop.setText(sbpUpper);
        }
        if (!TextUtils.isEmpty(sbpFloor)) {
            mEtSbpBottom.setText(sbpFloor);
        }
        if (!TextUtils.isEmpty(dbpUpper)) {
            mEtDbpTop.setText(dbpUpper);
        }
        if (!TextUtils.isEmpty(dbpFloor)) {
            mEtDbpBottom.setText(dbpFloor);
        }
        if (!TextUtils.isEmpty(mapUpper)) {
            mEtMapTop.setText(spo2Upper);
        }
        if (!TextUtils.isEmpty(mapFloor)) {
            mEtMapBottom.setText(mapFloor);
        }
        mSwitchView.setChecked(ring);
    }
}
