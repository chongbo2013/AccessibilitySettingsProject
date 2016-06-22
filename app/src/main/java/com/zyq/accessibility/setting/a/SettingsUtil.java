package com.zyq.accessibility.setting.a;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.zyq.accessibility.activity.GlobalConstants;
import com.zyq.accessibility.utils.SystemUtils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zyq 16-5-16
 *
 *
 */
public class SettingsUtil {

	private static final Pattern a = Pattern.compile("(\\d+)(\\.(\\d+))?(\\.(\\d+))?(\\.(\\d+))?");

	public static boolean CheckNotifiServiceValid(Context context) {
		String string = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
		if (TextUtils.isEmpty(string)) {
			return false;
		}
		String[] split = string.split(":");
		for (String unflattenFromString : split) {
			ComponentName unflattenFromString2 = ComponentName.unflattenFromString(unflattenFromString);
			if (unflattenFromString2 != null && unflattenFromString2.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkFloatWindowAllowShow(Context context, Result eVar) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (windowManager == null) {
			return false;
		}

//		if(LockerApplication.f && "3.1.0".equals(getPropertyByName("ro.yunos.version"))){
//			eVar.onResult(false);
//			return  true;
//		}

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		layoutParams.flags = 16;
		layoutParams.width = 0;
		layoutParams.height = 0;
		Handler handler = new Handler();
		View cVar = new ResultView(context, handler, windowManager, eVar);
		windowManager.addView(cVar, layoutParams);
		handler.postDelayed(new WmRemoveRunnable(windowManager, cVar, handler, eVar), 100);
		return true;
	}

	public static final String getPropertyByName(String str) {
		return SystemUtils.getSystemProperty(str);
	}

	public static final long getVersionCodeByString(String str) {
		long j = -1;
		Matcher matcher = a.matcher(str);
		if (matcher.find()) {
			j = 0;
			for (int i = 0; i * 2 < matcher.groupCount(); i++) {
				String group = matcher.group((i * 2) + 1);
				if (group != null) {
					j |= Long.parseLong(group) << ((3 - i) * 16);
				}
			}
		}
		return j;
	}

	public static final boolean hasClassInApk(Context context, String str, String str2) {
		boolean z = false;
		try {
			Context createPackageContext = context.createPackageContext(str, 3);
			if (createPackageContext != null) {
				Class loadClass = createPackageContext.getClassLoader().loadClass(str2);
				if (loadClass != null) {
					z = true;
				}
			}
		} catch (Exception e) {
		}
		return z;
	}

	@TargetApi(19)
	public static boolean isFloatWindowOpen(Context context) {
		boolean z = true;
		if (Build.VERSION.SDK_INT >= 19) {
			return checkOp(context,24) == 0;
		} else {
			//Context.BIND_TREAT_LIKE_ACTIVITY == 13421..但设置后发现不行....
			if ((context.getApplicationInfo().flags & 134217728) == 0) {
				z = false;
			}
			return z;
		}
	}

	private static int checkOp(Context context, int op){
		final int version = Build.VERSION.SDK_INT;
		if (version >= 19){
			Object object = context.getSystemService(Context.APP_OPS_SERVICE);
			Class c = object.getClass();
			try {
				Class[] cArg = new Class[3];
				cArg[0] = int.class;
				cArg[1] = int.class;
				cArg[2] = String.class;
				Method lMethod = c.getDeclaredMethod("checkOp", cArg);
				return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
			} catch(NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public static boolean isOpenForV5(Context context) {
		try {
			//BIND_FOREGROUND_SERVICE_WHILE_AWAKE : 33554432
			boolean z;
			ApplicationInfo applicationInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo;
			if (Build.VERSION.SDK_INT >= 19) {
				String propertyByName = getPropertyByName("ro.build.version.incremental");
				if (!TextUtils.isEmpty(propertyByName)) {
					if (propertyByName.startsWith("KXDCNB") || propertyByName.startsWith("KHHCNB")) {
						z = true;
						return ((z ? 33554432 : 134217728) & applicationInfo.flags) != 0;
					} else if (!propertyByName.startsWith("JLB")) {
						z = getVersionCodeByString(propertyByName) > getVersionCodeByString("4.5.8");
						if (z) {
						}
						if (((z ? 33554432 : 134217728) & applicationInfo.flags) != 0) {
						}
					}
				}
			}
			z = false;
			if (z) {
			}
			if (((z ? 33554432 : 134217728) & applicationInfo.flags) != 0) {
			}
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return false;
	}

	public static final boolean isPackageNameUninstalled(Context context, String str) {
		return isPackageNameUninstalled(context.getPackageManager(), str);
	}

	public static final boolean isPackageNameUninstalled(PackageManager packageManager, String str) {
		try {
			packageManager.getApplicationInfo(str, 8192);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static boolean validateIntent(PackageManager packageManager, Intent intent) {
		boolean result = packageManager.queryIntentActivities(intent,1).size()>0;
//		if(!result){
//			if(intent.getAction().equals("miui.intent.action.APP_PERM_EDITOR")){
//				result = true;
//			}
//		}
		if(GlobalConstants.DEBUG) {
			Log.d("4", "intent.getAction():" + intent.getAction() + " valideIntent:result" + result);
		}
		return result;
	}
}
