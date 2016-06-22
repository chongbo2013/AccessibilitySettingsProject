package com.zyq.accessibility.setting.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author zyq 16-5-17
 */
public class ScreenOffReceiver extends BroadcastReceiver {

	private ScreenOffReceiverUtil mScreenOffReceiverUtil;
	public ScreenOffReceiver(ScreenOffReceiverUtil screenOffReceiverUtil){
		super();
		this.mScreenOffReceiverUtil = screenOffReceiverUtil;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	      if(intent.getAction().equals("android.intent.action.SCREEN_OFF")){
		      if(this.mScreenOffReceiverUtil.mObject instanceof ScreenOffReceiverListener){
			      ((ScreenOffReceiverListener) this.mScreenOffReceiverUtil.mObject).onScreenOffReceive();
		      }
	      }
	}
}
