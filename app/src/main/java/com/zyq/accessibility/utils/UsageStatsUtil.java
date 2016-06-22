package com.zyq.accessibility.utils;

import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

@TargetApi(21) public class UsageStatsUtil {

	public static final boolean DEBUG = true;
	public static final String TAG = "UsageStatsUtil";

	public static boolean isPackageUsingBeforeOneHour(Context context) {
		try {
			long currentTimeMillis = System.currentTimeMillis();
			List queryUsageStats = ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE)).queryUsageStats(4, currentTimeMillis - 3600000, currentTimeMillis);
			boolean result = (queryUsageStats == null || queryUsageStats.isEmpty()) ? false : true;
			if(DEBUG){
				Log.d(TAG,"isPackageUsingBeforeOneHour:"+result);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getLastOneHourUsingPackages(Context context) {
		UsageEvents queryEvents = ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE)).queryEvents(System.currentTimeMillis() - 3600000, System.currentTimeMillis());
		Object obj = "";
		String str = "";
		while (queryEvents.hasNextEvent()) {
			UsageEvents.Event event = new UsageEvents.Event();
			queryEvents.getNextEvent(event);
			if (event.getEventType() == 1) {
				obj = event.getPackageName();
				str = event.getClassName();
			}
		}
		String result = new StringBuilder(String.valueOf(obj)).append(" ").append(str).toString();
		if(DEBUG){
			Log.d(TAG,"getLastOneHourUsingPackages:"+result);
		}
		return result;
	}
}
