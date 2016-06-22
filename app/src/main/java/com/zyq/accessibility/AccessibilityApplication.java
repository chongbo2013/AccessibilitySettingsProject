package com.zyq.accessibility;

import android.app.Application;

/**
 * @author zyq 16-6-22
 */
public class AccessibilityApplication extends Application {

	public static AccessibilityApplication instance;

	public static AccessibilityApplication getInstance(){
		return  instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
}
