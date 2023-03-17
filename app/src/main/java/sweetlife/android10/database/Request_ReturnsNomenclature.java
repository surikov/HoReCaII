package sweetlife.android10.database;

import java.util.Date;

import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_ReturnsNomenclature {

	public static Cursor RequestNomenclature(SQLiteDatabase db, String zayavkaID) {
		
		String sqlString = "select zv._id, zv.[NomerStroki], zv.Nomenklatura, n.Artikul, n.Naimenovanie, " +
				"zv.Kolichestvo, zv.NomerNakladnoy, zv.DataNakladnoy, zv.Prichina, zv.AktPretenziy "+

			"from [ZayavkaNaVozvrat_Tovary] zv " +       
			"inner join [Nomenklatura] n on zv.[Nomenklatura] = n._IDRRef " +      
			"where zv.[_ZayavkaNaVozvrat_IDRRef] = " + zayavkaID;

		Cursor cursorFoodStuffs = db.rawQuery(sqlString, null);

		if( cursorFoodStuffs.moveToFirst() ) {

			return cursorFoodStuffs;
		}

		return null;
	}
	
	public static int get_id(Cursor cursor) {
		
		return cursor.getInt(0);
	}
	
	public static int getNomerStroki(Cursor cursor) {
		
		return cursor.getInt(1);
	}
	
	public static String getNomenklatura(Cursor cursor) {
		
		return Hex.encodeHex(cursor.getBlob(2));
	}
	
	public static String getArtikul(Cursor cursor) {
		
		return cursor.getString(3);
	}
	
	public static String getNomenklaturaNaimenovanie(Cursor cursor) {
		
		return cursor.getString(4);
	}
	
	public static double getKolichestvo(Cursor cursor) {

		return cursor.getDouble(5);
	}
	
	public static String getNomerNakladnoy(Cursor cursor) {
		
		return cursor.getString(6);
	}
	
	public static Date getDataNakladnoy(Cursor cursor) {	
	
		return DateTimeHelper.SQLDateToDate(cursor.getString(7));
	}
	
	public static int getPrichina(Cursor cursor) {
		
		return cursor.getInt(8);
	}
	
	public static String getAktPretenziy(Cursor cursor) {
		
		return cursor.getString(9);
	}
}
