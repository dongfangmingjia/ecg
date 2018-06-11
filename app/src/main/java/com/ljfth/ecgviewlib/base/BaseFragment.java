package com.ljfth.ecgviewlib.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by warner on 2017/9/29.
 */

public abstract class BaseFragment extends android.support.v4.app.Fragment {

	private View mRoot;
	private Unbinder mUnbinder;
	private FragmentActivity mActivity;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.mActivity = (FragmentActivity) context;

		// 初始化参数
		initArgs(getArguments());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (mRoot == null) {
			int layoutId = getContentLayoutId();
			// 初始化当前的根布局，但是不在创建时就添加到container里面
			View root = inflater.inflate(layoutId, container, false);
			initWidget(root);
			mRoot = root;
		} else {
			if (mRoot.getParent() != null) {
				// 把当前Root从父控件中移除
				((ViewGroup)mRoot.getParent()).removeView(mRoot);
			}
		}
		return mRoot;
	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// view创建完后，初始化数据
		initData();
	}

	/**
	 * 初始化相关参数
	 */
	protected void initArgs(Bundle bundle) {

	}

	/**得到当前界面的资源ID*/
	protected abstract int getContentLayoutId();

	/**初始化控件*/
	protected void initWidget(View root) {
		mUnbinder = ButterKnife.bind(this, root);
	};

	/**初始化数据*/
	protected void initData() {

	}

	/**
	 * 返回按键触发时调用
	 * @return true:返回逻辑我已处理，activity不必finish
	 * 		   false:返回逻辑没有处理，activity走自己的逻辑
	 */
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}
}
