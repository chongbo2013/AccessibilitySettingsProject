package com.zyq.accessibility.setting.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author zyq 16-5-17
 */
public class PhoneStateReceiver extends BroadcastReceiver {

	private PhoneStateReceiverUtil mPhoneStateReceiver;
	public PhoneStateReceiver(PhoneStateReceiverUtil receiver){
		super();
		this.mPhoneStateReceiver = receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(this.mPhoneStateReceiver.mObject instanceof PhoneStateReceiverListener){
			((PhoneStateReceiverListener) this.mPhoneStateReceiver.mObject).onPhoneStateReceive();
		}
	}
}
