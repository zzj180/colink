/**
 * Copyright (c) 2012-2015 Beijing Unisound Information Technology Co., Ltd. All right reserved.
 * 
 * @FileName : FmCategoryDao.java
 * @ProjectName : uniCarGUI
 * @PakageName : com.unisound.unicar.gui.dao
 * @Author : Xiaodong.He
 * @CreateDate : 2015-09-24
 */
package com.unisound.unicar.gui.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.unisound.unicar.gui.database.DBConstant;
import com.unisound.unicar.gui.database.DBOpenHelper;
import com.unisound.unicar.gui.utils.Logger;
import com.ximalaya.ting.android.opensdk.model.category.CategoryModel;

/**
 * FmCategoryDao
 * 
 * @author Xiaodong.He
 * @date 2015-09-24
 */
public class FmCategoryDao extends BaseDao {

	private static final String TAG = FmCategoryDao.class.getSimpleName();

	private static FmCategoryDao instance = null;

	private DBOpenHelper mDbOpenHelper = null;

	// private SQLiteDatabase mSQLiteDatabase = null;

	private FmCategoryDao(Context context) {
		this.mDbOpenHelper = DBOpenHelper.getInstance(context);
		// this.mDbOpenHelper = new DBOpenHelper(context, DBConstant.DB_NAME);
	}

	public static synchronized FmCategoryDao getInstance(Context context) {
		if (null == instance) {
			instance = new FmCategoryDao(context);
		}
		return instance;
	}

	public SQLiteDatabase getOpenDb() {
		SQLiteDatabase db = null;
		try {
			db = this.mDbOpenHelper.getWritableDatabase();
		} catch (SQLException e) {
			db = this.mDbOpenHelper.getReadableDatabase();
		}
		return db;
	}

	public void close() {
		try {
			// if(null != mSQLiteDatabase && mSQLiteDatabase.isOpen()){
			// mSQLiteDatabase.close();
			// }
			if (null != mDbOpenHelper) {
				mDbOpenHelper.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void add(CategoryModel category) {
		SQLiteDatabase db = getOpenDb();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_ID,
					category.getCategoryId());
			values.put(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME,
					category.getCategoryName());
			db.insert(DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, null, values);
		}
		db.close();
	}

	/**
	 * 
	 * @param categoryList
	 * @param isUpdate
	 *            : true:delete all old data before add
	 */
	public void add(List<CategoryModel> categoryList, boolean isUpdate) {
		if (null == categoryList || categoryList.size() == 0) {
			Logger.e(TAG, "add---category list is null, return.");
			return;
		}
		SQLiteDatabase db = getOpenDb();

		if (isUpdate && db.isOpen()) {
			Logger.d(TAG, "add---new data update, begin delete old data.");
			// XD 20151126 modify
			// String sql = "delete from " +
			// DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY;
			// db.execSQL(sql);
			db.delete(DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, null, null);
		}

		CategoryModel category = null;
		Iterator<CategoryModel> it = categoryList.iterator();
		while (it.hasNext()) {
			category = it.next();
			if (db.isOpen()) {
				ContentValues values = new ContentValues();
				values.put(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_ID,
						category.getCategoryId());
				values.put(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME,
						category.getCategoryName());
				db.insert(DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, null,
						values);
			}
		}
		db.close();
	}

	public void delete(String categoryName) {
		SQLiteDatabase db = getOpenDb();
		if (db.isOpen()) {
			db.execSQL("delete from "
					+ DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY + " where "
					+ DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME + "=?",
					new String[] { categoryName });
		}
		db.close();
	}

	/**
	 * 
	 */
	public void deleteAll() {
		SQLiteDatabase db = getOpenDb();
		if (db.isOpen()) {
			db.execSQL("delete * from "
					+ DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY);
		}
		db.close();
	}

	public void update(CategoryModel category) {
		SQLiteDatabase db = getOpenDb();
		String categoryName = category.getCategoryName();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_ID,
					category.getCategoryId());
			values.put(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME,
					categoryName);
			db.update(DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, values,
					DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME,
					new String[] { categoryName });
		}
		db.close();
	}

	public ArrayList<CategoryModel> getAllEntries() {
		ArrayList<CategoryModel> list = new ArrayList<CategoryModel>();
		SQLiteDatabase db = getOpenDb();
		if (db.isOpen()) {
			CategoryModel category = null;
			Cursor cursor = db.query(
					DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, null, null,
					null, null, null,
					DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_ID);
			while (cursor.moveToNext()) {
				category = cursor2DataItemInfo(cursor);
				list.add(category);
			}
			cursor.close();
			db.close();
			category = null;
		}
		return list;
	}

	public int getCagegoryIdByName(String categoryName) {
		int categoryId = 0;
		Logger.d(TAG, "!--->getCagegoryIdByName---categoryName = "
				+ categoryName);
		if (TextUtils.isEmpty(categoryName)) {
			return categoryId;
		}
		try {
			SQLiteDatabase db = getOpenDb();
			if (db.isOpen()) {
				Cursor cursor = db.query(
						DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, null,
						DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME
								+ " =?", new String[] { categoryName }, null,
						null, null);
				if (cursor.moveToNext()) {
					// categoryId = cursor.getInt(0);
					categoryId = cursor.getInt(cursor.getColumnIndex(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_ID));
				}
				if (null != cursor) {
					cursor.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryId;
	}

	public CategoryModel getCagegoryByName(String cagtegoryName) {
		SQLiteDatabase db = getOpenDb();
		if (db.isOpen()) {
			Cursor cursor = db.query(
					DBConstant.FmCategoryTB.TABLE_NAME_CATEGORY, null,
					DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME + " ="
							+ cagtegoryName, null, null, null, null);
			cursor.moveToNext();
			CategoryModel dataItemInfo = cursor2DataItemInfo(cursor);
			cursor.close();
			return dataItemInfo;
		} else {
			return null;
		}
	}

	private CategoryModel cursor2DataItemInfo(Cursor cursor) {
		CategoryModel itemInfo = new CategoryModel();
		itemInfo.setCategoryId(cursor.getInt(0));
		// category.setCategoryName(cursor.getString(1));
		itemInfo.setCategoryName(cursor.getString(cursor
				.getColumnIndex(DBConstant.FmCategoryTB.TABLE_ITEM_CATEGORY_NAME)));
		return itemInfo;
	}
}
