package sweetlife.android10.database;

import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Request_TrafiksList {
	public static Cursor Request(SQLiteDatabase db, String zayavkaID) {
		String sqlString = "select zpt._id, zpt.[NomerStroki], zpt.[Kolichestvo]," //
				+ "zpt.[Nomenklatura], n.[Artikul], " //
				+ "n.[Naimenovanie] [NomenklaturaNaimenovanie], "//
				//+ "ifnull(vkn.[Kolichestvo], 1) [minNorma], eho.[Naimenovanie] [EdinicaIzmereniyaNaimenovanie], " //
				//+ "ifnull(vkn.[Kolichestvo], 1) [minNorma], n.[otchEdIzm] [EdinicaIzmereniyaNaimenovanie], " //
				+ "ifnull(n.kvant, 1) [minNorma], n.[otchEdIzm] [EdinicaIzmereniyaNaimenovanie], " //
				//+ "ei.[Koephphicient], zpt.[Data], zpt.[Kommentariy]," //
				+ "n.[skladEdKoef], zpt.[Data], zpt.[Kommentariy]," //
				//+ "ei._IDRRef , vs "//
				+ "n._IDRRef , vs "//
				+ "from [ZayavkaPokupatelyaIskhodyaschaya_Traphiki] zpt " //
				+ "inner join [Nomenklatura] n on zpt.[Nomenklatura] = n._IDRRef "//
				//+ "left join VelichinaKvantovNomenklatury vkn on vkn.Nomenklatura = n.[_IDRRef]" //
				//+ "inner join EdinicyIzmereniya ei on n.[EdinicaDlyaOtchetov] = ei._IDRRef "//
				//+ "inner join EdinicyIzmereniya eho on n.EdinicaKhraneniyaOstatkov = eho._IDRRef  "//
				+ "where zpt.[_ZayavkaPokupatelyaIskhodyaschaya_IDRRef] = " + zayavkaID + " group by zpt._id";
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToFirst()) {
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
	public static int getKolichestvo(Cursor cursor) {
		return cursor.getInt(2);
	}
	public static String getNomenklaturaID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(3));
	}
	public static String getArtikul(Cursor cursor) {
		return cursor.getString(4);
	}
	public static String getNomenklaturaNaimenovanie(Cursor cursor) {
		
		return cursor.getString(5);
		
	}
	public static double getMinNorma(Cursor cursor) {
		return cursor.getDouble(6);
	}
	public static String getEdinicaIzmereniya(Cursor cursor) {
		return cursor.getString(7);
	}
	public static double getKoefficientMest(Cursor cursor) {
		return cursor.getDouble(8);
	}
	public static java.util.Date getData(Cursor cursor) {
		return DateTimeHelper.SQLDateToDate(cursor.getString(9));
	}
	public static String getKommentariy(Cursor cursor) {
		return cursor.getString(10);
	}
	public static String getEdinicaIzmereniyaID(Cursor cursor) {
		return Hex.encodeHex(cursor.getBlob(11));
	}
	public static boolean getVS(Cursor cursor) {
		int n = cursor.getInt(12);
		if (n > 0) {
			return true;
		}
		else {
			return false;
		}
	}
}
