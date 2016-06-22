package com.zyq.accessibility.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zyq.accessibility.R;

/**
 * @author zyq 16-6-22
 */
public class BaseActivity extends Activity implements View.OnClickListener {

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	/**
	 * 如果Activity引用了titleBar的可以设置标题
	 *
	 * @param title
	 */



	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {

	}
}
