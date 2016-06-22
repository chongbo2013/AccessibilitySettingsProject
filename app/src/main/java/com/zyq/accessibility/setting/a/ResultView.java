package com.zyq.accessibility.setting.a;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

/**
 * @author zyq 16-5-16
 */
public class ResultView extends View {

	private Handler mHandler;
	private WindowManager mWindowManager;
	private Result mResult;
	public ResultView(Context context, Handler handler, WindowManager wm, Result result){
		super(context);
		this.mHandler = handler;
		this.mWindowManager = wm;
		this.mResult = result;
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
//		super.onWindowFocusChanged(hasWindowFocus);
		this.mHandler.removeCallbacksAndMessages(null);
		if(this.getParent() != null){
			this.mWindowManager.removeView((View)this);
		}
		this.mResult.onResult(true);
	}
}
