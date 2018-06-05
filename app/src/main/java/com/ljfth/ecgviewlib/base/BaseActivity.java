package com.ljfth.ecgviewlib.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Created by warner on 2017/9/29.
 */

public abstract class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在界面未初始化之前调用的初始化窗口
		initWindows();

		if (initArgs(getIntent().getExtras())) {
			setContentView(getcontentLayoutId());
			initWidget();
			initData();
		} else {
			finish();
		}
	}

	/**初始化窗口*/
	protected void initWindows() {

	}

	/**
	 * 初始化相关参数
	 * 参数正确返回true，错误返回false
	 */
	protected boolean initArgs(Bundle bundle) {
		return true;
	}

	/**获取界面资源ID*/
	protected abstract int getcontentLayoutId();

	/**初始化控件*/
	protected void initWidget() {
	}

	/**初始化数据*/
	protected void initData() {

	}


	@Override
	public boolean onSupportNavigateUp() {
		// 当点击界面导航返回时，finish当前界面
		finish();
		return super.onSupportNavigateUp();
	}

	@Override
	public void onBackPressed() {
		// 获取当前activity下的所有的Fragment
		@SuppressLint("RestrictedApi")
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		// 判断是否为空
		if (fragments != null && fragments.size() > 0) {
			for (Fragment fragment :fragments) {
				// 判断是否是我们能够处理的fragment的类型
				if (fragment instanceof BaseFragment) {
					// 判断是否拦截了返回按钮
					if (((BaseFragment) fragment).onBackPressed()) {
						// 拦截返回按钮，直接return
						return;
					}
				}
			}
		}
		super.onBackPressed();
		finish();
	}
}
