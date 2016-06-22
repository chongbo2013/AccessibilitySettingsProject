package com.zyq.accessibility.setting.a.temp;

import android.os.Build;


import com.zyq.accessibility.setting.a.SettingsUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author zyq 16-5-18
 */
public class Feature {

	private static final boolean DEBUG = true;
	private static final String TAG = "e";
	private static Map a = new HashMap();
	private String b = null;
	private String c = null;
	private int d = 1;

	public final boolean matched() {
		boolean v0_2;
		String v0_1;
		long v8 = -1;
		long v4 = -1;
		boolean v1 = true;
		boolean v2 = false;
		String v3 = this.b;
		String v0 = (String) Feature.a.get(this.b);
		if(!Feature.a.containsKey(v3)) {
			if("display".equalsIgnoreCase(this.b)) {
				v0_1 = Build.DISPLAY;
//				Log.d("4","display:"+v0_1);
			}
			else if("device".equalsIgnoreCase(this.b)) {
				v0_1 = Build.DEVICE;
//				Log.d("4","device:"+v0_1);
			}
			else if("manufacturer".equalsIgnoreCase(this.b)) {
				v0_1 = Build.MANUFACTURER;
//				Log.d("4","manufacturer:"+v0_1);
			}
			else if("product".equalsIgnoreCase(this.b)) {
				v0_1 = Build.PRODUCT;
//				Log.d("4","product:"+v0_1);
			}
			else if("model".equalsIgnoreCase(this.b)) {
				v0_1 = Build.MODEL;
//				Log.d("4","model:"+v0_1);
			}
			else if("sdk".equalsIgnoreCase(this.b)) {
				v0_1 = String.valueOf(Build.VERSION.SDK_INT);
//				Log.d("4","sdk:"+v0_1);
			}
			else if("brand".equalsIgnoreCase(this.b)) {
				v0_1 = Build.BRAND;
//				Log.d("4","brand:"+v0_1);
			}
			else if(this.b.startsWith("ro.")) {
				v0_1 = SettingsUtil.getPropertyByName(this.b);
//				Log.d("4","pro:"+v0_1);
			}
			else {
				v0_1 = this.b;
//				Log.d("4",this.isAccessibilityEnabled);
			}

			v0_1 = v0_1 == null ? "" : v0_1.toLowerCase(Locale.ENGLISH);
			Feature.a.put(this.b, v0_1);
		}


		String v3_1 = v0;
		if(v3_1 != null && !((String)v3_1).isEmpty()) {
			switch(this.d) {
				case 1: {
//					Log.d("4","any: "+v3_1 + "  target:"+this.c);
					if(v3_1.contains(this.c)){
						return true;
					}
					return false;
				}
				case 2: {
//					Log.d("4","start: "+v3_1 + "  target:"+this.c);
					if(v3_1.startsWith(this.c)){
						return true;
					}
					return false;
				}
				case 3: {
//					Log.d("4","end: "+v3_1 + "  target:"+this.c);
					if(v3_1.endsWith(this.c)){
						return true;
					}
					return false;
				}
				case 4: {
//					Log.d("4","equal: "+v3_1 + "  target:"+this.c);
					if(v3_1.equals(this.c)){
						return true;
					}
					return false;
				}
				case 5: {
					v4 = SettingsUtil.getVersionCodeByString(this.c);
					v8 = Long.valueOf(v3_1);
//					Log.d("4","qe: "+v8 + "  target:"+v4);
					if(v8 >= v4){
						return true;
					}
					return false;
				}
				case 6: {
					v4 = SettingsUtil.getVersionCodeByString(this.c);
					v8 = Long.valueOf(v3_1);
//					Log.d("4","less: "+v8 + "  target:"+v4);
					if(v8 < v4){
						return true;
					}
					return false;
				}
				case 7: {
					v8 = Long.valueOf(v3_1);
					v4 = SettingsUtil.getVersionCodeByString(this.c);
//					Log.d("4","great: "+v8 + "  target:"+v4);
					if(v8>v4){
						return true;
					}
					return false;
				}
			}
		}
//		Log.d("3","not matched");
		return false;
	}


	@com.zyq.accessibility.setting.common.a.b(a = "condition")
	public final void setCondition(String str) {
		String toLowerCase = str.toLowerCase(Locale.ENGLISH);
		if ("any".equals(toLowerCase)) {
			this.d = 1;
		} else if ("left".equals(toLowerCase)) {
			this.d = 2;
		} else if ("right".equals(toLowerCase)) {
			this.d = 3;
		} else if ("equal".equals(toLowerCase)) {
			this.d = 4;
		} else if ("ge".equals(toLowerCase)) {
			this.d = 5;
		} else if ("less".equals(toLowerCase) || "lesserthan".equals(toLowerCase) || "lt".equals(toLowerCase)) {
			this.d = 6;
		} else if ("intent".equals(toLowerCase)) {
			this.d = 8;
		} else if ("greaterthan".equals(toLowerCase) || "gt".equals(toLowerCase)) {
			this.d = 7;
		}
	}

	@com.zyq.accessibility.setting.common.a.b(a = "key")
	public final void setKey(String str) {
//		Log.d("1","key"+str);
		this.b = str.toLowerCase(Locale.ENGLISH);
	}

	@com.zyq.accessibility.setting.common.a.b(a = "value")
	public final void setValue(String str) {
//		Log.d("1","value"+str);
		this.c = str.toLowerCase(Locale.ENGLISH);
	}


}
