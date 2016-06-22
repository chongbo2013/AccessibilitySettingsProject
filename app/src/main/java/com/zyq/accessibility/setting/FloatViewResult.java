package com.zyq.accessibility.setting;


import com.zyq.accessibility.activity.SettingActivity;
import com.zyq.accessibility.setting.a.Result;

/**
 * @author zyq 16-5-19
 */
public class FloatViewResult implements Result {

	private SettingActivity mSettingActivity;

	public FloatViewResult(SettingActivity settingActivity){
		this.mSettingActivity = settingActivity;
	}

	@Override
	public void onResult(boolean arg) {
		this.mSettingActivity.h = arg;
	}
}
