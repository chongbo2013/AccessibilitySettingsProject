package com.zyq.accessibility.setting.common.a;

import android.content.Context;
import android.util.Log;


import com.zyq.accessibility.activity.GlobalConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zyq 16-5-17
 */
public class ConfigParseUtil {

	public static final boolean DEBUG = true;
	public static final String TAG = "config";

	private Map map = new HashMap();
	private Context mContext = null;

	public ConfigParseUtil(Context context) {
		this.mContext = context;
	}

	private void readConfigFile(JSONObject jSONObject, Object obj) {
		try {
			Map map;
			Class cls = obj.getClass();
			Map map2 = (Map) this.map.get(cls);
			if (map2 == null) {
				Map hashMap = new HashMap();
				Method[] declaredMethods = cls.getDeclaredMethods();
				for (Method method : declaredMethods) {
					if (method.isAnnotationPresent(b.class)) {
						hashMap.put(((b) method.getAnnotation(b.class)).a(), method);
					}
				}
				this.map.put(cls, hashMap);
				map = hashMap;
			} else {
				map = map2;
			}
			//包括上面都有
			Iterator keys = jSONObject.keys();
			while (keys.hasNext()) {
				String str = (String) keys.next();
				Method method2 = (Method) map.get(str);
//				if (DEBUG) {
//					Log.d(TAG, "json key:" + str + " method2:" + method2.getName());
//				}
				try {
					if (method2 == null) {

					} else if (jSONObject.get(str) instanceof JSONArray) {
						JSONArray jSONArray = jSONObject.getJSONArray(str);
						for (int i = 0; i < jSONArray.length(); i++) {
							try {
								Class cls2 = method2.getParameterTypes()[0];
								if (String.class.equals(cls2)) {
									method2.invoke(obj, new Object[]{jSONArray.getString(i)});
								} else if (Boolean.TYPE.equals(cls2)) {
									method2.invoke(obj, new Object[]{Boolean.valueOf(jSONArray.getBoolean(i))});
								} else if (Integer.TYPE.equals(cls2)) {
									method2.invoke(obj, new Object[]{Integer.valueOf(jSONArray.getInt(i))});
								} else {
									Object object = cls2.newInstance();
									readConfigFile(jSONArray.getJSONObject(i), object);
									method2.invoke(obj,new Object[]{object});
								}
							} catch (Exception e) {
								if(GlobalConstants.DEBUG) {
									e.printStackTrace();
								}
							}
						}
						continue;
					} else {
						try {
							Class cls3 = method2.getParameterTypes()[0];
							if (String.class.equals(cls3)) {
								method2.invoke(obj, new Object[]{jSONObject.getString(str)});
							} else if (Boolean.TYPE.equals(cls3)) {
								method2.invoke(obj, new Object[]{Boolean.valueOf(jSONObject.getBoolean(str))});
							} else if (Integer.TYPE.equals(cls3)) {
								method2.invoke(obj, new Object[]{Integer.valueOf(jSONObject.getInt(str))});
							} else {
								Object object = cls3.newInstance();
								readConfigFile(jSONObject.getJSONObject(str), object);
								method2.invoke(obj,new Object[]{object});
							}
						} catch (Exception e2) {
							if(GlobalConstants.DEBUG) {
								e2.printStackTrace();
							}
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}catch (Throwable e){
			e.printStackTrace();
		}
	}

	public final void readConfigFile(String str, Object obj) {
		try {
			InputStream open = this.mContext.getAssets().open(str);
			byte[] bArr = new byte[open.available()];
			open.read(bArr);
			open.close();
			if(DEBUG){
				Log.d(TAG,"str 文件内容是:"+String.valueOf(bArr));
			}
			readConfigFile(new JSONObject(new String(bArr, "UTF-8")), obj);
		} catch (Exception e) {
			if(GlobalConstants.DEBUG) {
				e.printStackTrace();
			}
		}
	}
}
