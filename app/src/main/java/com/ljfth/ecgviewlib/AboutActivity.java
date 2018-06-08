package com.ljfth.ecgviewlib;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.ljfth.ecgviewlib.base.BaseActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected int getcontentLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initWidget() {
        TextView title = findViewById(R.id.title);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
