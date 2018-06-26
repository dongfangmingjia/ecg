package com.ljfth.ecgviewlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import com.allen.library.SuperTextView;
import com.ljfth.ecgviewlib.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_home;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, new HomeFragment());
        transaction.commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    @OnClick({R.id.stv_patient_info, R.id.stv_device_attachment, R.id.stv_param_setting,
            R.id.stv_save, R.id.stv_reset, R.id.stv_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_patient_info:
                startActivity(new Intent(HomeActivity.this, PatientInfoActivity.class));
                break;
            case R.id.stv_device_attachment:
                break;
            case R.id.stv_param_setting:
                startActivity(new Intent(HomeActivity.this, ParamSettingActivity.class));
                break;
            case R.id.stv_save:
                break;
            case R.id.stv_reset:
                startActivity(new Intent(HomeActivity.this, ResetActivity.class));
                break;
            case R.id.stv_about:
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                break;
        }
    }
}
