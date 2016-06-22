package com.zyq.accessibility.setting.common.receiver;

import android.content.Context;

/**
 * @author zyq 16-5-17
 */
public class ScreenOffReceiverUtil extends BaseReceiverUtil {

	public ScreenOffReceiverUtil(Context context){
		super(context,"android.intent.action.SCREEN_OFF");
	}

	public ScreenOffReceiverUtil getInstance(ScreenOffReceiverListener f){
		super.setBroadcastReceiver(new ScreenOffReceiver(this),f );
		return this;
	}
}
