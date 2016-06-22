package com.zyq.accessibility.setting.a.temp;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.zyq.accessibility.NativeParams;
import com.zyq.accessibility.activity.GlobalConstants;
import com.zyq.accessibility.setting.SettingService;
import com.zyq.accessibility.setting.a.ServiceConfigUtil;
import com.zyq.accessibility.setting.a.SettingsUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@TargetApi(16)
public class Task {//taskName
	public static final boolean DEBUG = true;
	public static final String TAG = "taskName";
	public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "detail";
	public static final String ACTION_APPLICATION_DETAILS_MIGOLOCKER = "migoLocker_detail";
	public static final String ACTION_MAIN = "main";
	public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "notify";
	public static final String ACTION_USAGE_ACCESS_SETTINGS = "usage_access";
	private String taskId = null;
	//有 显示悬浮窗 ,显示通知,开启消息功能,神隐模式,开机自启动,锁屏安全性设置,三星手机防关闭,
	//OPPO 手机防关闭,乐安全清理白名单,安全中心白名单,vivo手机防关闭,金立手机防关闭,FreemeOs必要设置
	//DidoOs必要设置,锁屏必要权限,手机管家清理白名单,360卫士清理白名单,360卫士信任微锁屏,百度卫士清理白名单
	//360清理白名单,一键清理大师白名单,猎豹清理白名单,电池医生清理白名单,超级管理后台白名单,LBE大师清理白名单
	//百度助手清理白名单,91助手清理白名单,腾讯手机管家白名单,
	private String taskName = null;//任务名称
	private List romList = new ArrayList();//
	private List followUpList = new ArrayList();
	private String packageName = null;//表示包名
	private int minVersion = 0;
	private int maxVersion = -1;
	private String activity = null;//表示要启动的activity
	private List taskActionList = new ArrayList();
	public boolean isTextAnim = false;
	private String intent = null;
	private Bundle extraData = null;
	private String intentData = null;
	private Iterator taskActionIterator = null;
	private TaskAction taskAction = null;
	private boolean isTaskFinished = false;

	public void clear(){
		if(romList != null){
			romList.clear();
		}
		romList = null;
		if(followUpList != null){
			followUpList.clear();
		}
		followUpList = null;
		if(taskActionList!=null){
			taskActionList.clear();
		}
		taskActionList = null;
		if(taskActionIterator != null){
			taskActionIterator = null;
		}
		activity = null;
		extraData = null;
	}

	public Task(){};
	private static int getPackageVersion(PackageManager packageManager, String str) {
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(str, PackageManager.GET_META_DATA);
			return packageInfo != null ? packageInfo.versionCode : -1;
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}

	//这块有可能有bug
	public final boolean execute() {
		Action currentAction = getCurrentAction();
		ServiceConfigUtil instance = ServiceConfigUtil.getInstance();
		if (currentAction == null) {
			return false;
		}
		this.taskAction.c = false;
		int execute = currentAction.execute(this.packageName);
		boolean isActionFinished = Action.isActionFinished(execute);
		if(GlobalConstants.DEBUG) {
			Log.d("10", "当前指令的包名是：" + this.packageName + " 执行的任务是:" + currentAction.getActionId() + (isActionFinished ? " 指令执行成功" : " 指令执行失败") + "执行后的值是:" + execute);
		}
		if (isActionFinished) {
			setActionFinished();//表示任务已经完成
			this.isTaskFinished = Action.isTaskFinished(execute);//若其中一个任务超过288或者１时候，就执行下个task，不再该task上浪费时间了．
			if (this.isTaskFinished) {
				//干掉应用
				if(packageName != null) {
					//FORCE_STOP_PACKAGES 需要该权限.....
//					Log.d("20","任务结束,干掉包名"+this.packageName);
//					ShellUtils.CommandResult result = ShellUtils.execCommand("am force-stop " + this.packageName,false);
//				    Log.d("20","执行结果是:"+result.successMsg + " 失败结果是:"+result.errorMsg);
				}
				boolean isSuccessed = Action.isSuccessed(execute);
				instance.setTaskStatus(this, true);//保存已经完成该任务
				while (this.taskActionIterator.hasNext()) {
					this.taskActionIterator.next();
				}
				if(GlobalConstants.DEBUG) {
					Log.d("10", "指令执行是否成功：" + isSuccessed);
				}
			}
			if (Action.isActionBreak(execute)) {//(i & 256) != 0,回报失误的原因．
				if(GlobalConstants.DEBUG) {
					Log.d("10", "任务被打断啊啊啊啊啊啊");
				}
				String str = currentAction.getActionId() + SettingService.a(SettingService.getInstance().getRootInActiveWindow());
				if (str.length() > 255) {
					str = str.substring(0, 255);
				}
				if(GlobalConstants.DEBUG) {
					Log.d("10","任务被打断:"+str);
				}
			}
		}
		return isActionFinished;
	}

	public final boolean finished() {
		return this.isTaskFinished;
	}

	public final String getActivityClassName() {
		return this.activity;
	}

	public final String getAppPackageName() {
		return this.packageName;
	}

	public final Action getCurrentAction() {
		if (!finished() && (this.taskAction == null || this.taskAction.actionFinished)) {
			if (this.taskActionIterator == null) {
				this.taskActionIterator = this.taskActionList.iterator();
			}
			if (this.taskActionIterator.hasNext()) {
				this.taskAction = (TaskAction) this.taskActionIterator.next();
				if (this.taskAction.getAction() != null) {
					this.taskAction.getAction().resetTime();
				}
			} else {
				this.isTaskFinished = true;
				this.taskAction = null;
				ServiceConfigUtil.getInstance().setTaskStatus(this, true);
			}
		}
		if (this.taskAction == null) {
			return null;
		}
		Action action = this.taskAction.getAction();
		if(GlobalConstants.DEBUG) {
			Log.d("10", "获取指令是：" + action.getActionId());
		}
		return action;
	}

	public final int getEstimatedTimeOfCompletion() {
		int i = 0;
		for (int i2 = 0; i2 < this.taskActionList.size(); i2++) {
			TaskAction taskActionVar = (TaskAction) this.taskActionList.get(i2);
			if (!taskActionVar.actionFinished) {
				Action action = taskActionVar.getAction();
				if (action != null) {
					i += action.getTimeout();
				}
			}
		}
		return i;
	}

	public final Intent getIntent() {
		Intent intent;
		if (this.intent == null) {
			intent = new Intent();
		} else if (ACTION_MAIN.equals(this.intent)) {
			intent = new Intent("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
		} else if(ACTION_NOTIFICATION_LISTENER_SETTINGS.equals(this.intent)){
            intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		} else if(ACTION_APPLICATION_DETAILS_SETTINGS.equals(this.intent)){
			intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		} else if (ACTION_USAGE_ACCESS_SETTINGS.equals(this.intent)) {
			intent = new Intent("android.settings.USAGE_ACCESS_SETTINGS");
		} else if (ACTION_APPLICATION_DETAILS_MIGOLOCKER.equals(this.intent)) {
			intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", "com.luna.screenlocker", null));
			intent.setFlags(276824064);
			return intent;
		} else {
//			intent = ACTION_NOTIFICATION_LISTENER_SETTINGS.equals(this.intent) ? new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS") : ACTION_APPLICATION_DETAILS_SETTINGS.equals(this.intent) ? new Intent("android.settings.APPLICATION_DETAILS_SETTINGS") : ACTION_USAGE_ACCESS_SETTINGS.equals(this.intent) ? new Intent("android.settings.USAGE_ACCESS_SETTINGS") : new Intent(this.intent);
		    intent = new Intent(this.intent);
		}
		if (this.extraData != null) {
			intent.putExtras(this.extraData);
		}
		if (this.intentData != null) {
			intent.setData(Uri.parse(this.intentData));
		}
		if (this.packageName != null) {
			intent.setComponent(new ComponentName(this.packageName, this.activity));
		}

		intent.addFlags(268468224);
		return intent;
	}

	public final String getIntentAction() {
		return this.intent;
	}

	public final String getIntentData() {
		return this.intentData;
	}

	public final int getNextActionTimeout() {
		int i = 0;
		while (i < this.taskActionList.size()) {
			TaskAction taskActionVar = (TaskAction) this.taskActionList.get(i);
			if (taskActionVar.actionFinished) {
				i++;
			} else {
				Action action = taskActionVar.getAction();
				return action != null ? action.getTimeout() : 0;
			}
		}
		return 0;
	}

	public final String getTaskId() {
		return this.taskId;
	}

	public final String getTaskName() {
		return this.taskName;
	}

	public final List getmFollowUp() {
		return this.followUpList;
	}

	public final boolean isNotificationListenerSetting() {
		return ACTION_NOTIFICATION_LISTENER_SETTINGS.equals(this.intent);
	}

	public final boolean isOnSetting() {
		return this.taskActionIterator != null;
	}

	public final void resetActionStatus() {
		if(GlobalConstants.DEBUG) {
			Log.d("10", "重置任务所有指令状态");
		}
		for (int i = 0; i < this.taskActionList.size(); i++) {
			((TaskAction) this.taskActionList.get(i)).init();
		}
		this.isTaskFinished = false;
		this.taskActionIterator = null;
		this.taskAction = null;
	}

	public final void setActionFinished() {
		this.taskAction.actionFinished = true;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "activity")
	public final void setActivityClassName(String str) {
		this.activity = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "pkg_name")
	public final void setAppPackageName(String str) {
		this.packageName = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "action")
	public final void setCheckAction(String str) {
		this.taskActionList.add(new TaskAction(this, str));
	}

	@com.zyq.accessibility.setting.common.a.b(a = "rom")
	public final void setRomFeature(String str) {
		this.romList.add(str);
	}

	public final void setFinished(boolean z) {
		this.isTaskFinished = z;
		if (!this.isTaskFinished) {
			resetActionStatus();
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "follow_up")
	public final void setFollowUp(String str) {
		this.followUpList.add(str);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "intent")
	public final void setIntentAction(String str) {
		this.intent = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "data")
	public final void setIntentData(String str) {
		this.intentData = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "extra")
	public final void setIntentExtra(String str) {
		if (this.extraData == null) {
			this.extraData = new Bundle();
		}
		String[] split = str.split("=");
		String str2 = split[0];
		String str3 = split[1];
		if (str3.startsWith("$uid@")) {
			try {
				//获取当前应用的uid
				//跳转到自己权限的设置界面
				this.extraData.putInt(str2, ServiceConfigUtil.getInstance().getContext().getPackageManager().getPackageInfo(str3.substring(5), 0).applicationInfo.uid);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		} else if (TextUtils.isDigitsOnly(str3)) {
			this.extraData.putInt(str2, Integer.valueOf(str3).intValue());
		} else {
			this.extraData.putString(str2, str3);
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "max_version")
	public final void setMaxVersion(int i) {
		this.maxVersion = i;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "min_version")
	public final void setMinVersion(int i) {
		this.minVersion = i;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "id")
	public final void setTaskId(String str) {
		this.taskId = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "task_name")
	public final void setTaskName(String str) {
		this.taskName = str;
	}

	public final void skipActions() {
		if(GlobalConstants.DEBUG) {
			Log.d("10", "skipActions");
		}
		if (this.taskActionIterator == null) {
			this.taskActionIterator = this.taskActionList.iterator();
		}
		while (this.taskActionIterator.hasNext()) {
			this.taskAction = (TaskAction) this.taskActionIterator.next();
			this.taskAction.actionFinished = true;
		}
	}

	public final boolean startActivity(Context context) {
		boolean z = false;
		if (!finished()) {
			try {
				context.startActivity(getIntent());
				z = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(GlobalConstants.DEBUG) {
			Log.d("10", "现在要打开：" + this.packageName + ":" + this.activity);
		}
		return z;
	}

	public final boolean validateIntent(PackageManager packageManager) {
//		Log.d("6","满足的romList:"+this.romList);
		boolean result = SettingsUtil.validateIntent(packageManager,getIntent());
		if(GlobalConstants.DEBUG) {
			Log.d("18", "validateIntent:本身intent:" + getIntentAction() + " 结果是:" + result);
		}
		return result;
	}

	public final boolean validateRom(Set set) {
		if(DEBUG){
			StringBuilder sb = new StringBuilder();
			for(int i = 0;i<romList.size();i++){
				sb.append((romList.get(i)));
			}
		}
		boolean DEBUG = false;

		if(this.romList.contains("-meizu")){
			DEBUG = true;
		}

		boolean z = this.romList.size() == 0;
		int i = 0;
		boolean z2 = false;
		while (!z && i < this.romList.size()) {//可能哟多种
			String obj = (String) this.romList.get(i);
			if(GlobalConstants.DEBUG) {
				Log.d("18","rom :"+obj);
			}
			z2 = obj.startsWith("-");//表示以"－"开头的,返回true，表示该rom 不该存在该set中
			if (z2) {
				obj = obj.substring(1);
			}
			if(GlobalConstants.DEBUG) {
				Log.d("18", "remove - " + obj);
			}
			i++;
			z = set.contains(obj);
			if(GlobalConstants.DEBUG) {
				Log.d("18","z:"+z);
			}
			z = z2?!z:z;
		}
        boolean result =z;
		if(GlobalConstants.DEBUG) {
			Log.d("18", "romList:" + this.romList + " result:" + result);
		}
		return result;
	}

	public final boolean validateVersion(PackageManager packageManager) {
		StringBuilder sb = new StringBuilder();
		if(this.extraData != null) {
			Bundle args = this.extraData;
			if (args.containsKey("extra_pkgname")) {
				sb.append("extra_pkgname-->").append(extraData.getString("extra_pkgname"));
			} else if (args.containsKey("extra_package_uid")) {
				sb.append("extra_package_uid-->").append(extraData.getString("extra_package_uid"));
			}
		}


		if (this.packageName == null) {
			return true;
		}
		if (this.minVersion <= 0 && this.maxVersion <= 0) {
			return true;
		}
		int version = getPackageVersion(packageManager, this.packageName);
		boolean result = version >= this.minVersion && (this.maxVersion == -1 || version < this.maxVersion);



		return result;
	}

	private static String getTaskStatusTag(Task taskVar) {
		return taskVar.getTaskId() != null ? taskVar.getTaskId() :
				(taskVar.getIntentAction() == null || NativeParams.ACTION_MAIN.equals(taskVar.getIntentAction())) ?
						taskVar.getAppPackageName() : taskVar.getIntentAction();
	}

	public  final boolean containTaskStatus(String str) {
		if (str == null) {
			return false;
		}
		boolean z = ServiceConfigUtil.getInstance().getSp().getBoolean(str, false);
		if(GlobalConstants.DEBUG) {
			Log.d(TAG,"containTaskStatus:"+new StringBuilder("getStatus:").append(str).append("=").append(z).toString());
		}
		return z;
	}

	public boolean  isInitFinished(){
		return containTaskStatus(getTaskStatusTag(this));
	}
}