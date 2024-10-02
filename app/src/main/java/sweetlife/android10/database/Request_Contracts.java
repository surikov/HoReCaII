package sweetlife.android10.database;

import java.util.ArrayList;

import sweetlife.android10.data.common.ContractInfo;
import sweetlife.android10.data.contracts.ContractStatus;
import sweetlife.android10.log.LogHelper;
import sweetlife.android10.utils.Hex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_Contracts {
	private ArrayList<ContractInfo> mContractsList;

	public static ArrayList<String> getContractsCodesForAllInRoute(SQLiteDatabase db) {
		String sqlQuery = "select dk.Kod from Cur_MarshrutyAgentov ma " + "inner join DogovoryKontragentov_strip dk on dk.dk.Vladelec = ma.Kontragent ";
		Cursor cursor = db.rawQuery(sqlQuery, null);
		ArrayList<String> codConstractList = new ArrayList<String>();
		if (cursor.moveToFirst()) {
			do {
				codConstractList.add(cursor.getString(0));
			}
			while (cursor.moveToNext());
		}
		return codConstractList;
	}
	public static void updateStatus(SQLiteDatabase db, ArrayList<ContractStatus> contractStatus) {
		db.beginTransactionNonExclusive();
		try {
			for (ContractStatus contract : contractStatus) {
				String sql = "update DogovoryKontragentov SET " + "PometkaUdaleniya = " + contract.getDeletionMark() + " , " + "Zakryt = " + contract.getClose() + " WHERE Kod = '" + contract.getCode() + "'";
				//System.out.println("Request_Contracts.updateStatus " + sql);
				db.execSQL(sql);
			}
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}
	public Request_Contracts(SQLiteDatabase db, String clientID) {
		//System.out.println("Request_Contracts " + clientID);
		int cntr = 0;
		mContractsList = new ArrayList<ContractInfo>();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(RequestString(clientID), null);
			if (cursor.moveToFirst()) {
				do {
					cntr++;
					AddContract(cursor);
				}
				while (cursor.moveToNext());
			}
		}
		finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		if (cntr < 1) {
			LogHelper.debug(this.getClass().getCanonicalName() + " no contracts found!");
		}
		//System.out.println("done Request_Contracts " + clientID);
	}
	public static Cursor Request(SQLiteDatabase db, String clientID) {
		return db.rawQuery(RequestString(clientID), null);
	}
	private static String RequestString(String clientID) {
		String s = "select dk._id, dk.[_IDRRef], dk.[Kod], dk.[Naimenovanie], " //
				+ "ifnull(gd.[Naimenovanie], '') [GruppyDogovorovNaimenovanie], dk.Zakryt "// 
				+ "from DogovoryKontragentov_strip dk "//
				//+ "from DogovoryKontragentov dk "// 
				+ "left join GruppyDogovorov gd on dk.[GruppaDogovorov] = gd.[_IDRRef] "// 
				+ "where dk.[PometkaUdaleniya]=x'00' and  dk.[Vladelec] = "// 
				+ clientID// 
				+ " group by dk.[Kod] order by dk.[Naimenovanie] "//
		;
		System.out.println("RequestString "+clientID+": "+s);
		return s;
	}
	private void AddContract(Cursor cursor) {
		ContractInfo info = new ContractInfo(Hex.encodeHex(cursor.getBlob(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4), (cursor.getBlob(5)[0] == 1 ? true : false));
		//LogHelper.debug(this.getClass().getCanonicalName()+".AddContract "+info.isClosed()+": "+info.getKod()+": "+info.getNaimenovanie());
		mContractsList.add(info);
	}
	public ArrayList<ContractInfo> getContractsList() {
		return mContractsList;
	}
}
