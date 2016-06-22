package com.zyq.accessibility.setting.common.receiver;

import android.content.Context;

/**
 * @author zyq 16-5-17
 */
public class CloseDialogReceiverUtil extends BaseReceiverUtil{

	public CloseDialogReceiverUtil(Context context){
		super(context,"android.intent.action.CLOSE_SYSTEM_DIALOGS");
	}

	public CloseDialogReceiverUtil getInstance(CloseDialogReceiverListener f){
		super.setBroadcastReceiver(new CloseDialogReceiver(this),f );
		return this;
	}
}
