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
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		helper = new DBHelper(context);
	}

	/**
	 * 保存
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
		// "_id desc"按照_id字段逆序排列
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
	 * 将本地基站对象保存到数据库
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
	 * 取出指定的保存在本地数据库中的基站对象
	 * 
	 * @param startResult
	 * @param maxResult
	 * @return
	 */
	public ArrayList<LocBaseStation> getOriLocBasestations(int startResult,
			int maxResult) {
		ArrayList<LocBaseStation> locBaseStations = new ArrayList<LocBaseStation>();
		database = helper.getReadableDatabase();
		// "_id desc"按照_id字段逆序排列
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
	 * 将本地部署基站对象保存到数据库
	 * 
	 * @param bs
	 */
	public void saveOriLocSupportBasestation(LocBaseStation bs) {
		// 执行插入的时候先执行删除,以确保数据库中只有一条关于这个基站的信息
		deleteOriLocSupportBasestation(bs);
		database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("code", bs.getCode());
		database.insert(DBHelper.TABLE_NAME_LOC_SUPPORT_BASESTATION, null,
				contentValues);
	}

	/**
	 * 取出指定的保存在本地数据库中的部署基站对象
	 * 
	 * @param startResult
	 * @param maxResult
	 * @return
	 */
	public ArrayList<LocBaseStation> getOriLocSupportBasestations(
			int startResult, int maxResult) {
		ArrayList<LocBaseStation> locBaseStations = new ArrayList<LocBaseStation>();
		database = helper.getReadableDatabase();
		// "_id desc"按照_id字段逆序排列
		Cursor cursor = database.query(
				DBHelper.TABLE_NAME_LOC_SUPPORT_BASESTATION,
				new String[] { "code" }, null, null, null, null, "_id desc",
				startResult + "," + maxResult);
		while (cursor.moveToNext()) {
			locBaseStations
					.add(new LocBaseStation(-1, cursor.getString(0), ""));
		}
		Log.v("demo", "读取大小：" + locBaseStations.size());
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

	// ******************************对正式基站与部署基站的关联表进行操作************************
	/**
	 * 暂时保存正式基站与部署基站的关系
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
	 * 获取与正式基站绑定的部署基站Code
	 * 
	 * @param basestation正式基站
	 * @return 部署基站Code
	 */
	public String getBaseStationMap(BaseStation basestation) {
		String supportBaseStationCode = "";
		database = helper.getReadableDatabase();
		// "_id desc"按照_id字段逆序排列
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
	 * 删除关系表
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

	// *********************************对部署基站以及其下的车位进行操作***************************
	/**
	 * 保存添加的车位信息
	 * 
	 * @param supportBaseStation
	 *            关联的部署基站
	 * @param parkingLots
	 *            车位s
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
	 * 获取部署基站下正在部署的车位
	 * 
	 * @param supportBaseStation
	 * @return
	 */
	public ArrayList<ParkingLot> getParkingLots(BaseStation supportBaseStation) {
		ArrayList<ParkingLot> parkingLots = new ArrayList<ParkingLot>();
		database = helper.getReadableDatabase();
		// "_id desc"按照_id字段逆序排列
		Cursor cursor = database.query(
				DBHelper.TABLE_NAME_SUPPORTBASESTATION_PARKINGLOTS,
				new String[] { "parkingLotCode" }, "supportBsCode = ?",
				new String[] { supportBaseStation.getNfcCode() }, null, null,
				"_id desc", null);
		while (cursor.moveToNext()) {
			parkingLots.add(new ParkingLot(-1, cursor.getString(0), cursor
					.getString(0), cursor.getString(0), "正在部署的车位", false, null,
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
