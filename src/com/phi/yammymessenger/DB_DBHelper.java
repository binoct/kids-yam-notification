package com.phi.yammymessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_DBHelper extends SQLiteOpenHelper {
	 
	// 資料庫名稱
	public static final String DATABASE_NAME = "yammy.db";
	// 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
	public static final int VERSION = 1;	
	// 資料庫物件，固定的欄位變數
	private static SQLiteDatabase database;
 
	// 建構子，在一般的應用都不需要修改
	public DB_DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
 
	// 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
	public static SQLiteDatabase getDatabase(Context context) {
		if (database == null || !database.isOpen()) {
			database = new DB_DBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
		}
 
		return database;
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 建立應用程式需要的表格
		db.execSQL(DB_Table.CREATE_TABLE_SETTING);
		db.execSQL(DB_Table.CREATE_TABLE_USER);
		db.execSQL(DB_Table.CREATE_TABLE_MESSAGE);
		db.execSQL(DB_Table.CREATE_TABLE_REPLY);
		db.execSQL(DB_Table.INIT_TABLE_SETTING);
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 刪除原有的表格
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_SETTING);
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_USER);
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_MESSAGE);
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_REPLY);
 
		// 呼叫onCreate建立新版的表格
		onCreate(db);
	}
 
}
