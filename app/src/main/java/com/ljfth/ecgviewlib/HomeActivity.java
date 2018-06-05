package com.ljfth.ecgviewlib;

import android.support.v4.app.FragmentTransaction;

import com.ljfth.ecgviewlib.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_home;
    }


    @Override
    protected void initWidget() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, new HomeFragment());
        transaction.commit();
    }
}
