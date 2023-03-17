package sweetlife.android10.database;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Request_FixedPrices implements ITableColumnsNames {

	private static String getRequestString() {

		return "select z._id [_id], z._IDRRef [_IDRRef], z.Data [Data]," +
				"z.Nomer [Nomer], z.Vygruzhen [Vygruzhen], z.[Kontragent] [Kontragent]," +
				"k.Naimenovanie [KontragentNaimenovanie], " +
				"z.VremyaNachalaSkidkiPhiksCen [VremyaNachalaSkidkiPhiksCen], " +
				"z.VremyaOkonchaniyaSkidkiPhiksCen [VremyaOkonchaniyaSkidkiPhiksCen], " +
				"z.[Kommentariy] [Kommentariy], k.[Kod] [Kod] " +
				"from ZayavkaNaSkidki z " +
				"inner join Kontragenty k on k.[_IDRRef] = z.[Kontragent] ";
	}

	public static Cursor Request(SQLiteDatabase db, String client) {

		String sqlString = getRequestString() +	"where z.[Kontragent] = " + 
				client + " order by z.Nomer DESC";

		return db.rawQuery(sqlString, null);    	
	}

	public static Cursor RequestUploaded(SQLiteDatabase db, String dateFrom, String dateTo) {

		String sqlString = getRequestString() + "where z.[Vygruzhen] = x'00' and " +
				"date(z.Data) >= date('" + dateFrom + "') and date(z.Data) <= date('" + dateTo + "') " + 
				" order by z._id DESC";

		return db.rawQuery(sqlString, null);  
	}

	public static int get_id( Cursor cursor ) {

		return cursor.getInt(cursor.getColumnIndex( ID ));
	}

	public static String getIDRRef( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( IDRREF )));
	}

	public static java.util.Date getData( Cursor cursor ) {

		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex( DATA )));
	}

	public static String getNomer( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( NOMER ));
	}

	public static boolean isUploaded( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( VYGRUZHEN ))).compareTo("x'01'") == 0 ? true : false;
	}

	public static String getKontragentNaimanovanie( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KONTRAGENT_NAME ));
	}

	public static String getKommentariy( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KOMMENTARIY ));
	}

	public static java.util.Date getVremyaNachalaSkidkiPhiksCen( Cursor cursor ) {

		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex( VREMYA_NACHALA_SKIDKI_PHIKS_CEN )));
	}

	public static java.util.Date getVremyaOkonchaniyaSkidkiPhiksCen( Cursor cursor ) {

		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex( VREMYA_OKONCHANIYA_SKIDKI_PHIKS_CEN )));
	}
	
	public static String getKontragentID( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( KONTRAGENT )));
	}
	
	public static String getKontragentKod( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KOD ));
	}
}
