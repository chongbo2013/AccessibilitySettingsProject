package com.zyq.accessibility.setting.a.temp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;


import com.zyq.accessibility.AccessibilityApplication;
import com.zyq.accessibility.activity.GlobalConstants;
import com.zyq.accessibility.activity.SettingActivity;
import com.zyq.accessibility.setting.SettingService;
import com.zyq.accessibility.setting.a.ServiceConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyq 16-5-17
 */
@TargetApi(value=16) public class Action {

	private static final boolean DEBUG = true;
	private static final String TAG = "Action";
	private String extraStatus = "";//状态:类似允许,开启
	private String actionId = null; //id;
	private String alertMessage = null;//表示要显示的内容.....
	private boolean mustSuccess = true;//表示是否必须成功
	private int timeOut = 10000;//表示超时时间
	private long f = -1;//辅助判断是否超时
	private long g = -1;//辅助判断是否超时
	private int h = 0;//表示查找class的次数
	private Integer index = null;//表示第几个,一般是第一个
	private int sleepTime = -1;//表示睡眠时间
	//一般是android.view.view,android.widget.ListView,android.widget.imageView集合
	private List classList = null;
	private int l = 1;
	//锁屏,显示悬浮窗,无限制,允许定位,["应用程序管理","应用程序"],["微锁屏", "VLocker", "微鎖屏"],
	//添加到白名单,清理加速保护名单,加入忽略名单,清理加速,手机清理,["手机瘦身", "手机管家"],
	//["手机加速", "内存不足"],["受保护应用","应用白名单"],手机加速,强力手机加速,可安全清理的进程
	//需谨慎清理的进程,软件管理,内存加速....
	private List labelList = null;
	private List clickItemList = null;
	private int status = -1;//表示状态
	private boolean scrollable = false;//表示是否可滚动
	private int perform = -1;//表示执行什么动作: click,show,refresh,nothing,back
	private boolean isFinished = false;


	private final AccessibilityNodeInfo a(AccessibilityNodeInfo accessibilityNodeInfo, List list, int i) {
		AccessibilityNodeInfo accessibilityNodeInfo2 = null;
		if (accessibilityNodeInfo != null) {
			int i2;
			AccessibilityNodeInfo b;
			if (i > 0) {
				i2 = 0;
				while (accessibilityNodeInfo2 == null && i2 < accessibilityNodeInfo.getChildCount()) {
					b = b(accessibilityNodeInfo.getChild(i2), list, i);
					i2++;
					accessibilityNodeInfo2 = b;
				}
			} else {
				i2 = accessibilityNodeInfo.getChildCount() - 1;
				while (accessibilityNodeInfo2 == null && i2 >= 0) {
					b = b(accessibilityNodeInfo.getChild(i2), list, i);
					i2--;
					accessibilityNodeInfo2 = b;
				}
			}
		}
		return accessibilityNodeInfo2;
	}

	private final boolean a(AccessibilityNodeInfo accessibilityNodeInfo, boolean z) {
		if (accessibilityNodeInfo == null) {
			return false;
		}
		switch (this.status) {
			case 1: //checked
				return accessibilityNodeInfo.isChecked();
			case 2://unchecked
				return !accessibilityNodeInfo.isChecked();
			case 3:
				return !accessibilityNodeInfo.isEnabled();
			case 4:
				return true;
			case 7:
				int i = 0;
				while (i < accessibilityNodeInfo.getParent().getChildCount()) {
					if (accessibilityNodeInfo.getParent().getChild(i) != null && accessibilityNodeInfo.getParent().getChild(i).getText() != null && this.extraStatus.equals(accessibilityNodeInfo.getParent().getChild(i).getText().toString())) {
						return true;
					}
					i++;
				}
				return false;
			default:
				return false;
		}
	}


	private final AccessibilityNodeInfo b(AccessibilityNodeInfo arg3, List arg4, int arg5) {
		AccessibilityNodeInfo v0 = null;
		if(arg3 == null) {
			arg3 = v0;
		}

		if(arg4.contains(arg3.getClassName())) {
			if(GlobalConstants.DEBUG) {
				Log.d("10", "在 " + arg4 + " 找到了" + arg3.getClassName());
			}
			if(Math.abs(arg5) != 1) {
				int v0_1 = this.h + 1;
				this.h = v0_1;
				if(v0_1 != Math.abs(arg5)) {
					return this.a(arg3, arg4, arg5);
				}
			}
		}
		else {
			return a(arg3,arg4,arg5);
		}

		return arg3;
	}

	public static final AccessibilityNodeInfo findViewByLabel(AccessibilityNodeInfo accessibilityNodeInfo, List list) {
		AccessibilityNodeInfo accessibilityNodeInfo2 = null;
		int i = 0;
		while (accessibilityNodeInfo2 == null && i < accessibilityNodeInfo.getChildCount()) {
			AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);
			if (child == null) {
				child = accessibilityNodeInfo2;
			} else if (child.getText() == null || !list.contains(child.getText().toString())) {
				child = findViewByLabel(child, list);
			} else {
				if(GlobalConstants.DEBUG) {
					Log.d("10", "通过标签找到了该字段：" + child.getText() + "@" + child.getClassName());
				}
			}
			i++;
			accessibilityNodeInfo2 = child;
		}
		return accessibilityNodeInfo2;
	}

	public static final String getStatusString(int i) {
		switch (i & 255) {
			case 0:
				return "unknow";
			case 1:
				return "setted";
			case 2:
				return "sucess";
			case 16:
				return "timeout";
			case 32:
				return "bottom";
			default:
				return "unknow";
		}
	}

	public static final boolean isActionBreak(int i) {
		return (i & 256) != 0;
	}

	public static final boolean isActionFinished(int i) {
		return i != 0;
	}

	public static final boolean isSuccessed(int i) {
		return (i & 15) != 0;
	}

	public static final boolean isTaskFinished(int i) {
		return (i & 256) != 0 || i == 1;
	}

	public final int autoStart_Vivo(AccessibilityNodeInfo accessibilityNodeInfo) {
		if (accessibilityNodeInfo == null) {
			perform(accessibilityNodeInfo);
			return 2;
		} else if (a(accessibilityNodeInfo, false)) {
			perform(accessibilityNodeInfo);
			return 2;
		} else if (!isTimeout()) {
			return 0;
		} else {
			int i = this.mustSuccess ? 272 : 16;
			if (this.perform != 3) {//diabled
				return i;
			}
			perform(accessibilityNodeInfo);
			return i;
		}
	}

	public final int execute(String str) {
		int i = 288;
		AccessibilityNodeInfo rootInActiveWindow = SettingService.getInstance().getRootInActiveWindow();
		if ("refresh_oppov2.1".equals(this.actionId) || "refresh_yunos".equals(this.actionId)) {
			return manual_setting(rootInActiveWindow);
		}
		AccessibilityNodeInfo accessibilityNodeInfo;
		int i2;
		if (rootInActiveWindow == null || !(str == null || str.equals(rootInActiveWindow.getPackageName()))) {
			if(GlobalConstants.DEBUG) {
				Log.d("14", "当前指令是：" + this.actionId + rootInActiveWindow == null ? "窗口是空" : "包名不匹配");
			}
			accessibilityNodeInfo = rootInActiveWindow;
			i2 = 0;
		} else {
			AccessibilityNodeInfo findViewByViewClass = findViewByViewClass(rootInActiveWindow);
			AccessibilityNodeInfo findViewByClickItemClass = findViewByClickItemClass(findViewByIndex(findViewByLabel(findViewByViewClass)));

			if(GlobalConstants.DEBUG) {
				Log.d("14", "终于找到了，" + findViewByClickItemClass);
			}

			if ("refresh_vlocker_vivo2".equals(this.actionId) || "refresh_vlocker_vivo".equals(this.actionId)) {
				return autoStart_Vivo(findViewByClickItemClass);
			}
			if (findViewByClickItemClass != null) {
				if ("find_vlocker_nothing".equals(this.actionId) && AccessibilityApplication.getInstance() != null) {
					Rect rect = new Rect();
					i2 = (((WindowManager) AccessibilityApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getHeight();
					findViewByClickItemClass.getBoundsInScreen(rect);
					if (rect.bottom > i2 && scroll(findViewByViewClass)) {
						return this.mustSuccess ? 288 : 32;
					}
				}
				if (this.status == 6) {
					if (findViewByClickItemClass.isChecked()) {
						this.status = 2;
					}
					if (isTimeout()) {
						this.status = 0;
					}
					i = 0;
				} else if (a(findViewByClickItemClass, false)) {
					i = ("click_vlocker_list_uncheck_switch_for_notify".equals(this.actionId) || "click_vlocker_check_close".equals(this.actionId)) ? 2 : 1;
				} else if (perform(findViewByClickItemClass)) {
					i = 2;
				} else{
					i = 0;//表示还没完成
				}
			} else {
				if (scroll(findViewByViewClass)) {//滚动到底部并且停留２秒钟
					if (!this.mustSuccess) {
						i = 32;
					}
				}else{
					i = 0;
				}
			}
			i2 = i;
			accessibilityNodeInfo = findViewByClickItemClass;
		}
		if (i2 == 0 && isTimeout()) {
			i2 = this.mustSuccess ? 272 : 16;
			if (this.perform == 3) {
				perform(accessibilityNodeInfo);
			}
			if(GlobalConstants.DEBUG) {
				Log.d("14", "超时了");
			}
		}
		if (!"refresh_vlocker_vivo2".equals(this.actionId) && !"refresh_vlocker_vivo".equals(this.actionId)) {
			return i2;
		}
		if (this.perform == 3) {
			perform(accessibilityNodeInfo);
		}
		return  2;
	}

	public int manual_setting(AccessibilityNodeInfo accessibilityNodeInfo) {
		if (!SettingActivity.h) {
			SettingActivity.getInstance().sendMessage(9);
			if (!(this.isFinished || TextUtils.isEmpty(this.alertMessage))) {
				this.isFinished = true;
				SettingActivity.getInstance().sendMessage(10, this.alertMessage);
			}
		}
		if (SettingActivity.h) {
			if (this.isFinished) {
				perform(accessibilityNodeInfo);
			}
			return 2;
		} else if (!isTimeout()) {
			return 0;
		} else {
			int i = this.mustSuccess ? 272 : 16;
			if (this.status != 3) {
				return i;
			}
			perform(accessibilityNodeInfo);
			return i;
		}
	}

	public final AccessibilityNodeInfo findViewByClickItemClass(AccessibilityNodeInfo accessibilityNodeInfo) {
		if (accessibilityNodeInfo == null || this.index != null || this.clickItemList == null) {
			return accessibilityNodeInfo;
		}
		if (accessibilityNodeInfo.getParent() != null) {
			accessibilityNodeInfo = accessibilityNodeInfo.getParent();
		}
		return a(accessibilityNodeInfo, this.clickItemList, 1);
	}

	public AccessibilityNodeInfo findViewByIndex(AccessibilityNodeInfo accessibilityNodeInfo) {
		AccessibilityNodeInfo accessibilityNodeInfo2 = null;
		if (accessibilityNodeInfo == null || this.index == null) {
			return accessibilityNodeInfo;
		}
		if (accessibilityNodeInfo.getChildCount() == 0) {
			accessibilityNodeInfo = accessibilityNodeInfo.getParent();
		}
		int intValue = this.index.intValue();
		if (this.index.intValue() < 0) {
			intValue = accessibilityNodeInfo.getChildCount() + this.index.intValue();
		}
		if (accessibilityNodeInfo == null || intValue < 0 || intValue >= accessibilityNodeInfo.getChildCount()) {
			return null;
		}
		AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(intValue);
		if (this.clickItemList == null || child == null || this.clickItemList.contains(child.getClassName())) {
			accessibilityNodeInfo2 = child;
		}
		if(GlobalConstants.DEBUG) {
			Log.d("10", "通过index找到了" + this.actionId);
		}
		return accessibilityNodeInfo2;
	}


	public final AccessibilityNodeInfo findViewByLabel(AccessibilityNodeInfo accessibilityNodeInfo) {
		return (accessibilityNodeInfo == null || this.labelList == null) ? accessibilityNodeInfo : findViewByLabel(accessibilityNodeInfo, this.labelList);
	}

	public final AccessibilityNodeInfo findViewByViewClass(AccessibilityNodeInfo accessibilityNodeInfo) {
		if (accessibilityNodeInfo == null || this.classList == null) {
			return accessibilityNodeInfo;
		}
		this.h = 0;
		return a(accessibilityNodeInfo, this.classList, this.l);
	}

	public String getActionId() {
		return this.actionId;
	}

	public int getTime() {
		return (int) ((SystemClock.uptimeMillis() - this.f) / 1000);
	}

	public int getTimeout() {
		return this.timeOut;
	}

	public boolean isTimeout() {
		return this.timeOut > 0 && this.f != -1 && SystemClock.uptimeMillis() > this.f + ((long) this.timeOut);
	}

	@TargetApi(16)
	public final boolean perform(AccessibilityNodeInfo accessibilityNodeInfo) {
		Rect rect = new Rect();

		switch (this.perform) {
			case 1://点击
				if(GlobalConstants.DEBUG) {
					Log.d("14", "终于点击了:" + accessibilityNodeInfo + " 是否可点击：" + accessibilityNodeInfo.isClickable());
				}
				if (!(accessibilityNodeInfo == null || accessibilityNodeInfo.isClickable())) {
					accessibilityNodeInfo = accessibilityNodeInfo.getParent();
				}
				if (accessibilityNodeInfo == null || !accessibilityNodeInfo.isClickable()) {
					return false;
				}
				boolean performAction = accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				if(GlobalConstants.DEBUG) {
					Log.d("10", "是否执行点击命令：" + performAction);
				}
				if (performAction || this.alertMessage == null || this.isFinished) {
					return performAction;
				}
				//
				this.isFinished = true;
				SettingActivity.getInstance().sendMessage(6,this.alertMessage);
				return performAction;
			case 2://展示
				if(GlobalConstants.DEBUG) {
					Log.d("14", "终于要展示了");
				}
				if (accessibilityNodeInfo == null) {
					return false;
				}
				accessibilityNodeInfo.getBoundsInScreen(rect);
				ServiceConfigUtil.getInstance().sendMessage(2,rect);
				return true;
			case 3://刷新
				if(GlobalConstants.DEBUG) {
					Log.d("14", "刷新");
				}
				ServiceConfigUtil.getInstance().sendMessage(1,null);
				return true;
			case 4://选中
				if(GlobalConstants.DEBUG) {
					Log.d("14", "选中");
				}
				if (!(accessibilityNodeInfo == null || accessibilityNodeInfo.isChecked())) {
					accessibilityNodeInfo = accessibilityNodeInfo.getParent();
				}
				return accessibilityNodeInfo != null ? accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK) : false;
			case 5:
				return true;
			case 6:
				if(GlobalConstants.DEBUG) {
					Log.d("14", "后退");
					Log.d("10", "back,back,back");
				}
				SettingService.getInstance().back();
				return true;
			default:
				return false;
		}
	}

	public void resetTime() {
		this.f = SystemClock.uptimeMillis();
		this.g = -1;
		this.isFinished = false;
	}

	public boolean scroll(AccessibilityNodeInfo accessibilityNodeInfo) {
		if(GlobalConstants.DEBUG) {
			Log.d("10", "scroll");
		}
		boolean z = false;
		if (this.scrollable && accessibilityNodeInfo != null) {
			if (accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
				this.g = SystemClock.uptimeMillis();
			} else if (this.g == -1) {
				this.g = SystemClock.uptimeMillis();
			} else {
				if (SystemClock.uptimeMillis() > this.g + 2000) {
					z = true;
				}
				if(GlobalConstants.DEBUG) {
					Log.d("10", "scroll" + accessibilityNodeInfo.getClassName() + "@" + this.actionId);
				}
			}
		}
		return z;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "id")
	public void setActionId(String str) {
		this.actionId = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "alert")
	public void setAlertMessage(String str) {
		this.alertMessage = str;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "class_times")
	public void setClassFoundTimes(int i) {
		this.l = i;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "click")
	public void setClickClassName(String str) {
		if (this.clickItemList == null) {
			this.clickItemList = new ArrayList();
		}
		this.clickItemList.add(str);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "index")
	public void setItemIndex(int i) {
		this.index = Integer.valueOf(i);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "label")
	public void setItemLabel(String str) {
		if (this.labelList == null) {
			this.labelList = new ArrayList();
		}
		this.labelList.add(str);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "status")
	public void setItemStatus(String str) {
		if (str != null && !str.isEmpty()) {
			if ("checked".equals(str)) {
				this.status = 1;
			} else if ("unchecked".equals(str)) {
				this.status = 2;
			} else if ("disable".equals(str)) {
				this.status = 3;
			} else if ("exist".equals(str)) {
				this.status = 4;
			} else if ("notexist".equals(str)) {
				this.status = 5;
			} else if ("checkselected".equals(str)) {//检查是否选择
				this.status = 6;
			} else {
				this.status = 7;
				this.extraStatus = str;//有些动作是允许,开启
			}
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "must")
	public void setMustSuccess(boolean z) {
		this.mustSuccess = z;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "perform")
	public void setPerformActionId(String str) {
		if ("click".equals(str)) {
			this.perform = 1;
		} else if ("show".equals(str)) {
			this.perform = 2;
		} else if ("refresh".equals(str)) {
			this.perform = 3;
		} else if ("check".equals(str)) {
			this.perform = 4;
		} else if ("nothing".equals(str)) {
			this.perform = 5;
		} else if ("back".equals(str)) {
			this.perform = 6;
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "scroll")
	public void setScrollable(boolean z) {
		this.scrollable = z;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "sleep")
	public void setSleepTime(int i) {
		this.sleepTime = i;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "timeout")
	public void setTimeout(int i) {
		this.timeOut = i;
	}

	@com.zyq.accessibility.setting.common.a.b(a = "class")
	public void setViewClassName(String str) {
		if (this.classList == null) {
			this.classList = new ArrayList();
		}
		this.classList.add(str);
	}

	public final void sleep() {
		if (this.sleepTime > 0) {
			try {
				Thread.sleep((long) this.sleepTime);
			} catch (InterruptedException e) {
			}
		}
	}
}
