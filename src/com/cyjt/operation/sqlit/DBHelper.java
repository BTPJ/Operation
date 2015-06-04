package com.cyjt.operation.sqlit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	/** ���ݿ��� */
	private static final String DATABASE_NAME = "construct.db";
	/** ��������������� */
	public static final String TABLE_NAME_SEARCHBEAN = "searchBean";
	/** ����ʹ�ù�����ʽ��վ�� */
	public static final String TABLE_NAME_LOC_BASESTATION = "locBasestation";
	/** ����ʹ�ù��Ĳ����վ�� */
	public static final String TABLE_NAME_LOC_SUPPORT_BASESTATION = "locSupportBasestation";
	/** �����վ�벿���վ�󶨹�ϵ�� */
	public static final String TABLE_NAME_BASESTATION_MAP = "basestationMap";
	/** ���沿���վ����ӵĳ�λ�� */
	public static final String TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS = "parkingLots";
	/** ���ݿ�汾�� */
	private static final int DATABASE_VERSION = 2;

	/**
	 * Helper��Ĺ��캯��
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		// CursorFactory����Ϊnull,ʹ��Ĭ��ֵ
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/** ���ݿ��һ�α�����ʱonCreate�ᱻ���� */
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

	/** ���DATABASE_VERSIONֵ����Ϊ2,ϵͳ�����������ݿ�汾��ͬ,�������onUpgrade */
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

	/** ÿ�γɹ������ݿ����״�ִ�� */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);

	}
}
