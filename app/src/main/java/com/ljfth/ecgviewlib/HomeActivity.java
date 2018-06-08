package com.ljfth.ecgviewlib;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.allen.library.SuperTextView;
import com.ljfth.ecgviewlib.base.BaseActivity;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_home;
    }


    @Override
    protected void initWidget() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, new HomeFragment());
        transaction.commit();

        initView();
    }

    private void initView() {
        SuperTextView stvAbout = findViewById(R.id.stv_about);
        stvAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, AboutActivity.class));
    }
}
