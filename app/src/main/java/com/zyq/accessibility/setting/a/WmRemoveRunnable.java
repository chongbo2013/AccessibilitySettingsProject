package com.zyq.accessibility.setting.a;

import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

/**
 * @author zyq 16-5-16
 */
public class WmRemoveRunnable implements Runnable {

	private View mView;
	private Handler mHandler;
	private Result mResult;
	private WindowManager mWindowManager;

	public WmRemoveRunnable(WindowManager wm,View view,Handler handler,Result result){
		super();
		this.mWindowManager = wm;
		this.mView = view;
		this.mHandler = handler;
		this.mResult = result;
	}

	@Override
	public void run() {
		this.mWindowManager.removeView(this.mView);
		this.mHandler.removeCallbacksAndMessages(null);
		this.mResult.onResult(false);
	}
}
