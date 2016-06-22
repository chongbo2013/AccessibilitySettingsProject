package com.zyq.accessibility.utils;

import android.support.annotation.NonNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zyq 16-6-22
 */
public class Reflect {

	private Object mObject;
	private boolean mIsClass;

	private Reflect(Class<?> type) {
		mObject = type;
		mIsClass = true;
	}

	private Reflect(Object object) {
		mObject = object;
		mIsClass = false;
	}

	public static Reflect on(@NonNull String className) throws ReflectException {
		return on(forName(className));
	}

	public static Reflect on(@NonNull String className, @NonNull ClassLoader classLoader) throws ReflectException {
		return on(forName(className, classLoader));
	}

	public static Reflect on(@NonNull Class<?> clazz) {
		return new Reflect(clazz);
	}

	public static Reflect on(@NonNull Object object) {
		return new Reflect(object);
	}

	public Reflect field(String fieldName) throws ReflectException {
		try {
			Field field = getField(fieldName);
			return on(field.get(mObject));
		} catch (Exception e) {
			throw new ReflectException(e);
		}
	}

	public static <T extends AccessibleObject> T accessible(@NonNull T accessible) {
		if (accessible instanceof Member) {
			Member member = (Member) accessible;

			if (Modifier.isPublic(member.getModifiers()) &&
					Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
				return accessible;
			}
		}

		if (!accessible.isAccessible()) {
			accessible.setAccessible(true);
		}
		return accessible;
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) mObject;
	}

	public Reflect set(String fieldName, Object value) throws ReflectException {
		try {
			Field field = getField(fieldName);
			field.set(mObject, unwrap(value));
			return this;
		}
		catch (Exception e) {
			throw new ReflectException(e);
		}
	}

	public <T> T get(String fieldName) throws ReflectException {
		return field(fieldName).get();
	}

	private Field getField(String fieldName) throws ReflectException {
		Class<?> typeClass = getTypeClass();
		try {
			return typeClass.getField(fieldName);
		} catch (NoSuchFieldException e) {
			do {
				try {
					return accessible(typeClass.getDeclaredField(fieldName));
				} catch (NoSuchFieldException ignore) {}

				typeClass = typeClass.getSuperclass();

			} while (typeClass != null);

			throw new ReflectException(e);
		}
	}

	public Map<String, Reflect> getFieldMap() {
		Map<String, Reflect> result = new LinkedHashMap<>();
		Class<?> type = getTypeClass();

		do {
			for (Field field : type.getDeclaredFields()) {
				if (!mIsClass ^ Modifier.isStatic(field.getModifiers())) {
					String name = field.getName();

					if (!result.containsKey(name))
						result.put(name, field(name));
				}
			}

			type = type.getSuperclass();
		} while (type != null);

		return result;
	}

	public Reflect call(String methodName) throws ReflectException {
		return call(methodName, new Object[0]);
	}

	public Reflect call(String methodName, Object... args) throws ReflectException {
		Class<?>[] types = getTypesClassArray(args);

		try {
			Method method = exactMethod(methodName, types);
			return on(method, mObject, args);
		} catch (NoSuchMethodException e) {
			try {
				Method method = similarMethod(methodName, types);
				return on(method, mObject, args);
			} catch (NoSuchMethodException e1) {
				throw new ReflectException(e1);
			}
		}
	}

	private Method exactMethod(String methodName, Class<?>[] types) throws NoSuchMethodException {
		Class<?> type = getTypeClass();

		try {
			return type.getMethod(methodName, types);
		} catch (NoSuchMethodException e) {
			do {
				try {
					return type.getDeclaredMethod(methodName, types);
				} catch (NoSuchMethodException ignore) {}

				type = type.getSuperclass();
			} while (type != null);

			throw new NoSuchMethodException();
		}
	}

	private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
		Class<?> type = getTypeClass();
		for (Method method : type.getMethods()) {
			if (isSimilarSignature(method, name, types)) {
				return method;
			}
		}

		do {
			for (Method method : type.getDeclaredMethods()) {
				if (isSimilarSignature(method, name, types)) {
					return method;
				}
			}

			type = type.getSuperclass();
		} while (type != null);

		throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on getTypeClass " + getTypeClass() + ".");
	}

	private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName, Class<?>[] desiredParamTypes) {
		return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
	}

	public Reflect create() throws ReflectException {
		return create(new Object[0]);
	}

	public Reflect create(Object... args) throws ReflectException {
		Class<?>[] types = getTypesClassArray(args);
		try {
			Constructor<?> constructor = getTypeClass().getDeclaredConstructor(types);
			return on(constructor, args);
		} catch (NoSuchMethodException e) {
			for (Constructor<?> constructor : getTypeClass().getDeclaredConstructors()) {
				if (match(constructor.getParameterTypes(), types)) {
					return on(constructor, args);
				}
			}
			throw new ReflectException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <P> P as(Class<P> proxyType) {
		final boolean isMap = (mObject instanceof Map);
		final InvocationHandler handler = new InvocationHandler() {

			@SuppressWarnings("null")
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				String name = method.getName();

				try {
					return on(mObject).call(name, args).get();
				} catch (ReflectException e) {
					if (isMap) {
						Map<String, Object> map = (Map<String, Object>) mObject;
						int length = (args == null ? 0 : args.length);

						if (length == 0 && name.startsWith("get")) {
							return map.get(property(name.substring(3)));
						}
						else if (length == 0 && name.startsWith("is")) {
							return map.get(property(name.substring(2)));
						}
						else if (length == 1 && name.startsWith("set")) {
							map.put(property(name.substring(3)), args[0]);
							return null;
						}
					}
					throw e;
				}
			}
		};
		return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[]{proxyType}, handler);
	}

	private static String property(String string) {
		int length = string.length();

		if (length == 0) {
			return "";
		} else if (length == 1) {
			return string.toLowerCase();
		} else {
			return string.substring(0, 1).toLowerCase() + string.substring(1);
		}
	}

	private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
		if (declaredTypes.length == actualTypes.length) {
			for (int i = 0; i < actualTypes.length; i++) {
				if (actualTypes[i] == NULL.class)
					continue;

				if (wrapType(declaredTypes[i]).isAssignableFrom(wrapType(actualTypes[i])))
					continue;

				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return mObject.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Reflect) && mObject.equals(((Reflect) obj).get());
	}

	@Override
	public String toString() {
		return mObject.toString();
	}

	private static Reflect on(Constructor<?> constructor, Object... args) throws ReflectException {
		try {
			return on(accessible(constructor).newInstance(args));
		} catch (Exception e) {
			throw new ReflectException(e);
		}
	}

	private static Reflect on(@NonNull Method method, @NonNull Object object, Object... args) throws ReflectException {
		try {
			accessible(method);
			if (method.getReturnType() == void.class) {
				method.invoke(object, args);
				return on(object);
			} else {
				return on(method.invoke(object, args));
			}
		} catch (Exception e) {
			throw new ReflectException(e);
		}
	}
	private static Object unwrap(Object object) {
		if (object instanceof Reflect) {
			return ((Reflect) object).get();
		}
		return object;
	}

	private static Class<?>[] getTypesClassArray(Object... values) {
		if (values == null) {
			return new Class[0];
		}

		Class<?>[] result = new Class[values.length];

		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			result[i] = value == null ? NULL.class : value.getClass();
		}

		return result;
	}

	private static Class<?> forName(String className) throws ReflectException {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			throw new ReflectException(e);
		}
	}

	private static Class<?> forName(String name, ClassLoader classLoader) throws ReflectException {
		try {
			return Class.forName(name, true, classLoader);
		} catch (Exception e) {
			throw new ReflectException(e);
		}
	}

	public Class<?> getTypeClass() {
		if (mIsClass) {
			return (Class<?>) mObject;
		} else {
			return mObject.getClass();
		}
	}

	public static Class<?> wrapType(@NonNull Class<?> type) {
		if (!type.isPrimitive()) return type;

		if (boolean.class == type) {
			return Boolean.class;
		} else if (int.class == type) {
			return Integer.class;
		} else if (long.class == type) {
			return Long.class;
		} else if (short.class == type) {
			return Short.class;
		} else if (byte.class == type) {
			return Byte.class;
		} else if (double.class == type) {
			return Double.class;
		} else if (float.class == type) {
			return Float.class;
		} else if (char.class == type) {
			return Character.class;
		} else if (void.class == type) {
			return Void.class;
		}
		return type;
	}

	private static class NULL {}

	public static class ReflectException extends RuntimeException {

		public ReflectException() {
			super();
		}

		public ReflectException(String detailMessage) {
			super(detailMessage);
		}

		public ReflectException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public ReflectException(Throwable throwable) {
			super(throwable);
		}

	}

	public static Object invokeStaticMethod(String class_name,String method_name,Class[] classType,Object[] parseValues){
		try {
			Class obj_class = Class.forName(class_name);
			Method method = obj_class.getMethod(method_name, classType);
			return method.invoke(null, parseValues);
		}catch (SecurityException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}catch (NoSuchMethodException e){
			e.printStackTrace();
		}catch (InvocationTargetException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	public static Object invokeMethod(String class_name,String method_name,Object obj,Class[] pareType,Object[] parseValues){
		try{
			Class obj_class = Class.forName(class_name);
			Method method = obj_class.getMethod(method_name,pareType);
			return method.invoke(obj,parseValues);
		}catch (SecurityException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}catch (NoSuchMethodException e){
			e.printStackTrace();
		}catch (InvocationTargetException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	public static Object getFieldObject(String class_name,Object object,String fieldName){
		try{
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		}catch (SecurityException e){
			e.printStackTrace();
		}catch (NoSuchFieldException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return null;
	}

	public static Object getStaticFieldObject(String class_name,String fieldName){
		try {
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	public static void setFieldOjbect(String classname, String filedName, Object obj, Object fieldValue){
		try {
			Class obj_class = Class.forName(classname);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			field.set(obj, fieldValue);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setStaticOjbect(String class_name, String filedName, Object fieldValue){
		try {
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			field.set(null, fieldValue);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
