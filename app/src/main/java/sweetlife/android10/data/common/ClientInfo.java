package sweetlife.android10.data.common;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClientInfo {

	private String mID;
	private String mKod;
	private String mName;
	private double mLat;
	private double mLon;
	public String dolgMessage;
	public boolean neMarshrut = false;

	private ArrayList<ContractInfo> mContracts;

	public ClientInfo(SQLiteDatabase db, String clientID) {

		ReadClientInfo(db, clientID);

		mContracts = null;
	}

	public ClientInfo(SQLiteDatabase db, String clientID, ArrayList<ContractInfo> contracts) {

		ReadClientInfo(db, clientID);

		mContracts = contracts;
	}

	private void ReadClientInfo(SQLiteDatabase db, String clientID) {

		String sqlStr = "SELECT _IDRRef, Kod, Naimenovanie, GeographicheskayaShirota, " +
				"GeographicheskayaDolgota FROM Kontragenty WHERE _IDRRef = " + clientID;

		Cursor cursor = db.rawQuery(sqlStr, null);

		if (cursor.moveToFirst()) {

			mID = clientID;
			mKod = cursor.getString(1);
			mName = cursor.getString(2);
			mLat = cursor.getDouble(3);
			mLon = cursor.getDouble(4);
		}
		if (cursor != null) {
			cursor.close();
		}
		setNeMarshrut(db, clientID);
	}

	void setNeMarshrut(SQLiteDatabase db, String clientID) {
		String sql = "select kontragent from marshrutyagentov where kontragent=" + clientID;
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {neMarshrut = false;} else {
			neMarshrut = true;
		}
		if (cursor != null) {
			cursor.close();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		if (mContracts != null) {

			mContracts.clear();
		}
	}

	public String getID() {

		return mID;
	}

	public String getKod() {

		return mKod;
	}

	public String getName() {

		return mName;
	}

	public double getLat() {

		return mLat;
	}

	public double getLon() {

		return mLon;
	}

	public ArrayList<ContractInfo> getContracts() {

		return mContracts;
	}

	public int getContractsCount() {

		return mContracts.size();
	}

	public ContractInfo getContract(int index) {

		return mContracts.get(index);
	}

}
