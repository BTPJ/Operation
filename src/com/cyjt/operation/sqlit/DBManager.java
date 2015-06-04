package com.cyjt.operation.sqlit;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cyjt.operation.bean.BaseStation;
import com.cyjt.operation.bean.LocBaseStation;
import com.cyjt.operation.bean.ParkingLot;
import com.cyjt.operation.bean.SearchBean;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase database;

	public DBManager(Context context) {
		// ��ΪgetWritableDatabase�ڲ�������mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// ����Ҫȷ��context�ѳ�ʼ��,���ǿ��԰�ʵ����DBManager�Ĳ������Activity��onCreate��
		helper = new DBHelper(context);
	}

	/**
	 * ����
	 * 
	 * @param searchBean
	 */
	public void saveOriSearch(SearchBean searchBean) {
		deleteOriSearchData(searchBean);
		database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("code", searchBean.getCode());
		contentValues.put("time", searchBean.getTime());
		contentValues.put("type", searchBean.getType());
		contentValues.put("gsonString", searchBean.getGsonString());
		database.insert("searchBean", null, contentValues);
	}

	public ArrayList<SearchBean> getOriSearchData(int startResult, int maxResult) {
		ArrayList<SearchBean> searchBeans = new ArrayList<SearchBean>();
		database = helper.getReadableDatabase();
		// "_id desc"����_id�ֶ���������
		Cursor cursor = database.query(DBHelper.TABLE_NAME_SEARCHBEAN,
				new String[] { "code", "time", "type", "gsonString" }, null,
				null, null, null, "_id desc", startResult + "," + maxResult);
		while (cursor.moveToNext()) {
			searchBeans.add(new SearchBean(cursor.getString(0), cursor
					.getLong(1), cursor.getInt(2), cursor.getString(3)));
			Log.v("demo", "cursor.getString(0):"+cursor.getString(0));
		}
		return searchBeans;
	}

	public void deleteOriSearchDatas(ArrayList<SearchBean> searchBeans) {
		for (SearchBean searchBean : searchBeans) {
			deleteOriSearchData(searchBean);
		}
	}

	public void deleteOriSearchData(SearchBean searchBean) {
		database = helper.getReadableDatabase();
		database.delete(DBHelper.TABLE_NAME_SEARCHBEAN, "code=?",
				new String[] { searchBean.getCode() });
	}

	/**
	 * �����ػ�վ���󱣴浽���ݿ�
	 * 
	 * @param bs
	 */
	public void saveOriLocBasestation(LocBaseStation bs) {
		deleteOriLocBasestation(bs);
		database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("code", bs.getCode());
		contentValues.put("description", bs.getDescription());
		contentValues.put("baseStationId", bs.getBaseStationId());
		database.insert(DBHelper.TABLE_NAME_LOC_BASESTATION, null,
				contentValues);
	}

	/**
	 * ȡ��ָ���ı����ڱ������ݿ��еĻ�վ����
	 * 
	 * @param startResult
	 * @param maxResult
	 * @return
	 */
	public ArrayList<LocBaseStation> getOriLocBasestations(int startResult,
			int maxResult) {
		ArrayList<LocBaseStation> locBaseStations = new ArrayList<LocBaseStation>();
		database = helper.getReadableDatabase();
		// "_id desc"����_id�ֶ���������
		Cursor cursor = database.query(DBHelper.TABLE_NAME_LOC_BASESTATION,
				new String[] { "baseStationId", "code", "description" }, null,
				null, null, null, "_id desc", startResult + "," + maxResult);
		while (cursor.moveToNext()) {
			locBaseStations.add(new LocBaseStation(cursor.getLong(0), cursor
					.getString(1), cursor.getString(2)));
		}
		return locBaseStations;
	}

	public void deleteOriLocBasestations(
			ArrayList<LocBaseStation> locBaseStations) {
		for (LocBaseStation lBs : locBaseStations) {
			deleteOriLocSupportBasestation(lBs);
		}
	}

	public void deleteOriLocBasestation(LocBaseStation locBaseStation) {
		database = helper.getReadableDatabase();
		database.delete(DBHelper.TABLE_NAME_LOC_BASESTATION, "code=?",
				new String[] { locBaseStation.getCode() });
	}

	/**
	 * �����ز����վ���󱣴浽���ݿ�
	 * 
	 * @param bs
	 */
	public void saveOriLocSupportBasestation(LocBaseStation bs) {
		// ִ�в����ʱ����ִ��ɾ��,��ȷ�����ݿ���ֻ��һ�����������վ����Ϣ
		deleteOriLocSupportBasestation(bs);
		database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("code", bs.getCode());
		database.insert(DBHelper.TABLE_NAME_LOC_SUPPORT_BASESTATION, null,
				contentValues);
	}

	/**
	 * ȡ��ָ���ı����ڱ������ݿ��еĲ����վ����
	 * 
	 * @param startResult
	 * @param maxResult
	 * @return
	 */
	public ArrayList<LocBaseStation> getOriLocSupportBasestations(
			int startResult, int maxResult) {
		ArrayList<LocBaseStation> locBaseStations = new ArrayList<LocBaseStation>();
		database = helper.getReadableDatabase();
		// "_id desc"����_id�ֶ���������
		Cursor cursor = database.query(
				DBHelper.TABLE_NAME_LOC_SUPPORT_BASESTATION,
				new String[] { "code" }, null, null, null, null, "_id desc",
				startResult + "," + maxResult);
		while (cursor.moveToNext()) {
			locBaseStations
					.add(new LocBaseStation(-1, cursor.getString(0), ""));
		}
		Log.v("demo", "��ȡ��С��" + locBaseStations.size());
		return locBaseStations;
	}

	public void deleteOriLocSupportBasestations(
			ArrayList<LocBaseStation> locBaseStations) {
		for (LocBaseStation lBs : locBaseStations) {
			deleteOriLocSupportBasestation(lBs);
		}
	}

	public void deleteOriLocSupportBasestation(LocBaseStation locBaseStation) {
		database = helper.getReadableDatabase();
		database.delete(DBHelper.TABLE_NAME_LOC_SUPPORT_BASESTATION, "code=?",
				new String[] { locBaseStation.getCode() });
	}

	// ******************************����ʽ��վ�벿���վ�Ĺ�������в���************************
	/**
	 * ��ʱ������ʽ��վ�벿���վ�Ĺ�ϵ
	 * 
	 * @param basestation
	 * @param supportBaseStation
	 */
	public void saveBaseStationMap(BaseStation basestation,
			BaseStation supportBaseStation) {
		database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("basestationCode", basestation.getNfcCode());
		contentValues.put("supportBsCode", supportBaseStation.getNfcCode());
		database.insert(DBHelper.TABLE_NAME_BASESTATION_MAP, null,
				contentValues);
	}

	/**
	 * ��ȡ����ʽ��վ�󶨵Ĳ����վCode
	 * 
	 * @param basestation��ʽ��վ
	 * @return �����վCode
	 */
	public String getBaseStationMap(BaseStation basestation) {
		String supportBaseStationCode = "";
		database = helper.getReadableDatabase();
		// "_id desc"����_id�ֶ���������
		Cursor cursor = database.query(DBHelper.TABLE_NAME_BASESTATION_MAP,
				new String[] { "supportBsCode" }, "basestationCode=?",
				new String[] { basestation.getNfcCode() }, null, null,
				"_id desc", null);
		while (cursor.moveToNext()) {
			supportBaseStationCode = cursor.getString(0);
		}
		return supportBaseStationCode;
	}

	/**
	 * ɾ����ϵ��
	 * 
	 * @param supportBaseStation
	 * @return
	 */
	public int deleteBaseStationMap(BaseStation supportBaseStation) {
		database = helper.getReadableDatabase();
		return database.delete(DBHelper.TABLE_NAME_BASESTATION_MAP,
				"supportBsCode=?",
				new String[] { supportBaseStation.getNfcCode() });
	}

	// *********************************�Բ����վ�Լ����µĳ�λ���в���***************************
	/**
	 * ������ӵĳ�λ��Ϣ
	 * 
	 * @param supportBaseStation
	 *            �����Ĳ����վ
	 * @param parkingLots
	 *            ��λs
	 */
	public void saveParkingLots(BaseStation supportBaseStation,
			ArrayList<ParkingLot> parkingLots) {
		database = helper.getWritableDatabase();
		ContentValues contentValues = null;
		for (ParkingLot pl : parkingLots) {
			contentValues = new ContentValues();
			contentValues.put("supportBsCode", supportBaseStation.getNfcCode());
			contentValues.put("parkingLotCode", pl.getCode());
			database.insert(DBHelper.TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS,
					null, contentValues);
		}
	}

	/**
	 * ��ȡ�����վ�����ڲ���ĳ�λ
	 * 
	 * @param supportBaseStation
	 * @return
	 */
	public ArrayList<ParkingLot> getParkingLots(BaseStation supportBaseStation) {
		ArrayList<ParkingLot> parkingLots = new ArrayList<ParkingLot>();
		database = helper.getReadableDatabase();
		// "_id desc"����_id�ֶ���������
		Cursor cursor = database.query(
				DBHelper.TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS,
				new String[] { "parkingLotCode" }, "supportBsCode = ?",
				new String[] { supportBaseStation.getNfcCode() }, null, null,
				"_id desc", null);
		while (cursor.moveToNext()) {
			parkingLots.add(new ParkingLot(-1, cursor.getString(0), cursor
					.getString(0), cursor.getString(0), "���ڲ���ĳ�λ", false, null,
					new Date(), -1, new Date()));
		}
		return parkingLots;
	}

	public void deleteParkingLots(ArrayList<ParkingLot> parkingLots) {
		database = helper.getReadableDatabase();
		for (ParkingLot pl : parkingLots) {
			database.delete(DBHelper.TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS,
					"parkingLotCode=?", new String[] { pl.getCode() });
		}
	}
}
