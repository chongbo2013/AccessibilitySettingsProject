package com.zyq.accessibility.utils;

/**
 * @author zyq 16-6-22
 */

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.util.Log;


/**
 * @author zyq 16-5-16
 * 针对各个平台的工具
 */
public class PlatformUtil {

	private static String TAG = "platformUtil";
	public static boolean isSamsung(){
		boolean result = "samsung".equals(Build.MANUFACTURER) ? true:false;
		return result;
	}

	/**
	 * 是否允许
	 * @param context
	 * @return
	 */
	public static boolean isCanAlertWindow(Context context){
		if(Build.VERSION.SDK_INT < 19){
			Log.d(TAG,"below API 19 cannot invoke");
			return false;
		}else {
			try {
				Object object = context.getSystemService(Context.APP_OPS_SERVICE);
				//24 对应的是 OP_SYSTEM_ALERT_WINDOW
				int mode = ((AppOpsManager) object).checkOp(String.valueOf(24), Binder.getCallingUid(), context.getPackageName());
				if(mode != 0){//mode = 0表示允许该操作
					return false;
				}
				return true;
			} catch (Throwable e) {
				e.printStackTrace();
				Log.e(TAG,e.toString());
			}
		}
		return false;
	}

	public static boolean b(){
		boolean v0 = false;
		try{
			String v1_1 = System.getProperty("ro.build.version.emui","unknow").trim();
			if(v1_1 == null){
				return v0;
			}
			if(v1_1.equals("unknow")){
				return v0;
			}
			int v2 = v1_1.indexOf(95);
			if(v2==-1){
				return v0;
			}

			v1_1 = v1_1.substring(v2+1,v1_1.length());
			new StringBuilder(String.valueOf(v1_1)).append(",index = ").append(v2);
			if(((double)Float.parseFloat(v1_1))<2){
				return v0;
			}
		}catch (Throwable e){
			e.printStackTrace();
			return v0;
		}
		return true;
	}

	public static boolean isMeizu(){
		return Build.BRAND.toLowerCase().contains("meizu");
	}

	public static boolean isXiaomi(){
		boolean v0 = true;
		String v1 = Build.DISPLAY;
		if(v1 == null || !v1.toUpperCase().contains("MIUI")){
			v1 = Build.MODEL;
			if(v1!=null && (v1.contains("MI-ONE"))){
				return v0;
			}
			v1 = Build.DEVICE;
			if(v1!= null && (v1.contains("mione"))){
				return v0;
			}
			v1 = Build.MANUFACTURER;
			if(v1!= null && (v1.contains("Xiaomi"))){
				return v0;
			}
			v1 = Build.PRODUCT;
			if(v1!= null && (v1.contains("mione"))){
				return v0;
			}

			if(k()){
				return v0;
			}
			v0 = false;
		}
		return v0;
	}

	public static boolean k(){
		String v0 = System.getProperty("ro.miui.ui.version.name","unknown");
		boolean v0_1 = (v0.equalsIgnoreCase("V5")) || (v0.equalsIgnoreCase("V6")) || (v0.equalsIgnoreCase("v7")) ? true:false;
		return v0_1;
	}

	public static boolean isXiaolaijiao(){
		try{
			String brand = Build.BRAND;
			String model = Build.MODEL;
			if(brand == null){
				return  false;
			}
			if(!brand.toLowerCase().equalsIgnoreCase("yusun")){
				return false;
			}
			if(model == null){
				return  false;
			}
			if(!model.toLowerCase().contains("la4")){
				return false;
			}
		}catch (Throwable e){
			return false;
		}
		return true;
	}

	public static boolean isHuawei(){
		try{
			String brand = Build.BRAND;
			String model = Build.MODEL;
			if(brand == null){
				return  false;
			}

			if(!brand.toLowerCase().equalsIgnoreCase("huawei")){
				return false;
			}
			if(model == null){
				return false;
			}
			if(!model.toLowerCase().contains("p6")){
				return false;
			}
		}catch (Throwable e){
			return  false;
		}
		return  true;
	}

	public static boolean isOppo(){
		try{
			String v0 = System.getProperty("ro.build.version.opporom");
			if(v0 == null){
				return false;
			}
			if(!v0.toLowerCase().contains("v2.".toLowerCase())){
				return false;
			}
		}catch (Throwable e){
			return  false;
		}
		return  true;
	}

	public static boolean isYunOs(){
		try{
			String v0 = System.getProperty("ro.sys.vendor");
			if(v0 == null){
				return false;
			}
			if(!v0.toLowerCase().contains("yunos")){
				return false;
			}
		}catch (Throwable e){
			return  false;
		}
		return  true;
	}

	public static boolean isFlyMe(){
		try{
			String v0 = System.getProperty("ro.build.user");
			if(v0 == null){
				return false;
			}
			if(!v0.toLowerCase().contains("flyme")){
				return false;
			}
		}catch (Throwable e){
			return  false;
		}
		return  true;
	}


	public static boolean isMiuiV6() {
		boolean v0 = System.getProperty("ro.miui.ui.version.name", "unkonw").equalsIgnoreCase("V6")
				? true : false;
		return v0;
	}

	public static boolean isMiuiV567(){
		String str = SystemUtils.getSystemProperty("ro.miui.ui.version.name");
		return  str.equalsIgnoreCase("V5")||str.equalsIgnoreCase("V6")||str.equalsIgnoreCase("V7");
	}


	public static boolean l() {
		boolean v0 = true;
		try {
			if(Build.MANUFACTURER.toLowerCase().contains("huawei")) {
				return v0;
			}

			if(Build.BRAND.toLowerCase().contains("huawei")) {
				return v0;
			}

			if(Build.MODEL.toLowerCase().contains("huawei")) {
				return v0;
			}
		}
		catch(Exception v0_1) {
		}

		return false;
	}

	public static boolean m() {
		try {
			String v0_1 = Build.BRAND;
			String v1 = Build.MODEL;
			if(v0_1 == null) {
				return false;
			}

			if(!v0_1.toLowerCase().equals("lge")) {
				return false;
			}

			if(v1 == null) {
				return false;
			}

			if(!v1.toLowerCase().contains("lg-h818")) {
				return false;
			}
		}
		catch(Exception v0) {
			return false;
		}

		boolean v0_2 = true;
		return v0_2;
	}

	public static boolean n() {
		try {
			String v0_1 = Build.BRAND;
			String v1 = Build.MODEL;
			if(v0_1 == null) {
				return false;
			}

			if(!v0_1.toLowerCase().equals("gionee")) {
				return false;
			}

			if(v1 == null) {
				return false;
			}

			if(!v1.toLowerCase().contains("f103")) {
				return false;
			}
		}
		catch(Exception v0) {
			return false;
		}

		boolean v0_2 = true;
		return v0_2;
	}

	public static boolean o() {
		try {
			String v0_1 = Build.BRAND;
			String v1 = Build.MODEL;
			if(v0_1 == null) {
				return false;
			}

			if(!v0_1.toLowerCase().equals("huawei")) {
				return false;
			}

			if(v1 == null) {
				return false;
			}

			if(!v1.toLowerCase().contains("h30-c00")) {
				return false;
			}
		}
		catch(Exception v0) {
			return false;
		}

		boolean v0_2 = true;
		return v0_2;
	}

	public static boolean p() {
		boolean v0 = false;
		try {
			if(Build.class.getMethod("hasSmartBar", new Class[0]) == null) {
				return v0;
			}
		}
		catch(Exception v1) {
			return v0;
		}

		return true;
	}

	public static boolean q() {
		try {
			String v0_1 = Build.BRAND;
			String v1 = Build.MODEL;
			if(v0_1 == null) {
				return false;
			}

			if(!v0_1.toLowerCase().equals("hisense")) {
				return false;
			}

			if(v1 == null) {
				return false;
			}

			if(!v1.toLowerCase().contains("hs-x8t")) {
				return false;
			}
		}
		catch(Exception v0) {
			return false;
		}

		boolean v0_2 = true;
		return v0_2;
	}

	public static boolean r() {
		try {
			String v0_1 = Build.BRAND;
			String v1 = Build.MODEL;
			if(v0_1 == null) {
				return false;
			}

			if(!v0_1.toLowerCase().equals("oppo")) {
				return false;
			}

			if(v1 == null) {
				return false;
			}

			if(!v1.toLowerCase().contains("r7")) {
				return false;
			}
		}
		catch(Exception v0) {
			return false;
		}

		boolean v0_2 = true;
		return v0_2;
	}


}

