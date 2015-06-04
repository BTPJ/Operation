package com.cyjt.operation.sqlit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	/** 数据库名 */
	private static final String DATABASE_NAME = "construct.db";
	/** 保存快捷搜索结果用 */
	public static final String TABLE_NAME_SEARCHBEAN = "searchBean";
	/** 保存使用过的正式基站用 */
	public static final String TABLE_NAME_LOC_BASESTATION = "locBasestation";
	/** 保存使用过的部署基站用 */
	public static final String TABLE_NAME_LOC_SUPPORT_BASESTATION = "locSupportBasestation";
	/** 保存基站与部署基站绑定关系用 */
	public static final String TABLE_NAME_BASESTATION_MAP = "basestationMap";
	/** 保存部署基站上添加的车位用 */
	public static final String TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS = "parkingLots";
	/** 数据库版本号 */
	private static final int DATABASE_VERSION = 2;

	/**
	 * Helper类的构造函数
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** 数据库第一次被创建时onCreate会被调用 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createSearchBeanTable(db);
		createLocBasestationTable(db);
		createLocSupportBasestationTable(db);
		createBaseStationMapTable(db);
		createSupportBsParkingLotsTable(db);
	}

	private void createSearchBeanTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME_SEARCHBEAN
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT, time INTEGER, type INTEGER, gsonString TEXT)");
	}

	private void createLocBasestationTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME_LOC_BASESTATION
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, baseStationId INTEGER, code TEXT, description TEXT)");
	}

	private void createLocSupportBasestationTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME_LOC_SUPPORT_BASESTATION
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT)");
	}

	private void createBaseStationMapTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME_BASESTATION_MAP
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, basestationCode TEXT, supportBsCode TEXT)");
	}

	private void createSupportBsParkingLotsTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, supportBsCode TEXT, parkingLotCode TEXT)");
	}

	/** 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE " + TABLE_NAME_SEARCHBEAN
				+ " ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE " + TABLE_NAME_LOC_BASESTATION
				+ " ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE " + TABLE_NAME_LOC_SUPPORT_BASESTATION
				+ " ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE " + TABLE_NAME_BASESTATION_MAP
				+ " ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE " + TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS
				+ " ADD COLUMN other STRING");
	}

	/** 每次成功打开数据库后会首次执行 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);

	}
}
