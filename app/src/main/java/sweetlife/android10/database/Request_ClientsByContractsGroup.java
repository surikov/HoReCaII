package sweetlife.android10.database;

import java.util.ArrayList;

import sweetlife.android10.utils.Hex;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_ClientsByContractsGroup {

	private ArrayList<String> mClientsList;

	public Request_ClientsByContractsGroup( SQLiteDatabase db, String contactsGroup ) {

		mClientsList = new ArrayList<String>();

		String sqlStr = "select k.[_IDRRef] "+ 
		"from Kontragenty k "+
		"inner join DogovoryKontragentov_strip dk on dk.[Vladelec] = k.[_IDRRef] "+
		"inner join GruppyDogovorov gd on dk.[GruppaDogovorov] = gd.[_IDRRef] "+ 
		"where gd.[_IDRRef]= " + contactsGroup + //x'8304001438C58CB411DB627183F183B0' Жаров
		" group by k.[_IDRRef]";

		Cursor cursor = db.rawQuery( sqlStr, null);

		if( cursor.moveToFirst() ) {

			do {

				mClientsList.add(Hex.encodeHex(cursor.getBlob(0)));
			}
			while( cursor.moveToNext() );
		}
	}

	public ArrayList<String> getClientsList() {

		return mClientsList;
	}
}
