package com.zyq.accessibility.setting.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.zyq.accessibility.R;
import com.zyq.accessibility.activity.SettingActivity;
import com.zyq.accessibility.setting.a.ServiceConfigUtil;
import com.zyq.accessibility.setting.a.SettingsUtil;
import com.zyq.accessibility.utils.PlatformUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author zyq 16-5-18
 */
public class WindowViewManager {

	public static int l = 0;
	private static final WindowManager.LayoutParams a(int i, int i2) {
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.systemUiVisibility = 1;
		if (i <= 0) {
			layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		} else if (ServiceConfigUtil.getInstance().hasRomReature("huawei_em2+")) {
			layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		} else {
			layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		}
		if (ServiceConfigUtil.getInstance().hasRomReature("funtouch(5.0+)")
				|| com.zyq.accessibility.setting.a.ServiceConfigUtil.getInstance().hasRomReature("oppo_coloros_v2.1")) {
			layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
		}
		layoutParams.gravity = 48;
		layoutParams.format = 1;
		layoutParams.flags = 264072;
		layoutParams.width = -1;
		layoutParams.height = i;
		layoutParams.y = i2;
		return layoutParams;
	}


	@SuppressLint({"InflateParams"})
	public static final void guideToAccessibilitySetting(Context context) {
		View inflate = ((Activity) context).getLayoutInflater().inflate(R.layout.setting_accessbility_guide, null);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(119, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(inflate);
		toast.show();
	}

	@SuppressLint({"InflateParams"})
	public static final void guideToAlertBanner(Context context, String str) {
		View inflate = ((Activity) context).getLayoutInflater().inflate(R.layout.setting_alert_guide_banner, null);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(119, 0, 0);
		((TextView) inflate.findViewById(R.id.alert_text)).setText(str);
		toast.setView(inflate);
		toast.show();
	}

	public static final void guideToSettingYunos(Context context, String str) {
		View inflate = ((Activity) context).getLayoutInflater().inflate(R.layout.guide_setting_yunos, null);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(55, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		((TextView) inflate.findViewById(R.id.alert_text)).setText(str);
		toast.setView(inflate);
		toast.show();
	}

	public static final void addView(WindowManager windowManager, View view) {
		addView(windowManager, view, a(-1, 0));
	}

	public static final void addView(WindowManager windowManager, View view, int i) {
		b(windowManager, view, i, 0);
	}

	public static final void addView(WindowManager windowManager, View view, int i, int i2) {
		WindowManager.LayoutParams a = a(i, i2);
		a.flags = 1976;
		addView(windowManager, view, a);
	}

	private static final void addView(WindowManager windowManager, View view, WindowManager.LayoutParams layoutParams) {
		try {
			windowManager.addView(view, layoutParams);
		} catch (Exception e) {
			WindowViewManager.class.getSimpleName();
		}
	}

	public static final void guideToSettingManual(Context context) {
		View inflate = ((Activity) context).getLayoutInflater().inflate(R.layout.guide_setting_manual, null);
		Toast toast = new Toast(context.getApplicationContext());
		toast.setGravity(119, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(inflate);
		toast.show();
	}


	public static final void removeView(WindowManager windowManager, View view) {
		if (view != null) {
			try {
				windowManager.removeView(view);
			} catch (Exception e) {
				WindowViewManager.class.getSimpleName();
			}
		}
	}

	public static final void b(WindowManager windowManager, View view, int i, int i2) {
		addView(windowManager, view, a(i, i2));
	}

	/**
	 * 在activity.onAttachToWindow()时候调用
	 *
	 * @param activity
	 */
	public static void drawSystemBarBackgrounds(Activity activity) {
		if (Build.VERSION.SDK_INT >= 21 && !Build.BRAND.toLowerCase().contains("huawei")) {
			try {
				Window window = activity.getWindow();
				WindowManager.LayoutParams attributes = window.getAttributes();
				attributes.systemUiVisibility |= 1792;
				window.clearFlags(201326592);
				window.addFlags(WindowManager.LayoutParams.class.getField("FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS").getInt(null));
				Method declaredMethod = Window.class.getDeclaredMethod("setStatusBarColor", new Class[]{Integer.TYPE});
				Method declaredMethod2 = Window.class.getDeclaredMethod("setNavigationBarColor", new Class[]{Integer.TYPE});
				declaredMethod.invoke(window, new Object[]{Integer.valueOf(0)});
				declaredMethod2.invoke(window, new Object[]{Integer.valueOf(0)});
			} catch (NoSuchFieldException e) {
			} catch (NoSuchMethodException e2) {
			} catch (IllegalAccessException e3) {
			} catch (IllegalArgumentException e4) {
			} catch (InvocationTargetException e5) {
			}
		}
	}

	public static int getStatusBarHeight(Context context) {
		int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		return identifier > 0 ? context.getResources().getDimensionPixelSize(identifier) : 0;
	}


	public static  WindowManager.LayoutParams getWindowLayoutParams(Context context) {
		int v2_1;
		int v6 = 20;
		int v5 = -1;
		int v4 = -3;
		int v0 = 0;
		WindowManager.LayoutParams v3 = new WindowManager.LayoutParams();
		if(isWindowUsable(SettingActivity.getInstance().getWindowManager())) {
			DisplayMetrics v1 = new DisplayMetrics();
			SettingActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(v1);
			v3.y = 0;
			v3.width = Math.min(v1.widthPixels, v1.heightPixels);
			v3.height = Math.max(v1.widthPixels, v1.heightPixels) +l;
			v3.gravity = 48;
			v3.flags = 155714048;
			v3.type = WindowManager.LayoutParams.TYPE_TOAST;
			v3.softInputMode = 16;
			v3.screenOrientation = 5;
			v3.format = v4;
			return v3;
		}

		v3.width = v5;
		v3.height = v5;
		v3.format = v4;
		v3.screenOrientation = 1;
		if(!PlatformUtil.isOppo()) {
			v0 = 1;
		}

		if(!SettingsUtil.hasClassInApk(context, "com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity")
				) {
			if(SettingsUtil.hasClassInApk(context, "com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity")
					) {
				v2_1 = 1;
			}else {
				v2_1 = 0;
			}
		}
		else {
			v2_1 = 1;
		}
		if(v2_1 == 0 || (PlatformUtil.isCanAlertWindow(context))) {
			v0 = 1;
		}
		if(v0 != 0) {
			v3.type = WindowManager.LayoutParams.TYPE_PHONE;
		}else {
			if (Build.VERSION.SDK_INT < v6) {
				v3.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
			}

			if (Build.VERSION.SDK_INT >= v6) {
				v3.type = WindowManager.LayoutParams.TYPE_TOAST;
			}
		}
		v3.flags = 21103616;
		if(Build.VERSION.SDK_INT >= 19) {
			v3.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		}
		return v3;
	}

	public static boolean isWindowUsable(WindowManager windowManager){
		boolean result = false;
		if(Build.VERSION.SDK_INT >= 19 && windowManager != null){
			Display display = windowManager.getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getRealMetrics(metrics);
			int height = metrics.heightPixels;
			int width = metrics.widthPixels;
			DisplayMetrics metrics1 = new DisplayMetrics();
			display.getMetrics(metrics1);
			int height1 = metrics1.heightPixels;
			int width1 = metrics1.widthPixels;
			l = height - height1;
			if(width - width1 <= 0 && height -height1 <= 0){
				return false;
			}
			result = true;
		}
		return result;
	}



}
