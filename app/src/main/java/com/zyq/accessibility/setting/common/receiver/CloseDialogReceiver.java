package com.zyq.accessibility.setting.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * @author zyq 16-5-17
 */
public class CloseDialogReceiver extends BroadcastReceiver {

	private String a;
	private String b;
	private String c;
	private String d;
	private CloseDialogReceiverUtil mCloseDialogReceiverUtil;
	public CloseDialogReceiver(CloseDialogReceiverUtil closeDialogReceiverUtil){
		super();
		this.mCloseDialogReceiverUtil = closeDialogReceiverUtil;
		this.a = "reason";
		this.b = "globalactions";
		this.c = "recentapps";
		this.d = "homekey";
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String v1 = intent.getAction();
		if(v1.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")){
			String v2 = intent.getStringExtra("reason");
			if(v2 != null){
				if(mCloseDialogReceiverUtil.mObject != null && (v2.equals("homekey"))){
                    if(mCloseDialogReceiverUtil.mObject instanceof CloseDialogReceiverListener){
	                    ((CloseDialogReceiverListener) mCloseDialogReceiverUtil.mObject).onCloseDialogReceive();
                    }
				}
			}
		}
	}
}
