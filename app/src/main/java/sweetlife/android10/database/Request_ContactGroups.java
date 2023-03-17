package sweetlife.android10.database;

import sweetlife.android10.utils.Hex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_ContactGroups {

	public static Cursor Request( SQLiteDatabase db ) {

		String sqlStr = "select gd._id, gd.[_IDRRef], gd.[Naimenovanie] " + 
		"from GruppyDogovorov gd " +
		"inner join DogovoryKontragentov_strip dk on dk.[GruppaDogovorov] = gd.[_IDRRef] " + 
		"inner join Cur_MarshrutyAgentov ma on dk.[Vladelec] = ma.[Kontragent] " +
		"group by gd.[_IDRRef] " +
		"order by gd.[Naimenovanie]";

		return db.rawQuery( sqlStr, null);
	}

	public static String getContactsGroupID( Cursor cursor ) {
		
		return Hex.encodeHex( cursor.getBlob(1) );
	}
	
	public static String getContactsGroupName( Cursor cursor ) {
		
		return cursor.getString(2);
	}
}
