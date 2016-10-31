package com.aispeech.aios.bridge.utils;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;

public class SystemPropertiesProxy {

	/**
	 * @author zzj
	 * @createTime 20150914 根据给定的key返回int类型值.
	 * @param key
	 *            要查询的key
	 * @param def
	 *            默认返回值
	 * @return 返回一个int类型的值, 如果没有发现则返回默认值
	 * @throws IllegalArgumentException
	 *             如果key超过32个字符则抛出该异常
	 */
	@SuppressLint("UseValueOf")
	public static Integer getInt(Context context, String key, int def)
			throws IllegalArgumentException {

		Integer ret = def;

		try {

			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class SystemProperties = cl
					.loadClass("android.os.SystemProperties");

			// 参数类型
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[2];
			paramTypes[0] = String.class;
			paramTypes[1] = int.class;

			@SuppressWarnings("unchecked")
			Method getInt = SystemProperties.getMethod("getInt", paramTypes);

			// 参数
			Object[] params = new Object[2];
			params[0] = new String(key);
			params[1] = new Integer(def);

			ret = (Integer) getInt.invoke(SystemProperties, params);

		} catch (IllegalArgumentException iAE) {
			throw iAE;
		} catch (Exception e) {
			ret = def;
		}

		return ret;

	}

	public static boolean getBoolean(Context context, String key, boolean def)
			throws IllegalArgumentException {

		boolean ret = def;

		try {

			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class SystemProperties = cl
					.loadClass("android.os.SystemProperties");

			// 参数类型
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[2];
			paramTypes[0] = String.class;
			paramTypes[1] = boolean.class;

			@SuppressWarnings("unchecked")
			Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

			// 参数
			Object[] params = new Object[2];
			params[0] = new String(key);
			params[1] = new Boolean(def);

			ret = (Boolean) getBoolean.invoke(SystemProperties, params);

		} catch (IllegalArgumentException iAE) {
			throw iAE;
		} catch (Exception e) {
			ret = def;
		}

		return ret;

	}

	/**
	 * 根据给定Key获取值.
	 * 
	 * @return 如果不存在该key则返回空字符串
	 * @throws IllegalArgumentException
	 *             如果key超过32个字符则抛出该异常
	 */
	public static String get(Context context, String key)
			throws IllegalArgumentException {

		String ret = "";

		try {
			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class SystemProperties = cl.loadClass("android.os.SystemProperties");

			// 参数类型
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[1];
			paramTypes[0] = String.class;

			@SuppressWarnings("unchecked")
			Method get = SystemProperties.getMethod("get", paramTypes);

			// 参数
			Object[] params = new Object[1];
			params[0] = new String(key);

			ret = (String) get.invoke(SystemProperties, params);

		} catch (IllegalArgumentException iAE) {
			throw iAE;
		} catch (Exception e) {
			ret = "";
		}

		return ret;

	}

	public static String getString(Context context, String key, String def)
			throws IllegalArgumentException {

		String ret = def;

		try {

			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class SystemProperties = cl
					.loadClass("android.os.SystemProperties");

			// 参数类型
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = new Class[1];
			paramTypes[0] = String.class;

			@SuppressWarnings("unchecked")
			Method get = SystemProperties.getMethod("get", paramTypes);

			// 参数
			Object[] params = new Object[1];
			params[0] = new String(key);

			ret = (String) get.invoke(SystemProperties, params);

		} catch (IllegalArgumentException iAE) {
			ret = def;
		} catch (Exception e) {
			ret = def;
		}

		return ret;

	}
}
