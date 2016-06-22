package com.zyq.accessibility.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.webkit.URLUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

/**
 * @author zyq 16-6-22
 */
public class ParseUtils {

	static final boolean DEBUG = false;

	public static int dbmToRssi(int dbm) {
		return -113 + dbm;
	}

	public static int dpToPx(Context context, int dp) {
		return Math.round(context.getResources().getDisplayMetrics().density * (float) dp);
	}

	public static int spToPx(Context context, int sp) {
		Resources res = context.getResources();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				sp, res.getDisplayMetrics());
	}

	public static int pxToSp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int pxToDp(Context context, int px) {
		Resources res = context.getResources();
		float density = res.getDisplayMetrics().density;
		return (int) (px / density + 0.5f);
	}

	public static String bytesToHexString(@NonNull byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			String x16 = Integer.toHexString(b);
			if (x16.length() < 2) {
				sb.append("0");
				sb.append(x16);
			} else if (x16.length() > 2) {
				sb.append(x16.substring(x16.length() - 2));
			} else {
				sb.append(x16);
			}
		}
		return sb.toString();
	}

	public static byte[] hexStringToBytes(@NonNull String intString) {
		if (TextUtils.isEmpty(intString)) return null;

		if (intString.length() % 2 == 1) {
			intString = "0" + intString;
		}
		byte[] bytes = new byte[intString.length() / 2];

		try {
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) Integer.parseInt(intString.substring(i * 2, i * 2 + 2), 16);
			}
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] htmlToBytes(String url) {
		if (!URLUtil.isNetworkUrl(url)) return null;

		URL htmlUrl;
		InputStream inputStream;
		try {
			htmlUrl = new URL(url);
			HttpURLConnection urlConnection = (HttpURLConnection) htmlUrl.openConnection();
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inputStream = urlConnection.getInputStream();
				return inputStreamToBytes(inputStream);
			} else {
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] inputStreamToBytes(InputStream is) {
		if (is == null) return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int oneByte;
			while ((oneByte = is.read()) != -1) {
				baos.write(oneByte);
			}
		} catch (IOException e) {
		} finally {
			try{
				if(baos != null){
					baos.close();
				}
			}catch (Throwable e){

			}
		}
		return baos.toByteArray();
	}


	public static int stringToInt(String intString) {
		return stringToInt(intString, 0);
	}

	public static int stringToInt(String intString, int defValue) {
		int result;
		try {
			result = Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			result = defValue;
		}
		return result;
	}

	public static Long[] transformLongArray(long[] source) {
		Long[] destin = new Long[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	public static long[] longArrayToLongArray(Long[] source) {
		long[] destin = new long[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	public static Integer[] intArrayToIntegerArray(int[] source) {
		Integer[] destin = new Integer[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	public static int[] integerArrayToIntArray(Integer[] source) {
		int[] destin = new int[source.length];
		for (int i = 0; i < source.length; i++) {
			destin[i] = source[i];
		}
		return destin;
	}

	public static String doubleToString(double number, int precision) {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(precision);
		return format.format(number);
	}

	@SuppressWarnings("unchecked")
	public static <V> int compare(V v1, V v2) {
		return v1 == null ? (v2 == null ? 0 : -1) : (v2 == null ? 1 : ((Comparable) v1).compareTo(v2));
	}

}
