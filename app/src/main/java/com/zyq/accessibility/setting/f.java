package com.zyq.accessibility.setting;


import com.zyq.accessibility.activity.SettingActivity;
import com.zyq.accessibility.setting.a.Result;

/**
 * @author zyq 16-5-23
 */
public class f implements Result {

	private SettingActivity mSettingActivity;

	public f(SettingActivity settingActivity){
		this.mSettingActivity = settingActivity;
	}

	@Override
	public void onResult(boolean arg) {

	}
}
