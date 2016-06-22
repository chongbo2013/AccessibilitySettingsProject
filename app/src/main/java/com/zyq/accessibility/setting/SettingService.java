package com.zyq.accessibility.setting;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zyq.accessibility.R;
import com.zyq.accessibility.activity.SettingActivity;
import com.zyq.accessibility.setting.a.ServiceConfigUtil;
import com.zyq.accessibility.setting.a.SettingsUtil;
import com.zyq.accessibility.setting.ui.WindowViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zyq 16-5-17
 */


@TargetApi(value=16) public final class SettingService extends AccessibilityService {  // //开启辅助工具

	private static boolean DEBUG = true;
	private static final String TAG = "settingService";


	private static Map a = null;
	private static Map b = null;
	private static SettingService instance = null;

	public SettingService() {
		instance = this;
	}

	private final AccessibilityNodeInfo a(AccessibilityNodeInfo accessibilityNodeInfo, List list) {
		AccessibilityNodeInfo accessibilityNodeInfo2 = null;
		int i = 0;
		while (accessibilityNodeInfo2 == null && i < accessibilityNodeInfo.getChildCount()) {
			AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i);
			if (child == null) {
				child = accessibilityNodeInfo2;
			} else if (child.getText() == null || !list.contains(child.getText().toString())) {
				child = a(child, list);
			} else {
                Log.d("10","settingService: 选中的名字是"+child.getText().toString());
			}
			i++;
			accessibilityNodeInfo2 = child;
		}
		return accessibilityNodeInfo2;
	}

	public static final SettingService getInstance() {
		return instance;
	}

	public static final String a(AccessibilityNodeInfo accessibilityNodeInfo) {
		return accessibilityNodeInfo != null ? new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(accessibilityNodeInfo.getPackageName().toString())).append("1").append(b(accessibilityNodeInfo)).append(c(accessibilityNodeInfo)).toString())).append(a(accessibilityNodeInfo, 2)).toString() : null;
	}

	private static final String a(AccessibilityNodeInfo accessibilityNodeInfo, int i) {
		String str = "";
		int i2 = 0;
		while (accessibilityNodeInfo != null && i2 < accessibilityNodeInfo.getChildCount()) {
			AccessibilityNodeInfo child = accessibilityNodeInfo.getChild(i2);
			if (child != null) {
				str = new StringBuilder(String.valueOf(str)).append(i).append(b(child)).append(c(child)).toString();
				if (child.getChildCount() > 0) {
					str = new StringBuilder(String.valueOf(str)).append(a(child, i + 1)).toString();
				}
			}
			i2++;
		}
		return str;
	}

	public static final boolean a(Context context) {
		boolean z = Build.VERSION.SDK_INT >= 16;
		if (!z) {
			return z;
		}
		return context.getPackageManager().queryIntentActivities(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}

	private static final boolean a(Context context, Intent intent) {
		try {
			intent.addFlags(268468224);
			context.startActivity(intent);
			return true;
		} catch (Exception e) {
			boolean result1 = SettingsUtil.hasClassInApk(context, "com.android.settings", "com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment");
			boolean result2 = SettingsUtil.hasClassInApk(context, "com.android.settings", "com.android.settings.AccessibilitySettings");
			if(DEBUG){
				Log.d(TAG,"result1:"+result1 + "  result2:"+result2);
			}
			return false;
		}
	}

	private static final String b(AccessibilityNodeInfo accessibilityNodeInfo) {
		String str = "";
		if (a == null) {
			a = new HashMap();
			String[] split = "Button#B,CheckBox#C,CompoundButton#instance,CheckedTextView#CT,FrameLayout#F,ImageView#I,ImageButton#i,LinearLayout#L,ListView#l,RelativeLayout#R,ScrollView#S,Switch#s,TextView#T,ToggleButton#t,View#V,ViewPager#v".split(",");
			for (String split2 : split) {
				String[] split3 = split2.split("#");
				a.put(split3[0], split3[1]);
			}
		}
		if (accessibilityNodeInfo.getClassName() == null) {
			return str;
		}
		String charSequence = accessibilityNodeInfo.getClassName().toString();
		int lastIndexOf = charSequence.lastIndexOf(".");
		charSequence = lastIndexOf != -1 ? charSequence.substring(lastIndexOf + 1, charSequence.length()) : str;
		return a.containsKey(charSequence) ? (String) a.get(charSequence) : charSequence;
	}

	public static final boolean isAccessibilityEnabled(Context context) {
		try {
			int i;
			boolean z;
			String str = context.getPackageName() + "/" + SettingService.class.getName();
			try {
				i = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), "accessibility_enabled");

			} catch (Exception e) {
				SettingService.class.getSimpleName();
				i = 0;
			}
			TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
			if (i == 1) {
				String string = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), "enabled_accessibility_services");
				if (string != null) {
					simpleStringSplitter.setString(string);
					z = false;
					while (!z && simpleStringSplitter.hasNext()) {

						z = str.equalsIgnoreCase(simpleStringSplitter.next());
					}
					return z && instance != null;
				}
			}
			z = false;
			if (z) {
				return false;
			}
		}catch (Throwable e){
			e.printStackTrace();
		}
		return false;
	}

	private static final String c(AccessibilityNodeInfo accessibilityNodeInfo) {
		return accessibilityNodeInfo.getText() != null ? accessibilityNodeInfo.getText().toString() : "";
	}

	public static void promptBringToAccessibilitySetting(Context context) {
		try {
//		m.guideToAccessibilitySetting(context, "Vlocker_Open_Rescue_Service_PPC_TF", new String[0]);
			ServiceConfigUtil instance = ServiceConfigUtil.getInstance();
			if (instance == null || !(instance.hasRomReature("miui") || instance.hasRomReature("amigo2"))) {
				bringToAccessibilitySetting(context);
				return;
			}
			Intent intent = new Intent("android.intent.action.MAIN");
			String packageName = context.getPackageName();
			intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
			intent.putExtra(":android:show_fragment_short_title", 0);
			intent.putExtra(":android:show_fragment_args", 0);
			intent.putExtra(":android:show_fragment_title", 0);
			intent.putExtra(":android:no_headers", true);
			intent.putExtra("setting:ui_options", 1);
			Bundle bundle = new Bundle();
			bundle.putString("summary", context.getString(R.string.setting_service_description));
			bundle.putString("title", context.getString(R.string.setting_service_name));
			bundle.putString("preference_key", new StringBuilder(String.valueOf(packageName)).append("/").append(SettingService.class.getName()).toString());

			bundle.putParcelable("component_name", new ComponentName(packageName, SettingService.class.getName()));
			bundle.putBoolean("checked", false);
			String str = "com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment";
			if (Build.VERSION.SDK_INT < 19 || !SettingsUtil.hasClassInApk(context, "com.android.settings", str)) {
				bundle.putString("disable_warning_message", "触摸“确定”会停用【MIGO 锁屏】");
				bundle.putString("enable_warning_message", "【MIGO 锁屏】会收集您键入的所有文字信息（密码除外），其中包括信用卡号等个人数据。它还会收集关于您与手机互动情况的数据。");
				bundle.putString("disable_warning_title", "要停用【MIGO 锁屏】吗？");
				bundle.putString("disable_warning_message", "要停用【MIGO 锁屏】吗？");
				bundle.putString("enable_warning_title", "要使用【MIGO 锁屏】吗？");
				bundle.putString("service_component_name", new StringBuilder(String.valueOf(packageName)).append("/").append(SettingService.class.getName()).toString());
				intent.putExtra(":android:show_fragment", "com.android.settings.AccessibilitySettings$ToggleAccessibilityServicePreferenceFragment");
			} else {
				intent.putExtra(":android:show_fragment", str);
			}
			intent.putExtra(":android:show_fragment_args", bundle);
			intent.addFlags(268468224);
			if (!a(context, intent)) {
				bringToAccessibilitySetting(context);
			}
		}catch (Throwable e){
			e.printStackTrace();
		}
	}

	public static boolean d(Context context) {
		try {
			int i;
			String str = context.getPackageName() + "/" + SettingService.class.getName();
			try {
				i = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), "accessibility_enabled");

			} catch (Settings.SettingNotFoundException e) {
				i = 0;
			}
			TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
			if (i != 1) {
				return false;
			}
			String string = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), "enabled_accessibility_services");
			if (string == null) {
				return false;
			}
			simpleStringSplitter.setString(string);
			while (simpleStringSplitter.hasNext()) {
				if (simpleStringSplitter.next().equalsIgnoreCase(str)) {
					return true;
				}
			}
		}catch (Throwable e){
			return  false;
		}
		return false;
	}


	private static final boolean bringToAccessibilitySetting(Context context) {
		WindowViewManager.guideToAccessibilitySetting(context);
		return a(context, new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
	}

	public final void back() {
		SettingActivity settingActivity = SettingActivity.getInstance();
		if (settingActivity != null) {
			performGlobalAction(1);
			settingActivity.backActivity(this);
//			m.getInstance((Context) this, "Vlocker_Success_Rescue_Service_PPC_TF", "status", "Open");
//			getClass().getSimpleName();
		}
	}

	public final void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
		try {
			if (SettingActivity.isOpenAccessibilityPage() && accessibilityEvent != null && !accessibilityEvent.isFullScreen()) {
				AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
				if (rootInActiveWindow != null && rootInActiveWindow.equals(accessibilityEvent.getSource())) {
					List list;
					if (b == null) {
						b = new HashMap();
						String[] split = "android.settings.ACCESSIBILITY_SETTINGS#Migo锁屏#BACK,android#权限请求#BACK,android#无法访问短信#BACK,android#安全警告#BACK,com.baidu.appsearch#开启桌面悬浮窗#BACK,com.baidu.appsearch#请开通权限#取消,com.baidu.appsearch#新增炫酷悬浮窗#BACK,com.baidu.appsearch#恭喜你！#BACK,com.baidu.appsearch#马上登录#BACK,com.baidu.appsearch#点击查看#BACK,com.ijinshan.kbatterydoctor#跳过#跳过,com.iqoo.secure#提示#继续,com.lbe.security#运行提醒#BACK,com.mediatek.security#安全警告#允许,com.moxiu.launcher#好，这就去#BACK,com.oppo.secure#程序加密#取消,com.oppo.secure#程序加密#BACK,com.qihoo.cleandroid_cn#立即开启#BACK,com.qihoo360.mobilesafe#重要提示#BACK,com.tencent.qqpimsecure#设置来电秀#进入首页,com.tencent.qqpimsecure#更多#关闭,com.tencent.qqpimsecure#温馨提示#放弃使用,com.tencent.qqpimsecure#未开启广告拦截#BACK,com.tencent.qqpimsecure#警告#确定,com.tencent.qqpimsecure#温馨提示#取消".split(",");
						for (String split2 : split) {
							String[] split3 = split2.split("#");
							list = (List) b.get(split3[0]);
							if (list == null) {
								list = new ArrayList();
								b.put(split3[0], list);
							}
							list.add(split3[1]);
						}
					}
					list = (List) b.get(rootInActiveWindow.getPackageName());
					StringBuilder sb = new StringBuilder();
					if(list != null) {
						for (int i = 0; i < list.size(); i++) {
							sb.append(list.get(i));
						}
					Log.d("10","onAccessibilityEvent满足的字样时候就执行后退"+sb.toString());
					}
					if (list != null && a(rootInActiveWindow, list) != null) {
						performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
					}
				}
			}
		}catch (Throwable e){
			e.printStackTrace();

		}
	}

	public final void onInterrupt() {
	}

	public final void onServiceConnected() {
		super.onServiceConnected();
		Log.d("10","onServiceConnected");
		SettingActivity a = SettingActivity.getInstance();
		if (a != null) {
			performGlobalAction(1);
			a.backActivity(this);
		}
	}




}

