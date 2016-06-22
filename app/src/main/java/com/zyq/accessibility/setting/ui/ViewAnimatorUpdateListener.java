package com.zyq.accessibility.setting.ui;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * @author zyq 16-5-19
 */
public class ViewAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

	private View v;

	public ViewAnimatorUpdateListener(View view){
		this.v = view;
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		Integer num = (Integer) animation.getAnimatedValue();
		this.v.getLayoutParams().height = num.intValue();
		this.v.requestLayout();
	}
}
