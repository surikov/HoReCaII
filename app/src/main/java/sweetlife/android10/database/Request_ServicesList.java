package sweetlife.android10.database;

import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Request_ServicesList {

	public static Cursor Request(SQLiteDatabase db, String zayavkaID) {
		
		String sqlString = "select zpu._id, zpu.[NomerStroki], zpu.[Soderzhanie], zpu.[Kolichestvo]," +
				"zpu.[Cena],  zpu.[Summa], zpu.[Nomenklatura], n.[Artikul], " +
				"n.[Naimenovanie] [NomenklaturaNaimenovanie]" +

				"from [ZayavkaPokupatelyaIskhodyaschaya_Uslugi] zpu "+
				"inner join [Nomenklatura] n on zpu.[Nomenklatura] = n._IDRRef " +      
				"where zpu.[_ZayavkaPokupatelyaIskhodyaschaya_IDRRef] = " + zayavkaID;

		Cursor cursor = db.rawQuery(sqlString, null);

		if( cursor.moveToFirst() ) {

			return cursor;
		}
cursor.close();
		return null;
	}
	
	public static int get_id(Cursor cursor) {
		
		return cursor.getInt(0);
	}
	
	public static int getNomerStroki(Cursor cursor) {
		
		return cursor.getInt(1);
	}

	public static String getSoderganie(Cursor cursor) {
		
		return cursor.getString(2);
	}
	
	public static int getKolichestvo(Cursor cursor) {
		
		return cursor.getInt(3);
	}
	
	public static double getCena(Cursor cursor) {

		return cursor.getDouble(4);
	}
	
	public static double getSumma(Cursor cursor) {

		return cursor.getDouble(5);
	}
	
	public static String getNomenklaturaID(Cursor cursor) {
		
		return Hex.encodeHex(cursor.getBlob(6));
	}
	
	public static String getArtikul(Cursor cursor) {
		
		return cursor.getString(7);
	}
	
	public static String getNomenklaturaNaimenovanie(Cursor cursor) {
		
		return cursor.getString(8);
	}
}
