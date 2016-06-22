package com.zyq.accessibility.setting.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * @author zyq 16-5-17
 */
public abstract class BaseReceiverUtil {

	protected Context mContext;
	protected Object mObject;
	protected String filter;
	protected BroadcastReceiver mBroadcastReceiver;

	protected BaseReceiverUtil(Context context, String arg){
		super();
		this.mContext = context;
		this.filter = arg;
	}

	public void register(){
		if(this.mContext != null){
			this.mContext.registerReceiver(mBroadcastReceiver,new IntentFilter(this.filter));
		}
	}

	public void setBroadcastReceiver(BroadcastReceiver broadcastReceiver,Object object){
		this.mBroadcastReceiver = broadcastReceiver;
		this.mObject = object;
	}

	public void unregister(){
		if(this.mContext != null){
			this.mContext.unregisterReceiver(mBroadcastReceiver);
		}
	}
}
