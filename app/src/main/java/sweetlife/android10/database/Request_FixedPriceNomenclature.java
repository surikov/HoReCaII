package sweetlife.android10.database;

import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Request_FixedPriceNomenclature {

	public static Cursor RequestNomenclature(SQLiteDatabase db, String zayavkaID) {
		
		String sqlString = "select z._id, z.[NomerStroki], z.Nomenklatura, n.Artikul, n.Naimenovanie, " +
				"z.Cena, z.Obyazatelstva "+

			"from [ZayavkaNaSkidki_TovaryPhiksCen] z " +       
			"inner join [Nomenklatura] n on z.[Nomenklatura] = n._IDRRef " +      
			"where z.[_ZayavkaNaSkidki_IDRRef] = " + zayavkaID;
//System.out.println(sqlString);
		Cursor cursor = db.rawQuery(sqlString, null);

		if( cursor.moveToFirst() ) {

			return cursor;
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
	
	public static double getCena(Cursor cursor) {

		return cursor.getDouble(5);
	}
	
	public static double getObyazatelstva(Cursor cursor) {

		return cursor.getDouble(6);
	}
}
