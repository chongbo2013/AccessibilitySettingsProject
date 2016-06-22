package com.zyq.accessibility.setting.a;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;


import com.zyq.accessibility.NativeParams;
import com.zyq.accessibility.activity.GlobalConstants;
import com.zyq.accessibility.activity.SettingActivity;
import com.zyq.accessibility.setting.SettingService;
import com.zyq.accessibility.setting.a.temp.Action;
import com.zyq.accessibility.setting.a.temp.Rom;
import com.zyq.accessibility.setting.a.temp.Task;
import com.zyq.accessibility.setting.common.a.ConfigParseUtil;
import com.zyq.accessibility.utils.UsageStatsUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zyq 16-5-17
 */
public final class ServiceConfigUtil extends com.zyq.accessibility.setting.common.data.a {
	public static final boolean DEBUG = true;
	public static final String TAG = "ServiceConfigUtil";
	public static final String SHARED_KEY_TASK_STATUS = "onekeysetting_status";
	private static ServiceConfigUtil ServiceConfigUtil = null;
	private Context mContext = null;
	private Task task = null;//表示当前task
	private int currentTaskIndex = -1;
	private int allTime = -1;//包括cleanTask 的事件和各个task执行后的时间总和
	private int f = -1;
	private boolean g = false;
	private Set romSet = new HashSet();
	private Set taskSet = new HashSet();
	private Map actionMap = new HashMap();
	private final List taskList = new ArrayList();
	private Task cleanTask = null;
	private SharedPreferences sp = null;
	private Map n = null;
	private String[] o = new String[]{"SYS_AUTO_BOOT_VIVO", "SYS_AUTO_BOOT_VIVO+", "vivo_funtouch_i_2.5+", "FLOAT_WIN_VIVO","SHOW_NOTIFY"};

	public ServiceConfigUtil() {

	}

	private static String getTaskStatusTag(Task taskVar) {
		return taskVar.getTaskId() != null ? taskVar.getTaskId() :
				(taskVar.getIntentAction() == null || NativeParams.ACTION_MAIN.equals(taskVar.getIntentAction())) ?
						taskVar.getAppPackageName() : taskVar.getIntentAction();
	}

	private final void putBoolean(String str, boolean z) {
		if (str != null && !str.isEmpty()) {
			SharedPreferences.Editor edit = this.sp.edit();
			if (containTaskStatus(str) && !z) {
				edit.remove(str);
			} else if (z) {
				edit.putBoolean(str, z);
			}
			edit.commit();
			if(GlobalConstants.DEBUG) {
				Log.d("16", "保存 "+str + " 设置"+" 结果是:"+ z);
			}

		}
	}

	public  final boolean containTaskStatus(String str) {
		if (str == null) {
			return false;
		}
		boolean z = this.sp.getBoolean(str, false);
		if(GlobalConstants.DEBUG) {
			Log.d(TAG,"containTaskStatus:"+new StringBuilder("getStatus:").append(str).append("=").append(z).toString());
		}
		return z;
	}

	private final void calculateAllTime() {
		int i = 0;
		for (int i2 = 0; i2 < this.taskList.size(); i2++) {
			Task taskVar = (Task) this.taskList.get(i2);
			if (!taskVar.finished()) {
				i += taskVar.getEstimatedTimeOfCompletion();
			}
		}
		if(this.cleanTask != null) {
			this.f = this.cleanTask.getEstimatedTimeOfCompletion();
		}
		this.allTime = this.f + i;
	}

	@TargetApi(16)
	private final void preventErrorSituation() {
		String appPackageName = this.task.getAppPackageName();
		if (!TextUtils.isEmpty(appPackageName)) {
			List list;
			if (this.n == null) {
				this.n = new HashMap();
				String[] split = "com.tencent.qqpimsecure#进入首页>#skip,com.tencent.qqpimsecure#手机卡慢 空间不足#skip,com.tencent.qqpimsecure#《腾讯手机管家许可及服务协议》#skip,ccom.tencent.qqpimsecure#All Rights Reserved#skip,om.tencent.qqpimsecure#手机卡慢 空间不足#skip,com.qihoo.cleandroid_cn#《安装许可协议》#skip".split(",");
				for (String split2 : split) {//com.tencent.qqpimsecure#进入首页>#skip
					String[] split3 = split2.split("#");
					list = (List) this.n.get(split3[0]);
					if (list == null) {
						list = new ArrayList();
						this.n.put(split3[0], list);
					}
					list.add(split3[1]);
				}
			}
			list = (List) this.n.get(appPackageName);//进入首页，手机卡慢　空间不足　
			if (list != null) {
				AccessibilityNodeInfo rootInActiveWindow = SettingService.getInstance().getRootInActiveWindow();
				if (rootInActiveWindow != null && appPackageName.equals(rootInActiveWindow.getPackageName()) && Action.findViewByLabel(rootInActiveWindow, list) != null) {
					this.task.skipActions();
					if(GlobalConstants.DEBUG) {
						Log.e("10", "由于安全软件一些干扰,跳过一些指令:" + list + "@" + appPackageName);
					}
				}
			}
		}
	}

	public static final ServiceConfigUtil getInstance() {
		return getInstance(null);
	}

	//调用该方法......
	public static final ServiceConfigUtil getInstance(Context context) {
		if (ServiceConfigUtil == null) {
			ServiceConfigUtil = new ServiceConfigUtil();
		}
		if (context != null) {
			ServiceConfigUtil.mContext = context;
			ServiceConfigUtil.sp = ServiceConfigUtil.mContext.getSharedPreferences(SHARED_KEY_TASK_STATUS, 0);
		}
		return ServiceConfigUtil;
	}

	protected final boolean startConfig() {

		if(GlobalConstants.DEBUG) {
			Log.e("10", "startConfig");
			Log.e("10", "startConfig");
			Log.e("10", "startConfig");
			Log.e("10", "startConfig");
		}

		boolean isCompleted;
		boolean isAccessibilityNotOpen = this.mContext == null || !SettingActivity.isOpenAccessibilityPage();

		if(this.task != null) {
			if(GlobalConstants.DEBUG) {
				Log.d("10", "任务:" + this.task.getTaskName() + " 是否完成:" + this.task.finished() + " 辅助是否打开：" + !isAccessibilityNotOpen);
			}
		}else{
			if(GlobalConstants.DEBUG) {
				Log.d("10", "还没找到任务，等待分配！！！" + " 辅助是否打开：" + !isAccessibilityNotOpen);
			}
		}

		if (isAccessibilityNotOpen || !(this.task == null || this.task.finished())) {//要么没有打开,或者任务还没有完成
			if (!(!isAccessibilityNotOpen || this.task == null || this.task.finished())) { //z2&&this.task!=null&&!this.task.finished();
				if(GlobalConstants.DEBUG) {
					Log.d("10", "任务还没有完成，重置任务列表");
				}
				this.task.resetActionStatus();
			}
			isCompleted = isAccessibilityNotOpen;
			if(GlobalConstants.DEBUG) {
				Log.d("10", "  hhhhhhh   ");
				Log.d("10", "  hhhhhhh   " + ((this.task != null) ? "任务名字:" + this.task.getTaskName() + " 任务是否完成:" + this.task.finished() : "任务还没有完成"));
				Log.d("10", "  hhhhhhh   ");
			}

		} else {//

			if(GlobalConstants.DEBUG) {
				Log.d("10", "　wwwwwww   ");
				Log.d("10", "　wwwwwww   " + ((this.task != null) ? "任务名字:" + this.task.getTaskName() + " 任务是否完成:" + this.task.finished() : "任务还没有完成"));
				Log.d("10", "　wwwwwww   ");
			}

			if (this.task != null && this.task.finished()) {
				if(GlobalConstants.DEBUG) {
					Log.d("10", "任务存在而且任务已经完成了！！！！！！！！");
				}
				if (!SettingActivity.d || "close_app".equals(this.task.getTaskName())) {
//					sp.readConfigFile(this.mContext, "Vlocker_Name_Rescue_Locker_PPC_TF", "rescue_name", this.task.getAppPackageName());
				} else {
//					sp.readConfigFile(this.mContext, "Vlocker_Success_Restart_Rescue_Locker_PPC_TF", "rescue_name", this.task.getTaskName());
				}
			}
//
			StringBuilder sb = new StringBuilder();
			for(int i = 0 ;i<this.taskList.size();i++){
				sb.append(((Task)taskList.get(i)).getTaskName()+" ");
			}
			if(GlobalConstants.DEBUG) {
				Log.d("10", "当前要任务是:" + sb.toString() + "当前的任务是:" + this.task);
			}

			if (needCleanTask()) {
				if(GlobalConstants.DEBUG) {
					Log.d("10", "需要做清理任务的操作");
				}
				this.task = this.cleanTask;
			} else {
				do {
					int i = this.currentTaskIndex + 1;
					this.currentTaskIndex = i;
					isCompleted = i >= this.taskList.size();
					if (isCompleted) {
						this.task = null;
					} else {
						this.task = (Task) this.taskList.get(this.currentTaskIndex);
					}
					if(this.task == null){
						isAccessibilityNotOpen = isCompleted;
						break;
					}
				} while (this.task.finished());
				isAccessibilityNotOpen = isCompleted;//z2 == false,表示任务还没完成,z2 == true,表示任务已经完成
				if(GlobalConstants.DEBUG) {
					Log.d("10", isAccessibilityNotOpen ? "当前任务已经完成" : "当前任务还没完成,正在做" + this.task.getTaskName());
				}
			}

			if(GlobalConstants.DEBUG) {
				Log.d("10", "当前是第" + this.currentTaskIndex + "个任务:" + this.task);
			}

			if (!(this.task == null || this.task.finished())) {//task !=null && !task.finished()
				if(GlobalConstants.DEBUG) {
					Log.d("10", "当前存在该任务，而且任务还没完成，");
				}
				this.task.resetActionStatus();
				this.task.startActivity(this.mContext);
				if ("package:com.android.settings".equals(this.task.getIntentData())) {
					if(GlobalConstants.DEBUG) {
						Log.d("10", "任务目标是设置界面");
					}
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if ("USAGE_ACCESS".equals(this.task.getTaskId()) || "SET_PURE_BACKGROUND_dido6.0".equals(this.task.getTaskId())) {
					if(GlobalConstants.DEBUG) {
						Log.d("10", "跳去最近使用列表");
					}
					try {
						Thread.sleep(1000);
						isCompleted = isAccessibilityNotOpen;
					} catch (Exception e2) {
						isCompleted = isAccessibilityNotOpen;
						e2.printStackTrace();
					}
				}
			}
			isCompleted = isAccessibilityNotOpen;
		}

		if(GlobalConstants.DEBUG) {
			Log.d("10", "...........　z ....." + isCompleted);
		}
		if (isCompleted) {
			if(GlobalConstants.DEBUG) {
				Log.d("10", "!!!!!!!!! 全部任务设置完成了．．．．．．．");
			}
            sendMessage(11,null);
		} else {
			if(GlobalConstants.DEBUG) {
				Log.d("10", "．．．．．．．．．．．．．任务还没完成，需要继续奋斗啊啊啊");
			}
//			preventErrorSituation();

			if(this.task != null) {
				if (this.task.execute() && ((!this.task.finished() || this.currentTaskIndex >= this.taskList.size() - 1 || ((Task) this.taskList.get(this.currentTaskIndex + 1)).isOnSetting()) && !"close_app".equals(this.task.getTaskName()))) {
					if(GlobalConstants.DEBUG) {
						Log.d("10", "sendMessage 3");
					}
					sendMessage(3, getProgress());
				}
			}
		}
		return isCompleted;
	}

	protected final boolean initConfig(Message message) {
		try {
			if(GlobalConstants.DEBUG) {
				Log.d("1", "display:" + Build.DISPLAY + " brand:" + Build.BRAND + " device:" + Build.DEVICE
						+ " manufacturer:" + Build.MANUFACTURER + " product:" + Build.PRODUCT + " model:" + Build.MODEL + " sdk:" + Build.VERSION.SDK_INT
						+ " versionName:");
				Log.d("1", "initConfig.1");
			}
			this.task = null;
			this.currentTaskIndex = -1;
			this.g = false;
			message.obj = this.taskList;
			ConfigParseUtil aVar = new ConfigParseUtil(this.mContext);
			if (this.romSet.isEmpty()) {
				aVar.readConfigFile("conf/rom.conf", (Object) this);
			}

			StringBuilder sb = new StringBuilder();
			Iterator<String> iterator = romSet.iterator();
			while(iterator.hasNext()){
				sb.append(iterator.next() + " ");
			}
			if(GlobalConstants.DEBUG) {
				Log.d("1", "initConfig.2" + sb.toString() + "rom的个数：" + romSet.size());
			}

			if ("360UI".equals(Build.BRAND)) {
				if(GlobalConstants.DEBUG) {
					Log.d("1", "360ui");
				}
				putBoolean("FLOAT_WIN", SettingActivity.h);
			} else if (hasRomReature("dido(6.0+)")) {//基于android深入定制
				if(GlobalConstants.DEBUG) {
					Log.d("1", "dido(6.0+)");
				}

			} else if (hasRomReature("miui_v5")) {
				if(GlobalConstants.DEBUG) {
					Log.d("1", "miui_v5");
				}
				putBoolean("FLOAT_WIN", SettingsUtil.isOpenForV5(this.mContext));
			} else if (Build.VERSION.SDK_INT >= 19 || hasRomReature("miui_v6") || hasRomReature("miui_v7")) {
				if(GlobalConstants.DEBUG) {
					Log.d("1", "miui_v6");
				}
				putBoolean("FLOAT_WIN", SettingsUtil.isFloatWindowOpen(this.mContext));
			}
			if ((Build.VERSION.SDK_INT >= 18 || hasRomReature("miui")) && !SettingActivity.d) {
				putBoolean(NativeParams.ACTION_NOTIFICATION_LISTENER_SETTINGS, SettingsUtil.CheckNotifiServiceValid(this.mContext));

			}
			if (Build.VERSION.SDK_INT > 20) {
				putBoolean(NativeParams.ACTION_USAGE_ACCESS, UsageStatsUtil.isPackageUsingBeforeOneHour(this.mContext));

			}
			aVar.readConfigFile("conf/set.conf", (Object) this);

            sb = new StringBuilder();
			for(int i = 0;i<taskList.size();i++){
                sb.append(((Task)taskList.get(i)).getTaskName()+" ");
			}

			if(GlobalConstants.DEBUG) {
				Log.d("1", "initconfig.3:" + sb.toString());
			}

			if (!this.taskList.isEmpty()) {
				sendMessage(message.what, message.obj);
				PackageManager packageManager = this.mContext.getPackageManager();
				int i = 0;
				while (i < this.taskList.size()) {
					Task taskVar = (Task) this.taskList.get(i);
					if (!(taskVar.getAppPackageName() == null || SettingsUtil.isPackageNameUninstalled(packageManager, taskVar.getAppPackageName()))) {
						this.taskSet.remove(taskVar.getTaskName());
						this.taskList.remove(taskVar);
						setTaskStatus(taskVar, false);
						i--;
					}
					boolean isFinished = containTaskStatus(getTaskStatusTag(taskVar));
					taskVar.setFinished(isFinished);
					if(isFinished) {
						if(GlobalConstants.DEBUG) {
							Log.d("10", "在初始化配置文件时候，发现任务已经完成：" + taskVar.getTaskName());
						}
					}
					i++;
				}
			}
			if (this.cleanTask != null) {
				this.cleanTask.resetActionStatus();
			}

			calculateAllTime();
			if(GlobalConstants.DEBUG) {
				Log.d("10", "预计花费时间为：" + this.allTime);
			}
//			com.luna.screenlocker.db.a.a(this.mContext).f(!hasNewTask());//保存一键设置是否完成
			sendMessage(7, this.taskList);
			return true;
		}catch (Throwable e){
			e.printStackTrace();
		}
		return  false;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "roms")
	public final void addRomFeature(Rom romVar) {
		boolean isMatched = romVar.matched();
		if (isMatched) {
			String[] romNames = romVar.getRomNames();
			for (int i = 0; i < romNames.length; i++) {
				this.romSet.add(romNames[i]);
				if(DEBUG){
					if(GlobalConstants.DEBUG) {
						Log.d(TAG, new StringBuilder("addRomFeature mRomTitle=").append(romNames[i]).toString());
					}
				}
			}
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "action")
	public final void addSettingAction(Action actionVar) {
		this.actionMap.put(actionVar.getActionId(), actionVar);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "task")
	public final void addSettingTask(Task taskVar) {
		PackageManager packageManager = this.mContext.getPackageManager();

		if(GlobalConstants.DEBUG) {
			Log.d("11", "add task:" + taskVar.getTaskName());
		}

		if (!this.taskSet.contains(taskVar.getTaskName()) && taskVar.validateRom(this.romSet) && taskVar.validateIntent(packageManager) && taskVar.validateVersion(packageManager)) {
			this.taskSet.add(taskVar.getTaskName());
			this.taskList.add(taskVar);
			sendMessage(0, this.taskList);
			if(GlobalConstants.DEBUG) {
				Log.d("10", "添加的任务有：" + taskVar.getTaskName());
			}
		}
	}

	protected final boolean clearConfig() {
		this.actionMap.clear();
		this.taskList.clear();
		this.taskSet.clear();
		this.task = null;
		return true;
	}

	public final void cleanEmptyTask() {
		Collection arrayList = new ArrayList();
		for (int i = 0; i < this.taskList.size(); i++) {
			if ("empty".equals(((Task) this.taskList.get(i)).getTaskName())) {
				arrayList.add((Task) this.taskList.get(i));
			}
		}
		this.taskList.removeAll(arrayList);
	}

	public final boolean finished() {
		boolean z = true;
		int i = 0;
		while (z && i < this.taskList.size()) {
			boolean finished = ((Task) this.taskList.get(i)).finished();
			i++;
			z = finished;
		}
		return z;
	}

	public final SettingActivity getApplicationContext() {
		return (SettingActivity) this.mContext;
	}

	public final Context getContext() {
		return this.mContext;
	}

	public final Float[] getProgress() {
		Float[] fArr = new Float[]{Float.valueOf(0.0f), Float.valueOf(1.0f)};
		if (this.allTime > 0 && this.currentTaskIndex >= 0 && this.currentTaskIndex < this.taskList.size()) {
			int i;
			Task taskVar;
			int nextActionTimeout;
			int i2 = 0;
			for (i = this.currentTaskIndex; i < this.taskList.size(); i++) {
				taskVar = (Task) this.taskList.get(i);
				if (!taskVar.finished()) {
					i2 += taskVar.getEstimatedTimeOfCompletion();
				}
			}
			taskVar = (Task) this.taskList.get(this.currentTaskIndex);
			if (this.currentTaskIndex + 1 == this.taskList.size() && "package:com.android.settings".equals(taskVar
					.getIntentData())) {
				i = i2 + this.cleanTask.getEstimatedTimeOfCompletion();
				nextActionTimeout = this.cleanTask.getNextActionTimeout();
			} else {
				i = this.f + i2;
				nextActionTimeout = taskVar.getNextActionTimeout();
			}
			fArr[0] = Float.valueOf(((float) (this.allTime - i)) / ((float) this.allTime));
			fArr[1] = Float.valueOf(((float) (nextActionTimeout + (this.allTime - i))) / ((float) this.allTime));
		} else if (finished()) {
			fArr[0] = Float.valueOf(1.0f);
			fArr[1] = Float.valueOf(1.0f);
		}
		return fArr;
	}

	public final Action getSettingAction(String str) {
		return (Action) this.actionMap.get(str);
	}

	public final List getSettingTasks() {
		return this.taskList;
	}

	public final int getTotalTime() {
		if (this.allTime == -1) {
			calculateAllTime();
		}
		return this.allTime;
	}

	public final int getUnfinishedTaskCount() {
		int i = 0;
		for (int i2 = 0; i2 < this.taskList.size(); i2++) {
			if (!((Task) this.taskList.get(i2)).finished()) {
				i++;
			}
		}
		return i;
	}

	public final boolean hasNewTask() {
		int i = 0;
		boolean z = false;
		while (!z && i < this.taskList.size()) {
			z = !isTaskSetted((Task) this.taskList.get(i));
			i++;
		}
		return z;
	}

	public final boolean hasRomReature(String str) {
		return this.romSet.contains(str);
	}

	public final boolean isEmpty() {
		getClass().getSimpleName();
		new StringBuilder().append(this.taskList.size());
		return this.taskList.isEmpty();
	}

	public final boolean isTaskSetted(Task taskVar) {
		return this.sp.contains(getTaskStatusTag(taskVar));
	}

	public final boolean needCleanTask() {
		if(GlobalConstants.DEBUG) {
			Log.d("10", "needCleanTask()　之前是否完成了任务．．．" + finished());
		}
		boolean z = true;
		if (this.task == null) {
			return false;
		}
		String appPackageName = this.task.getAppPackageName();
		if (appPackageName != null && !appPackageName.isEmpty() && !appPackageName.equals("com.android.settings")) {
			if(this.cleanTask != null) {
				this.cleanTask.setIntentData("package:" + this.task.getAppPackageName());
			}
		} else if (!finished() || this.g) {//该有的任务还没有完成
			z = false;
		} else {
			if(this.cleanTask != null) {
				this.cleanTask.setIntentData("package:com.android.settings");
			}
			this.g = true;
		}
		if (this.cleanTask == null) {
			return z;
		}
		if(this.cleanTask != null) {
			this.cleanTask.resetActionStatus();
		}
		return false;
	}

	public final void resetFinished() {
		int i = 0;
		while (i < this.taskList.size()) {
			if (((Task) this.taskList.get(i)).getmFollowUp() == null || !((Task) this.taskList.get(i)).getmFollowUp().contains("open")) {
				setTaskStatus((Task) this.taskList.get(i), false);
//				sp.readConfigFile(this.mContext, "Vlocker_Restart_Rescue_Locker_PPC_TF", "rescue_name", ((mContext) this.taskList.get(taskSet)).getTaskName());
			} else if ("开启消息功能".equals(((Task) this.taskList.get(i)).getTaskName())) {
				putBoolean(NativeParams.ACTION_NOTIFICATION_LISTENER_SETTINGS, false);
//				sp.readConfigFile(this.mContext, "Vlocker_Restart_Rescue_Locker_PPC_TF", "rescue_name", ((mContext) this.taskList.get(taskSet)).getTaskName());
			}
			if (SettingActivity.d && this.taskList.get(i) != null) {
				boolean z;
				String taskId = ((Task) this.taskList.get(i)).getTaskId();
				int i2 = 0;
				while (i2 < this.o.length) {
					if (this.o[i2].equals(taskId)) {
						z = "FLOAT_WIN_VIVO".equals(taskId) && !SettingsUtil.isFloatWindowOpen(this.mContext);
						if (!z) {
							setTaskStatus((Task) this.taskList.get(i), true);
						}
					} else {
						i2++;
					}
				}
				z = true;
				if (!z) {
					setTaskStatus((Task) this.taskList.get(i), true);
				}
			}
			i++;
		}
	}

	public final void sendMessage(int i, Object obj) {
		if (this.mContext instanceof SettingActivity) {
			((SettingActivity) this.mContext).sendMessage(i, obj);
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "clean_task")
	public final void setCleanTask(Task taskVar) {
		if(GlobalConstants.DEBUG) {
			Log.d("1", "setCleanTask");
		}
		this.cleanTask = taskVar;
	}

	public final void setTaskStatus(Task taskVar, boolean z) {
		putBoolean(getTaskStatusTag(taskVar), z);
	}

	public int getCurrentTaskIndex() {
		return currentTaskIndex;
	}

	public SharedPreferences getSp() {
		return sp;
	}
}