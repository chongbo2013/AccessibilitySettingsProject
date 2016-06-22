package com.zyq.accessibility.setting.common.receiver;

import android.content.Context;

/**
 * @author zyq 16-5-17
 */
public class PhoneStateReceiverUtil extends BaseReceiverUtil {

	public PhoneStateReceiverUtil(Context context){
		super(context,"android.intent.action.PHONE_STATE");
	}

	public PhoneStateReceiverUtil getInstance(PhoneStateReceiverListener c){
		super.setBroadcastReceiver(new PhoneStateReceiver(this),c);
		return this;
	}
}
