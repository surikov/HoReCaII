package sweetlife.android10.database;

import sweetlife.android10.consts.ITableColumnsNames;
import sweetlife.android10.utils.DateTimeHelper;
import sweetlife.android10.utils.Hex;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class Request_Returns implements ITableColumnsNames {
	
	private static String getRequestString() {
		
		return "select z._id [_id], z._IDRRef [_IDRRef], z.Data [Data]," +
				"z.Nomer [Nomer], z.Proveden [Proveden], z.[Kontragent]," +
				"k.Naimenovanie [KontragentNaimenovanie], z.DataOtgruzki [DataOtgruzki], " +
				"z.[AktPretenziy] [AktPretenziy], k.[Kod] [Kod] "
				       +", z.[_Version] as [_Version] "+
				"from ZayavkaNaVozvrat z " +
				"inner join Kontragenty k on k.[_IDRRef] = z.[Kontragent] ";
	}
	
	public static Cursor Request(SQLiteDatabase db, String client) {

		String sqlString = getRequestString() + "where z.[Kontragent] = " + client +
				" order by z.Nomer DESC";

		return db.rawQuery(sqlString, null);    	
	}
	
	public static Cursor RequestUploaded(SQLiteDatabase db, String dateFrom, String dateTo) {

		String sqlString = getRequestString() + "where " +
				"z.[Proveden] = x'00' and " +
				"date(z.Data) >= date('" + dateFrom + "') and date(z.Data) <= date('" + dateTo + "') " + 
				" order by z._id DESC";
//System.out.println("RequestUploaded "+sqlString);
		//System.out.println(Auxiliary.fromCursor(db.rawQuery(sqlString,null)).dumpXML());
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

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( PROVEDEN ))).compareTo("x'01'") == 0 ? true : false;
	}
	
	public static String getKontragentNaimanovanie( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KONTRAGENT_NAME ));
	}
	
	public static String getAktPretenziyPath( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( AKT_PRETENZIY ));
	}

	public static String getVersion( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( _VERSION ));
	}
	
	public static java.util.Date getDataOtgruzki( Cursor cursor ) {

		return DateTimeHelper.SQLDateToDate(cursor.getString(cursor.getColumnIndex( SHIPPING_DATE )));
	}
	
	public static String getKontragentID( Cursor cursor ) {

		return Hex.encodeHex(cursor.getBlob(cursor.getColumnIndex( KONTRAGENT )));
	}
	
	public static String getKontragentKod( Cursor cursor ) {

		return cursor.getString(cursor.getColumnIndex( KOD ));
	}
}
